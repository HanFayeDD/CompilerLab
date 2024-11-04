package cn.edu.hitsz.compiler.parser;

import javax.lang.model.type.TypeKind;

import cn.edu.hitsz.compiler.NotImplementedException;
import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.lexer.TokenKind;
import cn.edu.hitsz.compiler.parser.table.Production;
import cn.edu.hitsz.compiler.parser.table.Status;
import cn.edu.hitsz.compiler.symtab.SymbolTable;
import cn.edu.hitsz.compiler.ir.*;
import java.util.Stack;

// TODO: 实验三: 实现语义分析
public class SemanticAnalyzer implements ActionObserver {

    public SymbolTable st_in_SA;

    private  Stack<attr> attrstack = new Stack<>();

    @Override
    public void whenAccept(Status currentStatus) {
        // TODO: 该过程在遇到 Accept 时要采取的代码动作

        // throw new NotImplementedException();
    }

    @Override
    public void whenShift(Status currentStatus, Token currentToken) {
        // TODO: 该过程在遇到 shift 时要采取的代码动作
        // 是数字
        if (currentToken.getKindString().equals("IntConst")) {
            var new_temp = new attr(true,
                    Integer.parseInt(currentToken.getTextString()));
            attrstack.push(new_temp);
            System.out.println(new_temp);
        } else {
            attrstack.push(new attr(false, -1));
        }
    }

    @Override
    public void whenReduce(Status currentStatus, Production production) {
        // TODO: 该过程在遇到 reduce production 时要采取的代码动作
        int rightlen = production.body().size();
        int f_attr_val = -1;
        System.out.println(production.index());
        boolean f_attr_computed = false;
        if (production.index() == 7) {
            //????
        }
        // 不知道在干嘛
        // switch (production.index()) {
        //     case 1:
        //         break;
        //     case 2:
        //         break;
        //     case 3:
        //         break;
        //     case 4:
        //         break;
        //     case 5:
        //         break;
        //     case 6:
        //         break;
        //     case 7:
        //         f_attr_computed = true;
        //         f_attr_val = getTopAttr(1);
        //         break;
        //     case 8:
        //         f_attr_computed = true;
        //         f_attr_val = getTopAttr(3) + getTopAttr(1);
        //         break;
        //     case 9:
        //         f_attr_computed = true;
        //         f_attr_val = getTopAttr(3) - getTopAttr(1);
        //     case 10:
        //         f_attr_computed = true;
        //         f_attr_val = getTopAttr(1);
        //         break;
        //     case 11:
        //         f_attr_computed = true;
        //         f_attr_val = getTopAttr(3) * getTopAttr(1);
        //         break;
        //     case 12:
        //         f_attr_computed = true;
        //         f_attr_val = getTopAttr(1);
        //         break;
        //     case 13:
        //         f_attr_computed = true;
        //         f_attr_val = getTopAttr(2);
        //         break;
        //     case 14:
        //         f_attr_computed = true;
        //         f_attr_val = getTopAttr(1);
        //         break;
        //     case 15:
        //         f_attr_computed = true;
        //         f_attr_val = getTopAttr(1);
        // }
        for (int i = 0; i < rightlen; i++) {
            attrstack.pop();
        }
        attrstack.push(new attr(f_attr_computed, f_attr_val));
    }

    @Override
    public void setSymbolTable(SymbolTable table) {
        // TODO: 设计你可能需要的符号表存储结构
        this.st_in_SA = table;
        System.out.println("SymbolTable in SA:");
        st_in_SA.printSymbolTable();
    }

    // 从顶部1开始
    private int getTopAttr(int index) {
        if(index > attrstack.size() || index < 1) {
            throw new RuntimeException("Index out of range");
        }
        var p = attrstack.get(attrstack.size() - index);
        if(p.is_computable==false){
            throw new RuntimeException("cant compute");
        }else{
            return p.type;
        }
    }

    public static void main(String[] args) {
        Stack<Integer> s = new Stack<>();
        s.push(1);
        s.push(2);
        s.push(3);
        s.push(4);
        s.push(5);
        int topindex = 3;//从1开始
        System.out.println(s.get(s.size() - topindex));
    }
}
