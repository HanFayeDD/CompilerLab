package cn.edu.hitsz.compiler.parser;

import cn.edu.hitsz.compiler.NotImplementedException;
import cn.edu.hitsz.compiler.ir.Instruction;
import cn.edu.hitsz.compiler.ir.IRValue;
import cn.edu.hitsz.compiler.ir.IRImmediate;
import cn.edu.hitsz.compiler.ir.IRVariable;
import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.lexer.TokenKind;
import cn.edu.hitsz.compiler.parser.table.Production;
import cn.edu.hitsz.compiler.parser.table.Status;
import cn.edu.hitsz.compiler.symtab.SymbolTable;
import cn.edu.hitsz.compiler.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

// TODO: 实验三: 实现 IR 生成

/**
 *
 */
public class IRGenerator implements ActionObserver {

    Stack<Token> tokens = new Stack<>();
    List<Instruction> instructions = new ArrayList<>();

    @Override
    public void whenShift(Status currentStatus, Token currentToken) {
        tokens.push(currentToken);
    }

    @Override
    public void whenReduce(Status currentStatus, Production production) {
        Token id, e, a, b, intconst, ret;
        IRVariable temp;// 生成$1之类的
        IRValue p1, p2;
        switch (production.index()) {
            case 6:// S -> id = E;
                e = tokens.pop();
                tokens.pop();
                id = tokens.pop();
                if (genId(e.getKindString())) {// 说明是E非立即数
                    instructions.add(Instruction.createMov(IRVariable.named(id.getTextString()),
                            IRVariable.named(e.getTextString())));
                } else if (genImm(e.getKindString())) {// 说明是E是立即数
                    instructions.add(Instruction.createMov(IRVariable.named(id.getTextString()),
                            IRImmediate.of(Integer.parseInt(e.getTextString()))));
                } else {
                    throw new RuntimeException("S -> id = E出错");
                }
                break;
            case 7:// S -> return E;
                e = tokens.pop();
                ret = tokens.pop();
                p1 = genL_R(e);
                instructions.add(Instruction.createRet(p1));
                break;
            case 8:// E -> E + A;
                a = tokens.pop();
                tokens.pop();
                e = tokens.pop();
                temp = IRVariable.temp();
                // 判断E和A类型并产生对应中间变量
                p1 = genL_R(e);
                p2 = genL_R(a);
                instructions.add(Instruction.createAdd(temp, p1, p2));
                tokens.push(Token.normal(TokenKind.eof(), temp.toString()));
                break;
            case 9:// E -> E - A;
                a = tokens.pop();
                tokens.pop();
                e = tokens.pop();
                temp = IRVariable.temp();
                // 判断E和A类型并产生对应中间变量
                p1 = genL_R(e);
                p2 = genL_R(a);
                instructions.add(Instruction.createSub(temp, p1, p2));
                tokens.push(Token.normal(TokenKind.eof(), temp.toString()));
                break;
            case 10:// E -> A;
                a = tokens.pop();
                if (a.getKindString().equals("$")) {// A不是生成立即数
                    tokens.push(Token.normal(TokenKind.eof(), a.getTextString()));
                } else if (a.getKindString().equals("IntConst")) {// A是生成立即数
                    tokens.push(Token.normal(a.getKindString(), a.getTextString()));
                } else {
                    throw new RuntimeException("E -> A出错");
                }
                break;
            case 11:// A -> A * B;
                b = tokens.pop();
                tokens.pop();
                a = tokens.pop();
                temp = IRVariable.temp();
                // 判断A和B的类型
                p1 = genL_R(a);
                p2 = genL_R(b);
                instructions.add(Instruction.createMul(temp, p1, p2));
                tokens.push(Token.normal(TokenKind.eof(), temp.toString()));
                break;
            case 12:// A -> B;
                b = tokens.pop();
                if (b.getKindString().equals("$")) {// B不是生成立即数
                    tokens.push(Token.normal(TokenKind.eof(), b.getTextString()));
                } else if (b.getKindString().equals("IntConst")) {// B是生成立即数
                    tokens.push(Token.normal(b.getKindString(), b.getTextString()));
                } else {
                    throw new RuntimeException("A -> B出错");
                }
                break;
            case 13:// B -> ( E );
                tokens.pop();
                e = tokens.pop();
                tokens.pop();
                if (genId(e.getKindString())) {
                    tokens.push(Token.normal(TokenKind.eof(), e.getTextString()));
                } else if (genImm(e.getKindString())) {
                    tokens.push(Token.normal(e.getKindString(), e.getTextString()));
                } else {
                    throw new RuntimeException("B -> ( E )出错");
                }
                break;
            case 14:// B -> id;
                id = tokens.pop();
                tokens.push(Token.normal(TokenKind.eof(), id.getTextString()));
                break;
            case 15:// B -> IntConst;
                intconst = tokens.pop();
                tokens.push(Token.normal(intconst.getKindString(), intconst.getTextString()));
                break;
            default:
                break;
        }
    }

    /**
     * 根据传入token的kind字符描述，来确定是否产生IR变量或IR立即数
     * 产生立即数则返回真
     * 
     * @param kindstring
     * @return
     */
    private boolean genImm(String kindstring) {
        return kindstring.equals("IntConst");
    }

    /**
     * 根据传入token的kind字符描述，来确定是否产生IR变量或IR立即数
     * 产生中间变量则返回真
     * 
     * @param kindstring
     * @return
     */
    private boolean genId(String kindstring) {
        return kindstring.equals("$");
    }

    /**
     * 主要用于对于+ - * 以及return命令的处理
     * 判断上述四个产生式右边的变量类型，产生IR变量或IR立即数
     * 
     * @param t
     * @return
     */
    private IRValue genL_R(Token t) {
        if (genId(t.getKindString())) {// 是产生的变量
            return IRVariable.named(t.getTextString());
        } else if (genImm(t.getKindString())) {// 是立即数
            return IRImmediate.of(Integer.parseInt(t.getTextString()));
        } else {
            throw new RuntimeException("genL_R出错");
        }
    }

    @Override
    public void whenAccept(Status currentStatus) {
        // TODO
        // do nothing
    }

    @Override
    public void setSymbolTable(SymbolTable table) {
        // dothing
        System.out.println("IRGenerator no need to store symbol table");

    }

    public List<Instruction> getIR() {
        return instructions;
    }

    public void dumpIR(String path) {
        FileUtils.writeLines(path, getIR().stream().map(Instruction::toString).toList());
    }
}
