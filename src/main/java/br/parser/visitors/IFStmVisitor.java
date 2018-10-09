package br.parser.visitors;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class IFStmVisitor extends VoidVisitorAdapter<Void>{

	List<IfStmt> listIfstm = new ArrayList<IfStmt>();
	
	@Override
	public void visit(IfStmt n, Void arg)  {
		listIfstm.add(n);
		super.visit(n, arg);
	}
	
	public List<IfStmt> getIfStm(MethodDeclaration md){
		md.accept(this, null);
		return listIfstm;
	}
	
}
