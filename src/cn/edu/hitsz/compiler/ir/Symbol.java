package cn.edu.hitsz.compiler.ir;

import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.parser.table.NonTerminal;

public class Symbol {
    Token token;
    NonTerminal nonTerminal;
    SourceCodeType sourcecodetype;

    private Symbol(Token token, NonTerminal nonTerminal, SourceCodeType sourcecodetype) {
        this.token = token;
        this.nonTerminal = nonTerminal;
        this.sourcecodetype = sourcecodetype;
    }

    public Symbol(Token token) {
        this(token, null, new SourceCodeType(false, -1));
    }

    public Symbol(NonTerminal nonTerminal) {
        this(null, nonTerminal, new SourceCodeType(false, -1));
    }

    public boolean isToken() {
        return this.token != null;
    }

    public boolean isNonterminal() {
        return this.nonTerminal != null;
    }
}

// 记录符号栈中符号的属性
class SourceCodeType {
    // 标识能不能计算
    boolean is_computable;
    // 记录符号类型，即符号的值
    int type;

    public SourceCodeType(boolean is_computable, int type) {
        this.is_computable = is_computable;
        this.type = type;
    }

}
