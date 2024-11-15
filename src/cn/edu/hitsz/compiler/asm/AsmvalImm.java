package cn.edu.hitsz.compiler.asm;

public class AsmvalImm implements Asmvalue{
    private int value;
    public AsmvalImm(int value){
        this.value = value;
    }
    @Override
    public String toString(){
        return String.valueOf(value);
    }
    public static void main(String[] args) {
        AsmvalImm a = new AsmvalImm(1);
        System.out.println(a.toString());
    }
}
