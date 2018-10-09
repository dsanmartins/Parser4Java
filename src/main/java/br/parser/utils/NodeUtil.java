package br.parser.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import br.parser.logic.Parser;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

public class NodeUtil {

	public String checkModuleType(String sourcePath, String url1, String classCall, String url2, String classCaller) throws IOException {

		Properties p = new Properties();
		InputStream in = Parser.class.getClassLoader().getResourceAsStream("configuration.properties");
		p.load(in);

		url1 = p.getProperty("source") + url1;
		url2 = p.getProperty("source") + url2;
		
		String rtn1 = "";
		String rtn2 = "";

		File file = new File(url1);
		try 
		{
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) 
			{	
				String line = scanner.nextLine().replaceAll("\\s+","");

				if(line.contains("class"+classCall)) 
					rtn1 =  "Class";
				else
					if(line.contains("@interface"+classCall)) 
						rtn1 =  "Annotation";
					else
						if(line.contains("interface"+classCall)) 
							rtn1 =  "Interface";
			}
			scanner.close();
		} catch(FileNotFoundException e) { 
			rtn1 = "Class";
		}


		file = new File(url2);
		try 
		{
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) 
			{	
				String line = scanner.nextLine().replaceAll("\\s+","");

				if(line.contains("class"+classCaller)) 
					rtn2 =  "Class";
				else
					if(line.contains("@interface"+classCaller)) 
						rtn2 =  "Annotation";
					else
						if(line.contains("interface"+classCaller)) 
							rtn2 =  "Interface";
			}
			scanner.close();
		} catch(FileNotFoundException e) { 
			rtn2 = "Class";
		}

		String rtn = rtn1 + "|" + rtn2;
		return rtn;
	}

	public MutableNode getNodeByName(MutableGraph g, String nodeName) {

		List<MutableNode> nodes = new ArrayList<MutableNode>(g.nodes());
		for (MutableNode node : nodes ) {

			if (node.name().toString().equals(nodeName))
				return node;
		}
		return null;
	}

}
