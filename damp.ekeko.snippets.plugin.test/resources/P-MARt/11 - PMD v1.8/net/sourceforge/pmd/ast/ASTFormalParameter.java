/* Generated By:JJTree: Do not edit this line. ASTFormalParameter.java */

package net.sourceforge.pmd.ast;

public class ASTFormalParameter extends AccessNode {
    public ASTFormalParameter(int id) {
        super(id);
    }

    public ASTFormalParameter(JavaParser p, int id) {
        super(p, id);
    }


    /** Accept the visitor. **/
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public void dump(String prefix) {
        System.out.println(collectDumpedModifiers(prefix));
        dumpChildren(prefix);
    }

}
