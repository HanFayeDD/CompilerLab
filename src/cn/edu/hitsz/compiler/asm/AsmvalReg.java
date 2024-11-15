package cn.edu.hitsz.compiler.asm;

public class AsmvalReg implements Asmvalue{
    private Reg reg;
    public AsmvalReg(Reg reg){
        this.reg = reg;
    }
    @Override
    public String toString() {
        return reg.toString();
    }
    public static void main(String[] args) {
        AsmvalReg asmvalReg = new AsmvalReg(Reg.t0);
        System.out.println(asmvalReg);
    }
}
