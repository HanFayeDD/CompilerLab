package cn.edu.hitsz.compiler.ir;
public class attr {
    // 标识能不能计算
    public boolean is_computable;
    // 记录符号类型，即符号的值
    public int type;

    public attr(boolean is_computable, int type) {
        this.is_computable = is_computable;
        this.type = type;
    }

    @Override
    public String toString() {
        return "SourceCodeType{" +
                "is_computable=" + is_computable +
                ", type=" + type +
                '}';
    }
}