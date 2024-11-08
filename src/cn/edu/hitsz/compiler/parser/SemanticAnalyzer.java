package cn.edu.hitsz.compiler.parser;

import javax.lang.model.type.TypeKind;

import cn.edu.hitsz.compiler.NotImplementedException;
import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.lexer.TokenKind;
import cn.edu.hitsz.compiler.parser.table.NonTerminal;
import cn.edu.hitsz.compiler.parser.table.Production;
import cn.edu.hitsz.compiler.parser.table.Status;
import cn.edu.hitsz.compiler.parser.table.Term;
import cn.edu.hitsz.compiler.symtab.SourceCodeType;
import cn.edu.hitsz.compiler.symtab.SymbolTable;
import cn.edu.hitsz.compiler.ir.*;

import java.net.IDN;
import java.util.Stack;

// TODO: 实验三: 实现语义分析
public class SemanticAnalyzer implements ActionObserver {

    public SymbolTable st_in_SA;

    public Stack<Token> tokenStack = new Stack<>();


    @Override
    public void whenAccept(Status currentStatus) {
        // TODO: 该过程在遇到 Accept 时要采取的代码动作
        // do nothing
    }

    @Override
    public void whenShift(Status currentStatus, Token currentToken) {
        // TODO: 该过程在遇到 shift 时要采取的代码动作
        tokenStack.push(currentToken);
    }

    @Override
    public void whenReduce(Status currentStatus, Production production) {
        // TODO: 该过程在遇到 reduce production 时要采取的代码动作
        // System.out.println(tokenStack);

        Token top;
        switch (production.index()) {
            case 5: //D -> int;
                top = tokenStack.pop(); 
                var tokenD = Token.normal(top.getKindId(), "nonterminal D");
                tokenStack.push(tokenD);
                break;
            case 4: //S -> D id;
                var idout = tokenStack.pop();
                var Dout = tokenStack.pop();
                Token tokenS = null;
                tokenStack.push(tokenS);
                setSymbolType(idout.getTextString(), Dout.getKindString());
                break;
            default:
                break;
        }
    
    }

    private void setSymbolType(String var_name, String var_type){
        // System.out.println("set variable type begin");
        if(st_in_SA.get(var_name).getType() != null){
            return;
        }
        var symbol_tb_entery = st_in_SA.get(var_name);
        switch (var_type) {
            case "int":
                // System.out.printf("set variable %s to IntConst\n", var_name);
                symbol_tb_entery.setType(SourceCodeType.Int);
                break;
            default:
                break;
        }
        st_in_SA.printSymbolTable();
        // System.out.println("set variable type end");
    }

    @Override
    public void setSymbolTable(SymbolTable table) {
        // TODO: 设计你可能需要的符号表存储结构
        this.st_in_SA = table;
    }

}
