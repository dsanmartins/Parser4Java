package br.parser.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseStart;
import com.github.javaparser.StreamProvider;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserAnnotationDeclaration;
import com.google.common.base.Strings;

import br.parser.utils.DirExplorer;
import br.parser.visitors.IFStmVisitor;
import br.parser.visitors.MethodCallVisitor;
import br.parser.visitors.MethodVisitor;
import br.parser.visitors.VariableDeclarationVisitor;

public class Parser {


	Map<String, String> listOfCallers = new HashMap<String, String>();

	public Parser() throws IOException{


	}

	public List<String> getMethodCalls(JavaParser parser, String sourcePath) {

		String sPath[] = sourcePath.split("\\,");
		for (String p : sPath)
		{
			File projectDir = new File(p);
			new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {

				System.out.println(path);
				System.out.println(Strings.repeat("=", path.length()));

				try 
				{
					// Parse some code
					CompilationUnit cu = parser.parse(ParseStart.COMPILATION_UNIT,	new StreamProvider(new FileInputStream(file))).getResult().get();

					// Find all the calculations with two sides:
					MethodVisitor md = new MethodVisitor();
					List<MethodDeclaration>  listMd = md.getMethodClasses(cu); 
					for (int i = 0; i< listMd.size(); i ++)
					{
						String methodName = listMd.get(i).getName().toString();
						MethodCallVisitor mc = new MethodCallVisitor();
						List<MethodCallExpr>  listMc = mc.getMethodCalls(listMd.get(i)); 
						for (int j = 0; j< listMc.size(); j ++)
						{					
							try
							{							
								ResolvedMethodDeclaration methodDeclaration = listMc.get(j).resolve();
								if (!methodDeclaration.getQualifiedName().startsWith("java") && 
										!methodDeclaration.getQualifiedName().startsWith("javax") &&
										!methodDeclaration.getQualifiedName().startsWith("org"))
								{
									MessageDigest md5 = MessageDigest.getInstance("MD5");
									String value = path.split("\\.")[0].replaceAll("\\/", "\\.").replaceFirst("\\.", "") + "."+ methodName + "|" + methodDeclaration.getQualifiedName();
									md5.update(value.getBytes(),0,value.length());
									String md5Str = new BigInteger(1,md5.digest()).toString(16);
									listOfCallers.put(md5Str, value);
								}

							}catch(UnsupportedOperationException  ex)
							{
								try
								{
									if (listMc.get(j)
											.getScope()
											.get().calculateResolvedType()
											.asReferenceType()
											.getTypeDeclaration() instanceof JavaParserAnnotationDeclaration)
									{
										String callee = listMc.get(j).getScope().get().calculateResolvedType().describe();

										MessageDigest md5 = MessageDigest.getInstance("MD5");
										String value = path.split("\\.")[0].replaceAll("\\/", "\\.").replaceFirst("\\.", "") + "/"+ methodName + "|" + callee + "." + listMc.get(j).getName();
										md5.update(value.getBytes(),0,value.length());
										String md5Str = new BigInteger(1,md5.digest()).toString(16);
										listOfCallers.put(md5Str, value);

									}
								}catch (NoSuchAlgorithmException e) {
									// TODO Auto-generated catch block
								}catch(UnsupportedOperationException ex2) {
									System.out.println("Caller: "+methodName + " Callee: "+ listMc.get(j).getScope() + " - " + listMc.get(j).getName());
								}catch(NoSuchElementException ex3) {

								}

							}catch(UnsolvedSymbolException ex2) {
								System.out.println("Caller: "+methodName + " Callee: "+ listMc.get(j).getScope() + " - " + listMc.get(j).getName());
							}
							catch(RuntimeException ex3) {
								System.out.println("Caller: "+methodName + " Callee: "+ listMc.get(j).getScope() + " - " + listMc.get(j).getName());
							} catch (NoSuchAlgorithmException e) {
								// TODO Auto-generated catch block

							}
						}
					}
				} 
				catch ( IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}).explore(projectDir);
		}

		List<String> values = new ArrayList<String>(listOfCallers.values());
		Collections.sort(values);
		return values;

	}

	private List<String> getIfStmt(JavaParser parser, String sourcePath, String nodename) throws FileNotFoundException {

		String sPath[] = sourcePath.split("\\,");
		List<String> stringMethodWithIf = new ArrayList<String>();
		File file = null;

		for (String p : sPath)
		{
			file= new File(p + nodename);
			if(file.exists() && !file.isDirectory()) { 

				try 
				{
					// Parse some code
					CompilationUnit cu = parser.parse(ParseStart.COMPILATION_UNIT,	new StreamProvider(new FileInputStream(file))).getResult().get();
					// Find all the calculations with two sides:
					MethodVisitor md = new MethodVisitor();
					List<MethodDeclaration>  listMd = md.getMethodClasses(cu); 
					for (int i = 0; i< listMd.size(); i ++)
					{
						IFStmVisitor ifstmVisitor = new IFStmVisitor();
						List<IfStmt>  listIfstm = ifstmVisitor.getIfStm(listMd.get(i)); 
						if (!listIfstm.isEmpty())
						{
							String method = listMd.get(i).getNameAsString();
							String comparison = "";
							for (int j = 0; j< listIfstm.size(); j ++)
								comparison = comparison + listIfstm.get(j).getCondition().toString() + System.getProperty("line.separator");
							stringMethodWithIf.add(method + "-" + comparison);
						}
					}
				} 
				catch ( IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;
			}
		}
		return stringMethodWithIf;
	}

	private List<String> getVarDeclarationExpr(JavaParser parser, String sourcePath, String nodename) {

		String sPath[] = sourcePath.split("\\,");
		List<String> stringMethodWithIf = new ArrayList<String>();
		File file = null;

		for (String p : sPath)
		{
			file= new File(p + nodename);

			file= new File(p + nodename);
			if(file.exists() && !file.isDirectory()) { 

				try 
				{
					// Parse some code
					CompilationUnit cu = parser.parse(ParseStart.COMPILATION_UNIT,	new StreamProvider(new FileInputStream(file))).getResult().get();
					// Find all the calculations with two sides:
					MethodVisitor md = new MethodVisitor();
					List<MethodDeclaration>  listMd = md.getMethodClasses(cu); 
					for (int i = 0; i< listMd.size(); i ++)
					{
						VariableDeclarationVisitor vDeclarationVisitor = new VariableDeclarationVisitor();
						List<VariableDeclarationExpr>  listVDExpr = vDeclarationVisitor.getVarDeclaration(listMd.get(i)); 
						if (!listVDExpr.isEmpty())
						{
							String method = listMd.get(i).getNameAsString();
							String expr = "";
							for (int j = 0; j< listVDExpr.size(); j ++)
								expr = expr + listVDExpr.get(j).toString() + System.getProperty("line.separator");
							stringMethodWithIf.add(method + "-" + expr);
						}
					}
				} 
				catch ( IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				break;
			}
		}

		return stringMethodWithIf;
	}

	public List<String> operation(String operation, JavaParser parser, String sourcePath, String nodename) throws FileNotFoundException	{

		if (operation.equals("comparison"))
			return this.getIfStmt(parser, sourcePath, nodename);
		if (operation.equals("variable"))
			return this.getVarDeclarationExpr(parser, sourcePath,nodename);
		if (operation.equals("null"))
			return new ArrayList<String>();
		return null;
	}

}
