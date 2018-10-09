package br.parser.visitors;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class VariableDeclarationVisitor extends VoidVisitorAdapter<Void> {

	List<VariableDeclarationExpr> listVd = new ArrayList<VariableDeclarationExpr>();

	@Override
	public void visit(VariableDeclarationExpr n, Void arg) {
		listVd.add(n);
		super.visit(n, arg);
	}

	public List<VariableDeclarationExpr> getVarDeclaration(MethodDeclaration md){
		md.accept(this, null);
		return listVd;
	}
}
