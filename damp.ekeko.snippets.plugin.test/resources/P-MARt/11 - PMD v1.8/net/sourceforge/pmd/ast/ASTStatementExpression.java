/* Generated By:JJTree: Do not edit this line. ASTStatementExpression.java */

package net.sourceforge.pmd.ast;

public class ASTStatementExpression extends SimpleNode {
    public ASTStatementExpression(int id) {
        super(id);
    }

    public ASTStatementExpression(JavaParser p, int id) {
        super(p, id);
    }


    /** Accept the visitor. **/
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
