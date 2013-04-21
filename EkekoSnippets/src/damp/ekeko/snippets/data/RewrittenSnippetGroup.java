package damp.ekeko.snippets.data;

import clojure.lang.Keyword;
import clojure.lang.RT;

public class RewrittenSnippetGroup extends SnippetGroup{
	private Object rewriteMap;
	private Object rewriteImportMap;
	
	public RewrittenSnippetGroup(String name) {
		super(name);
		rewriteMap = RT.var("damp.ekeko.snippets.rewrite", "make-rewritemap").invoke();
		rewriteImportMap = RT.var("damp.ekeko.snippets.rewrite", "make-rewritemap").invoke();
	}
	

	/**
	 * 
	 * REWRITE SNIPPET PART
	 */
	
	public Object getRewriteMap() {
		return rewriteMap;
	}
	
	public Object getRewriteSnippet(SnippetGroup sGroup, Object nodeInSnippet) {
		Object snippet = sGroup.getSnippet(nodeInSnippet);
		return RT.var("damp.ekeko.snippets.rewrite","get-rewrite-snippet").invoke(getRewriteMap(), snippet); 		
	}

	public void addRewriteSnippet(SnippetGroup sGroup, Object nodeInSnippet, String code) {
		Object snippet = sGroup.getSnippet(nodeInSnippet);
		Object rewriteSnippet = addSnippetCode(code);
		rewriteMap = RT.var("damp.ekeko.snippets.rewrite","add-rewrite-snippet").invoke(getRewriteMap(), snippet, rewriteSnippet); 		
	}
	
	public void updateRewriteSnippet(SnippetGroup sGroup, Object nodeInSnippet, Object nodeInRewriteSnippet) {
		Object snippet = sGroup.getSnippet(nodeInSnippet);
		Object rewriteSnippet = getSnippet(nodeInRewriteSnippet);
		rewriteMap = RT.var("damp.ekeko.snippets.rewrite","update-rewrite-snippet").invoke(getRewriteMap(), snippet, rewriteSnippet); 		
	}
	
	public Object getRewriteImportMap() {
		return rewriteImportMap;
	}

	public Object getRewriteImportSnippet(SnippetGroup sGroup, Object nodeInSnippet) {
		Object snippet = sGroup.getSnippet(nodeInSnippet);
		return RT.var("damp.ekeko.snippets.rewrite","get-rewrite-snippet").invoke(getRewriteImportMap(), snippet); 		
	}

	public void addRewriteImportSnippet(SnippetGroup sGroup, Object nodeInSnippet, String code) {
		Object snippet = sGroup.getSnippet(nodeInSnippet);
		Object rewriteSnippet = addSnippetCode(code);
		rewriteImportMap = RT.var("damp.ekeko.snippets.rewrite","add-rewrite-snippet").invoke(getRewriteImportMap(), snippet, rewriteSnippet); 		
	}
	
	public void updateRewriteImportSnippet(SnippetGroup sGroup, Object nodeInSnippet, Object nodeInRewriteSnippet) {
		Object snippet = sGroup.getSnippet(nodeInSnippet);
		Object rewriteSnippet = getSnippet(nodeInRewriteSnippet);
		rewriteImportMap = RT.var("damp.ekeko.snippets.rewrite","update-rewrite-snippet").invoke(getRewriteImportMap(), snippet, rewriteSnippet); 		
	}
	
	public void applyOperator(Object operator, SnippetGroup sGroup, Object sNode, Object rwNode, String[] args) {
		Object rwRoot = getRoot(rwNode);
		//special case
		if (operator == Keyword.intern("introduce-logic-variables-for-snippet")) {
			Object snippet = sGroup.getSnippet(sNode);
			setGroupHistory(RT.var("damp.ekeko.snippets.operatorsrep", "apply-operator-to-snippetgrouphistory").invoke(getGroupHistory(), operator, rwNode, new Object[] {snippet}));		
		} else
			applyOperator(operator, rwNode, args, null);
		updateRewriteSnippet(sGroup, sNode, rwRoot);
	}

	public String getTransformationQuery(SnippetGroup snippetGroup) {
		Object query = RT.var("damp.ekeko.snippets.rewrite","snippetgrouphistory-rewrite-query").invoke(snippetGroup.getGroupHistory(), getRewriteMap()); 		
		Object queryImport = RT.var("damp.ekeko.snippets.rewrite","snippetgrouphistory-rewrite-import-declaration-query").invoke(snippetGroup.getGroupHistory(), getRewriteImportMap());
		String result = "";
		if (query != null)
			result += query.toString().replace(") ", ") \n").replace("] ", "] \n");
		if (queryImport != null)
			result += queryImport.toString().replace(") ", ") \n").replace("] ", "] \n");
		return result;
	}

	public void doTransformation(SnippetGroup snippetGroup) {
		RT.var("damp.ekeko.snippets.rewrite","rewrite-query-by-snippetgrouphistory").invoke(snippetGroup.getGroupHistory(), getRewriteMap(), getRewriteImportMap()); 		
		RT.var("damp.ekeko.snippets.rewrite","apply-and-reset-rewrites").invoke(); 		
	}

	
}