package cn.edu.hitsz.compiler.asm;

import cn.edu.hitsz.compiler.NotImplementedException;
import cn.edu.hitsz.compiler.ir.IRImmediate;
import cn.edu.hitsz.compiler.ir.IRValue;
import cn.edu.hitsz.compiler.ir.IRVariable;
import cn.edu.hitsz.compiler.ir.Instruction;
import cn.edu.hitsz.compiler.ir.InstructionKind;
import cn.edu.hitsz.compiler.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

import javax.print.DocFlavor.STRING;

/**
 * TODO: 实验四: 实现汇编生成
 * <br>
 * 在编译器的整体框架中, 代码生成可以称作后端, 而前面的所有工作都可称为前端.
 * <br>
 * 在前端完成的所有工作中, 都是与目标平台无关的, 而后端的工作为将前端生成的目标平台无关信息
 * 根据目标平台生成汇编代码. 前后端的分离有利于实现编译器面向不同平台生成汇编代码. 由于前后
 * 端分离的原因, 有可能前端生成的中间代码并不符合目标平台的汇编代码特点. 具体到本项目你可以
 * 尝试加入一个方法将中间代码调整为更接近 risc-v 汇编的形式, 这样会有利于汇编代码的生成.
 * <br>
 * 为保证实现上的自由, 框架中并未对后端提供基建, 在具体实现时可自行设计相关数据结构.
 *
 * @see AssemblyGenerator#run() 代码生成与寄存器分配
 */
public class AssemblyGenerator {

    List<Instruction> instructions = new ArrayList<>();
    List<Asmsentence> asm = new ArrayList<>();

    BMap<Reg, String> bMap = new BMap<>();

    private static int cnt = 0;

    private String getcnt() {
        return "##" + cnt++;
    }

    /**
     * 加载前端提供的中间代码
     * <br>
     * 视具体实现而定, 在加载中或加载后会生成一些在代码生成中会用到的信息. 如变量的引用
     * 信息. 这些信息可以通过简单的映射维护, 或者自行增加记录信息的数据结构.
     *
     * @param originInstructions 前端提供的中间代码
     */
    public void loadIR(List<Instruction> originInstructions) {
        // 读入前端提供的中间代码并生成所需要的信息
        // instructions = originInstructions;
        asm.add(Asmsentence.getText());
        System.out.println("before preprocess:" + originInstructions);
        for (var nowp : originInstructions) {
            // 处理二元的且都是立即数
            if (nowp.getKind().isBinary() && isImm(nowp.getOperands()) == 11) {
                var param1 = ((IRImmediate) nowp.getOperands().get(0)).getValue();
                var param2 = ((IRImmediate) nowp.getOperands().get(1)).getValue();
                var result = nowp.getResult();
                switch (nowp.getKind()) {
                    case InstructionKind.ADD:
                        instructions.add(Instruction.createMov(result, IRImmediate.of(param1 + param2)));
                        break;
                    case InstructionKind.SUB:
                        instructions.add(Instruction.createMov(result, IRImmediate.of(param1 - param2)));
                        break;
                    case InstructionKind.MUL:
                        instructions.add(Instruction.createMov(result, IRImmediate.of(param1 * param2)));
                        break;
                    default:
                        break;
                }
            }
            // add 立即数+非立即数
            else if (nowp.getKind() == InstructionKind.ADD && isImm(nowp.getOperands()) == 10) {
                var param1 = ((IRImmediate) nowp.getOperands().get(0));
                var param2 = ((IRVariable) nowp.getOperands().get(1));
                instructions.add(Instruction.createAdd(nowp.getResult(), param2, param1));
            }
            // SUB 非立即数-立即数 可以直接等价为subi
            // SUB 立即数-非立即数
            else if (nowp.getKind() == InstructionKind.SUB && isImm(nowp.getOperands()) == 10) {
                var param1 = ((IRImmediate) nowp.getOperands().get(0));
                var param2 = ((IRVariable) nowp.getOperands().get(1));
                var newvar = IRVariable.named(getcnt());
                instructions.add(Instruction.createMov(newvar, param1));
                instructions.add(Instruction.createSub(nowp.getResult(), newvar, param2));
            } else if (nowp.getKind() == InstructionKind.MUL &&
                    (isImm(nowp.getOperands()) == 10 || isImm(nowp.getOperands()) == 1)) {
                if (isImm(nowp.getOperands()) == 10) {
                    var param1 = ((IRImmediate) nowp.getOperands().get(0));
                    var param2 = ((IRVariable) nowp.getOperands().get(1));
                    var newvar = IRVariable.named(getcnt());
                    instructions.add(Instruction.createMov(newvar, param1));
                    instructions.add(Instruction.createMul(nowp.getResult(), newvar, param2));
                } else {
                    var param1 = ((IRImmediate) nowp.getOperands().get(1));
                    var param2 = ((IRVariable) nowp.getOperands().get(0));
                    var newvar = IRVariable.named(getcnt());
                    instructions.add(Instruction.createMov(newvar, param1));
                    instructions.add(Instruction.createMul(nowp.getResult(), param2, newvar));
                }
            } else if (nowp.getKind() == InstructionKind.RET) {
                instructions.add(nowp);
                break;
            } else {
                instructions.add(nowp);
            }
        }
        System.out.println("after preprocess:" + instructions);
    }

