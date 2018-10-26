package br.parser.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import br.parser.database.QueryClass;
import br.parser.graph.CreateGraph;
import br.parser.logic.Parser;
import br.parser.utils.LoadLibrary;
import br.parser.xquery.ExecuteXquery;

public class Controller {

	// Initializing Database
	QueryClass ct = new QueryClass();
	JavaParser parser = null;
	String systemSourcePath = "";

	public void initialize() throws Exception {

		ct.createTables();
	}

	public void insertSystem(String systemName, String repoPath, String sourcePath, String jarPath,  String xmlFile) throws Exception
	{

		Path path1 = Paths.get(sourcePath);
		Path path2 = Paths.get(jarPath);
		Path path3= Paths.get(repoPath);

		List<String>  list1= new ArrayList<String>();
		List<String>  list2= new ArrayList<String>();

		String procPath1 = "";
		String procPath2 = "";


		if (Files.exists(path1) && Files.exists(path2) &&  Files.exists(path3)) {

			try (Stream<String> stream = Files.lines(Paths.get(sourcePath))) {

				list1 = stream.collect(Collectors.toList());
				if (list1.size() !=0 ) {
					for (String str : list1)
					{
						procPath1 = str + "," + procPath1;
					}
					procPath1 = procPath1.substring(0,procPath1.length()-1);
				}
				else 
					procPath1 = "";

			} catch (IOException e) {
				e.printStackTrace();
			}

			try (Stream<String> stream = Files.lines(Paths.get(jarPath))) {

				list2 = stream.collect(Collectors.toList());
				if (list2.size() !=0 ) {
					for (String str : list2)
					{
						procPath2 = str + "," + procPath2;
					}
					procPath2 = procPath2.substring(0,procPath2.length()-1);
				}
				else 
					procPath2 = "";

			} catch (IOException e) {
				e.printStackTrace();
			}

			if (procPath1.length() > 0 && procPath2.length() > 0)
			{
				ct.insertConfiguration(systemName, repoPath, procPath1, procPath2, xmlFile, path1.toString(), path2.toString());
			}
		}
	}

	public void updateSystem(String systemName, String repoPath, String sourcePath, String jarPath,  String xmlFile) throws Exception
	{

		Path path1 = Paths.get(sourcePath);
		Path path2 = Paths.get(jarPath);
		Path path3= Paths.get(repoPath);

		List<String>  list1= new ArrayList<String>();
		List<String>  list2= new ArrayList<String>();

		String procPath1 = "";
		String procPath2 = "";


		if (Files.exists(path1) && Files.exists(path2) &&  Files.exists(path3)) {

			try (Stream<String> stream = Files.lines(Paths.get(sourcePath))) {

				list1 = stream.collect(Collectors.toList());
				if (list1.size() !=0 ) {
					for (String str : list1)
					{
						procPath1 = str + "," + procPath1;
					}
					procPath1 = procPath1.substring(0,procPath1.length()-1);
				}
				else 
					procPath1 = "";

			} catch (IOException e) {
				e.printStackTrace();
			}

			try (Stream<String> stream = Files.lines(Paths.get(jarPath))) {

				list2 = stream.collect(Collectors.toList());
				if (list2.size() !=0 ) {
					for (String str : list2)
					{
						procPath2 = str + "," + procPath2;
					}
					procPath2 = procPath2.substring(0,procPath2.length()-1);
				}
				else 
					procPath2 = "";

			} catch (IOException e) {
				e.printStackTrace();
			}

			if (procPath1.length() > 0 && procPath2.length() > 0)
			{
				ct.updateConfiguration(systemName, repoPath, procPath1, procPath2, xmlFile, path1.toString(), path2.toString());
			}
		}
	}

	public void deleteSystem(String systemName) throws Exception
	{
		ct.deleteSystem(systemName);
	}

	public boolean checkSystem(String systemName) throws Exception {

		if (ct.checkSystem(systemName))
			return true;
		else
			return false;
	}

	public List<String> loadSystems() throws Exception{

		return ct.loadSystems();
	}

	public List<String> getConfiguration(String systemName) throws Exception{

		List<String> lst = ct.getConfiguration(systemName);

		return lst;
	}

	public void createPackageCalls(String systemPath) throws Exception {

		CreateGraph createGraph = new CreateGraph(); 
		List<String> lst = ct.getPackageCall();
		createGraph.createGraphWithPackageCall(lst, systemPath);
	}

	public void createMethodCalls(String systemPath, String option, String nodeCall) throws Exception {
		
		CreateGraph createGraph = new CreateGraph();
		int signal = 0;
		List<String> lst = ct.getMethodCall();
		if (nodeCall.equals("Yes"))
			signal = 1;
		else
			signal = 0;
		
		createGraph.createGraphWithMethodCall(parser, systemSourcePath, systemPath,lst,signal, option);
		
	}
	
	public void initializeDB() throws Exception {
		
		ct.createTables();
	}
	
	public String loadConfigurations(String systemName) throws Exception {

	
		List<String> lst = ct.loadConfigurations(systemName);

		CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
		combinedTypeSolver.add(new ReflectionTypeSolver());

		LoadLibrary ll = new LoadLibrary();
		String jarlibs[] = lst.get(0).split("\\,");
		for (String jar: jarlibs)
		{
			List<String> lfu = ll.getListOfAllConfigFiles(jar, "*.jar");
			for (int i = 0; i < lfu.size(); i++)
				combinedTypeSolver.add(new JarTypeSolver(new File(lfu.get(i).toString())));
		}

		String directory[] = lst.get(1).split("\\,");
		for (String str : directory)
			combinedTypeSolver.add(new JavaParserTypeSolver(new File(str)));

		// Configure JavaParser to use type resolution
		ParserConfiguration parserConfiguration = new ParserConfiguration().setSymbolResolver(new JavaSymbolSolver(combinedTypeSolver));
		parser = new JavaParser(parserConfiguration);

		Parser parserClass = new Parser();
		List<String> values = parserClass.getMethodCalls(parser, lst.get(1));
		ct.insertCallsAndCallers(values);

		String systemPath = lst.get(2) + systemName;
		Path dirPathObj = Paths.get(systemPath);
		
		boolean dirExists = Files.exists(dirPathObj);
		if(!dirExists) {

			try 
			{	
				// Creating The New Directory Structure
				Files.createDirectories(dirPathObj);
			} catch (IOException ioExceptionObj) 
			{
				System.out.println("Problem Occured While Creating The Directory Structure= " + ioExceptionObj.getMessage());
			}
		}
		
		systemSourcePath = lst.get(1);
		return systemPath;

	}

	public String getXML(String systemName) throws Exception {
		
		String xml = ct.getXML(systemName);
		return xml;
	}
	
	public void runDynamicAnalysis(String systemName) throws Exception {
		
		List<String> lst = ct.loadConfigurations(systemName);
		String systemPath = lst.get(2) + systemName;
		ExecuteXquery executor = new ExecuteXquery();
		String pathXml = this.getXML(systemName);
		String path[] = pathXml.split("\\/");
		String newPath = "";
		for (int i=0; i<path.length-1; i++)
			newPath = newPath + "/" + path[i];
		
		newPath = newPath.substring(1,newPath.length()) + "/";
		String newFile = path[path.length-1];
		executor.executor(newPath, newFile, "call", systemPath);
		
		CreateGraph createGraph = new CreateGraph(); 
		createGraph.createGraphFromFile(new File(systemPath + "/Dynamic/CSV/"), systemPath);
	}

}
