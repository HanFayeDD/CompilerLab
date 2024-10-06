package cn.edu.hitsz.compiler.lexer;

import cn.edu.hitsz.compiler.NotImplementedException;
import cn.edu.hitsz.compiler.symtab.SymbolTable;
import cn.edu.hitsz.compiler.utils.FilePathConfig;
import cn.edu.hitsz.compiler.utils.FileUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

/**
 * TODO: 实验一: 实现词法分析
 * <br>
 * 你可能需要参考的框架代码如下:
 * 
 * @see Token 词法单元的实现
 * @see TokenKind 词法单元类型的实现
 */
public class LexicalAnalyzer {
    private final SymbolTable symbolTable;
    private List<String> sourceTextlist;
    private List<Token> tokens;
    private static final int START = 0;
    private static final int LETTER = 1;// 字符串
    private static final int DIGIT = 2;// 数字串
    private static final int FUHAO = 3;// 运算符
    private static final int SEP = 4;// 分界符
    private static final Set<String> keywords = new HashSet<>();
    static {
        keywords.add("int");
        keywords.add("return");
    }

    public LexicalAnalyzer(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.sourceTextlist = null;
        this.tokens = new ArrayList<>();
    }

    /**
     * 从给予的路径中读取并加载文件内容
     *
     * @param path 路径
     */
    public void loadFile(String path) {
        // TODO: 词法分析前的缓冲区实现
        this.sourceTextlist = FileUtils.readLines(FilePathConfig.SRC_CODE_PATH);
        System.out.println(this.sourceTextlist);
        System.out.println("*******read source file over*******");
    }

    /**
     * 执行词法分析, 准备好用于返回的 token 列表 <br>
     * 需要维护实验一所需的符号表条目, 而得在语法分析中才能确定的符号表条目的成员可以先设置为 null
     */
    public void run() {
        // TODO: 自动机实现的词法分析过程
        var p = new String("result = a * b - ( 3 + b ) * ( c - a );");
        int currentPos = 0;
        int currentState = START;
        StringBuilder currentToken = new StringBuilder();
        while (currentPos < p.length()) {
            char nowchar = p.charAt(currentPos);
            switch (currentState) {
                case START:
                    if (nowchar == ' ') {
                        currentState = START;
                    } else if (Character.isLetter(nowchar)) {
                        currentToken.append(nowchar);
                        currentState = LETTER;
                    } else if (Character.isDigit(nowchar)) {
                        currentToken.append(nowchar);
                        currentState = DIGIT;
                    } else if (nowchar == '+' || nowchar == '-' || nowchar == '*' || nowchar == '/' || nowchar == '=') {
                        addOperatorTotokens(nowchar);
                        currentState = START;
                    } else if (nowchar == ';' || nowchar == ',' || nowchar == '(' || nowchar == ')') {
                        addSepTotokens(nowchar);
                        currentState = START;
                    } else {
                        throw new RuntimeException("未知字符");
                    }
                    currentPos++;
                    break;
                case LETTER:
                    if (nowchar == ' ') {
                        addLetterTotokens(currentToken);
                        currentState = START;
                    } else if (Character.isLetter(nowchar)) {
                        currentToken.append(nowchar);
                        currentState = LETTER;
                    } else if (Character.isDigit(nowchar)) {
                        throw new RuntimeException("在字母后紧跟数字");
                    } else if (nowchar == '+' || nowchar == '-' || nowchar == '*' || nowchar == '/' || nowchar == '=') {
                        addLetterTotokens(currentToken);
                        addOperatorTotokens(nowchar);
                        currentState = START;
                    } else if (nowchar == ',' || nowchar == '(' || nowchar == ')') {
                        addLetterTotokens(currentToken);
                        addSepTotokens(nowchar);
                        currentState = START;
                    } else if(nowchar == ';'){
                        addLetterTotokens(currentToken);
                        addSepTotokens(nowchar);
                        currentState = START;
                    } else {
                        throw new RuntimeException("未知字符");
                    }
                    currentPos++;
                    break;
                case DIGIT:
                    if (nowchar == ' ') {
                        addNumberTotoken(currentToken);
                        currentState = START;
                    } else if (Character.isLetter(nowchar)) {
                        throw new RuntimeException("在数字后紧跟字母");
                    } else if (Character.isDigit(nowchar)) {
                        currentToken.append(nowchar);
                        currentState = DIGIT;
                    } else if (nowchar == '+' || nowchar == '-' || nowchar == '*' || nowchar == '/' || nowchar == '=') {
                        addNumberTotoken(currentToken);
                        addOperatorTotokens(nowchar);
                        currentState = START;
                    } else if (nowchar == ';' || nowchar == ',' || nowchar == '(' || nowchar == ')') {
                        addNumberTotoken(currentToken);
                        addSepTotokens(nowchar);
                        currentState = START;
                    } else if(nowchar==';'){
                        addNumberTotoken(currentToken);
                        addSepTotokens(nowchar);
                        currentState = START;
                    }else {
                        throw new RuntimeException("未知字符");
                    }
                    currentPos++;
                    break;
                default:
                    break;
            }
        }
        System.out.println(tokens);

    }

    private void addLetterTotokens(StringBuilder currentToken) {
        String temp = currentToken.toString();
        if (keywords.contains(temp)) {// 是关键字
            tokens.add(Token.simple(temp));
        } else {// 不是关键字
            tokens.add(Token.normal("id", temp));
        }
        currentToken.setLength(0);// 清空字符串
    }

    private void addNumberTotoken(StringBuilder currentToken) {
        String temp = currentToken.toString();
        tokens.add(Token.normal("IntConst", temp));
        currentToken.setLength(0);// 清空字符串
    }

    private void addOperatorTotokens(char op) {
        tokens.add(Token.simple(Character.toString(op)));
    }

    private void addSepTotokens(char sepsym) {
        String temp = "";
        if (sepsym == ';') {
            temp = "Semicolon";
        } else {
            temp = Character.toString(sepsym);
        }
        tokens.add(Token.simple(temp));
    }

    /**
     * 获得词法分析的结果, 保证在调用了 run 方法之后调用
     *
     * @return Token 列表
     */
    public Iterable<Token> getTokens() {
        // TODO: 从词法分析过程中获取 Token 列表
        // 词法分析过程可以使用 Stream 或 Iterator 实现按需分析
        // 亦可以直接分析完整个文件
        // 总之实现过程能转化为一列表即可
        return tokens;
    }

    public void dumpTokens(String path) {
        FileUtils.writeLines(
                path,
                StreamSupport.stream(getTokens().spliterator(), false).map(Token::toString).toList());
    }

    public static void main(String[] args) {
        TokenKind.loadTokenKinds();
        final var symbolTable = new SymbolTable();

        // 词法分析
        final var lexer = new LexicalAnalyzer(symbolTable);
        lexer.run();
    }
}