    private int isImm(List<IRValue> operands) {
        if (operands.size() == 1) {
            var p1 = operands.get(0);
            if (p1 instanceof IRImmediate) {
                return 1;
            } else if (p1 instanceof IRVariable) {
                return 0;
            }
        } else if (operands.size() == 2) {
            var p1 = operands.get(0);
            var p2 = operands.get(1);
            if (p1 instanceof IRImmediate && p2 instanceof IRImmediate) {
                return 11;
            } else if ((p1 instanceof IRImmediate) && !(p2 instanceof IRImmediate)) {
                return 10;
            } else if (!(p1 instanceof IRImmediate) && (p2 instanceof IRImmediate)) {
                return 1;
            } else if (!(p1 instanceof IRImmediate) && !(p2 instanceof IRImmediate)) {
                return 0;
            }
            throw new RuntimeException("error");
        }
        return -1;
    }

    /**
     * 执行代码生成.
     * <br>
     * 根据理论课的做法, 在代码生成时同时完成寄存器分配的工作. 若你觉得这样的做法不好,
     * 也可以将寄存器分配和代码生成分开进行.
     * <br>
     * 提示: 寄存器分配中需要的信息较多, 关于全局的与代码生成过程无关的信息建议在代码生
     * 成前完成建立, 与代码生成的过程相关的信息可自行设计数据结构进行记录并动态维护.
     */
    public void run() {
        // TODO: 执行寄存器分配与代码生成
        for (var nowp : instructions) {
            System.out.println(nowp);
            switch (nowp.getKind()) {
                case InstructionKind.ADD:
                    if (isImm(nowp.getOperands()) == 01) {
                        // System.out.println(AsmKind.addi);
                        asm.add(Asmsentence.createaddi(AsmKind.addi, (AsmvalReg)ir2asm(nowp.getResult()),
                                 ir2asm(nowp.getOperands().get(0)), 
                                 ir2asm(nowp.getOperands().get(1))));
                    } else {
                        // System.out.println(AsmKind.add);
                        asm.add(Asmsentence.createadd(AsmKind.add, (AsmvalReg)ir2asm(nowp.getResult()),
                        ir2asm(nowp.getOperands().get(0)), 
                        ir2asm(nowp.getOperands().get(1))));
                    }
                    break;
                case InstructionKind.SUB:
                    if (isImm(nowp.getOperands()) == 01) {
                        // System.out.println(AsmKind.subi);
                        asm.add(Asmsentence.createsubi(AsmKind.subi, (AsmvalReg)ir2asm(nowp.getResult()),
                        ir2asm(nowp.getOperands().get(0)), 
                        ir2asm(nowp.getOperands().get(1))));
                    } else {
                        // System.out.println(AsmKind.sub);
                        asm.add(Asmsentence.createsub(AsmKind.sub, (AsmvalReg)ir2asm(nowp.getResult()),
                        ir2asm(nowp.getOperands().get(0)), 
                        ir2asm(nowp.getOperands().get(1))));
                    }
                    break;
                case InstructionKind.MUL:
                    // System.out.println(AsmKind.mul);
                    asm.add(Asmsentence.createmul(AsmKind.mul, (AsmvalReg)ir2asm(nowp.getResult()),
                    ir2asm(nowp.getOperands().get(0)), 
                    ir2asm(nowp.getOperands().get(1))));
                    break;
                case InstructionKind.MOV:
                    if (isImm(nowp.getOperands()) == 1) {
                        // System.out.println(AsmKind.li);
                        asm.add(Asmsentence.createli(AsmKind.li, (AsmvalReg)ir2asm(nowp.getResult()),ir2asm(nowp.getOperands().get(0))));
                    } else if (isImm(nowp.getOperands()) == 0) {
                        // System.out.println(AsmKind.mv);
                        asm.add(Asmsentence.createmv(AsmKind.mv, (AsmvalReg)ir2asm(nowp.getResult()),ir2asm(nowp.getOperands().get(0))));
                    } else {
                        throw new RuntimeException("error");
                    }
                    break;
                case InstructionKind.RET:
                    // System.out.println(AsmKind.mv);
                    asm.add(Asmsentence.createmv(AsmKind.mv, new AsmvalReg(Reg.a0), ir2asm(nowp.getOperands().get(0))));
                    System.out.println("change to asm"+asm.get(asm.size()-1));

                    return;
                default:
                    break;
            }
            System.out.println("change to asm"+asm.get(asm.size()-1));
        }
    }

    public List<Asmsentence> getAsm(){
        return asm;
    }

    public Asmvalue ir2asm(IRValue irval){
        if(irval instanceof IRVariable){
            return new AsmvalReg(getReg(irval));
        }
        else if(irval instanceof IRImmediate){
            return new AsmvalImm(((IRImmediate) irval).getValue());
        }
        throw new RuntimeException("error in ir2asm");
    }

    /**
     * 输出汇编代码到文件
     *
     * @param path 输出文件路径
     */
    public void dump(String path) {
        FileUtils.writeLines(path, getAsm().stream().map(Asmsentence::toString).toList());
    }


    public Reg getReg(IRValue IRval){
        if(!(IRval instanceof IRVariable)){
            throw new RuntimeException("寄存器分配错误");
        }
        for (var reg :Reg.values()){
            if(!(bMap.containsK(reg))){
                bMap.put(reg, IRval.toString());
                return reg;     
            }
        }
        throw new RuntimeException("寄存器已满");
    }


    public static void main(String[] args) {
        var generator = new AssemblyGenerator();
        var temp = Asmsentence.createmv(AsmKind.mv, new AsmvalReg(Reg.a0), new AsmvalReg(Reg.t0));
        System.out.println(temp);
        var tq =   Asmsentence.createaddi(AsmKind.addi, new AsmvalReg(Reg.t0), new AsmvalReg(Reg.t2), new AsmvalImm(0));
        System.out.println(tq);
        generator.asm.add(Asmsentence.getText());
        generator.asm.add(temp);
        generator.asm.add(tq);
        generator.dump("output.asm");
    }
}
