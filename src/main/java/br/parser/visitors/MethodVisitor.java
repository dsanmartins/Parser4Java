package br.parser.visitors;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class MethodVisitor extends VoidVisitorAdapter<Void> {
	
	List<MethodDeclaration> listMd = new ArrayList<MethodDeclaration>();
	
	@Override
	public void visit(MethodDeclaration n, Void arg) {
		listMd.add(n);
		super.visit(n, arg);
	}
	
	public List<MethodDeclaration> getMethodClasses(CompilationUnit cu){
		cu.accept(this, null);
		return listMd;
	}
}
