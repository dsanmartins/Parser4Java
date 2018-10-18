package br.parser.main;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;

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

/**
 * Some code that uses JavaSymbolSolver.
 */
public class MyAnalysis {

	public static void main(String[] args) throws Exception {

		BasicConfigurator.configure();

		Properties p = new Properties();
		InputStream in = MyAnalysis.class.getClassLoader().getResourceAsStream("configuration.properties");
		p.load(in);

		CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
		combinedTypeSolver.add(new ReflectionTypeSolver());

		LoadLibrary ll = new LoadLibrary();
		String jarlibs[] = p.getProperty("jarlibs").split("\\,");
		for (String jar: jarlibs)
		{
			List<String> lfu = ll.getListOfAllConfigFiles(jar, "*.jar");
			for (int i = 0; i < lfu.size(); i++)
				combinedTypeSolver.add(new JarTypeSolver(new File(lfu.get(i).toString())));
		}

		String directory[] = p.getProperty("directories").split("\\,");
		for (String str : directory)
			combinedTypeSolver.add(new JavaParserTypeSolver(new File(str)));

		// Configure JavaParser to use type resolution
		ParserConfiguration parserConfiguration = new ParserConfiguration().setSymbolResolver(new JavaSymbolSolver(combinedTypeSolver));
		JavaParser parser = new JavaParser(parserConfiguration);

		// Initializing Database
		QueryClass ct = new QueryClass();
		ct.createTables();

		Parser parserClass = new Parser();
		CreateGraph createGraph = new CreateGraph(); 
		String sourcePath = p.getProperty("directories");
		String operation = p.getProperty("operation");
		//List<String> values = parserClass.getMethodCalls(parser, sourcePath);
		//ct.insertCallsAndCallers(values);

		//0: Deactivate calls from the same node
		//1: Activate calls from the same node
		//createGraph.createGraphWithMethodCall(parser, sourcePath,ct.getMethodCall(),0, operation);

		//Create graph with calls among packages
		//createGraph.createGraphWithPackageCall(ct.getPackageCall());

		//Create runtime csv file from VisualVM xml
		ExecuteXquery executor = new ExecuteXquery();
		executor.executor(p.getProperty("visualvmfolder"), p.getProperty("visualvmfile"), "call");
		
		//Create execution trace from file. (This file is created manually)
		String pathFileGraph = p.getProperty("pathfilegraph");
		createGraph.createGraphFromFile(pathFileGraph);
	}
}
