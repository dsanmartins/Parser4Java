package br.parser.visitors;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class MethodCallVisitor extends VoidVisitorAdapter<Void>{
	
	List<MethodCallExpr> mc = new ArrayList<MethodCallExpr>();
	
	@Override
	public void visit(MethodCallExpr n, Void arg) {
		mc.add(n);
		super.visit(n, arg);
	}
	
	public List<MethodCallExpr> getMethodCalls(MethodDeclaration md){
		md.accept(this, null);
		return mc;
	}
}
