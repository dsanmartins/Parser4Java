package br.parser.graph;

import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.mutNode;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.opencsv.CSVReader;

import br.parser.logic.Parser;
import br.parser.utils.NodeUtil;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizException;
import guru.nidi.graphviz.engine.GraphvizJdkEngine;
import guru.nidi.graphviz.model.Link;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

public class CreateGraph {

	NodeUtil nUtil = null;
	Parser parserClass = null;


	public void createGraphWithMethodCall(JavaParser parser, String sourcePath, List<String> values, int signal, String operation) throws IOException {

		nUtil = new NodeUtil();
		parserClass = new Parser();
		String newPackageCaller = values.get(0).split("\\|")[0].split("\\/")[0];
		String newClassCaller = values.get(0).split("\\|")[0].split("\\/")[1];

		Graphviz.useEngine(new GraphvizJdkEngine());
		MutableGraph g = mutGraph("project").setDirected(true);
		MutableNode nCall = null;
		MutableNode nCaller = null;

		for (int i = 0; i< values.size(); i++)
		{
			String[] call = values.get(i).split("\\|")[0].split("\\/");
			String packageCaller = call[0];
			String classCaller = call[1];
			String methodCaller = call[2];
			String[] caller = values.get(i).split("\\|")[1].split("\\/");
			String packageCalled = caller[0];
			String classCalled = caller[1];
			String methodCalled = caller[2];

			String url1 = packageCaller.replaceAll("\\.", "/") + "/" + classCaller + ".java";
			String url2 = packageCalled.replaceAll("\\.", "/") + "/" + classCalled + ".java";


			//New class name
			if (!newClassCaller.equals(classCaller))
			{	
				List<MutableNode> nodes = new ArrayList<MutableNode>(g.nodes());
				for (MutableNode node : nodes ) 
				{
					String nodename = node.name().toString().substring(node.name().toString().indexOf("[")+1, node.name().toString().length()-1);
					String type = node.name().toString().substring(0, node.name().toString().indexOf("[")+1); 
					String newNodoname = nodename.split("\\.")[nodename.split("\\.").length-1];
					nodename = nodename.replaceAll("\\.", "\\/") + ".java";
					List<String> lStr = parserClass.operation(operation, parser, sourcePath, nodename);
					node.setName(type + newNodoname + "]");
					for (String ifStr : lStr) {

						if (!ifStr.isEmpty()) {
							String methods[] = ifStr.split("\\-");
							MutableNode comparison = mutNode(methods[1]).add(Color.RED,Shape.RECTANGLE, Color.RED.font());
							Link link = Link.between(node, comparison).with(Label.of(methods[0]),Color.RED, Color.RED.font());
							node.addLink(link);
							g.add(node,comparison);
						}
					}
				}

				try
				{
					g.graphAttrs().add(Label.of("Method Calls for :" + newPackageCaller + "." + newClassCaller ));
					Graphviz.fromGraph(g).height(800).width(1280).render(Format.SVG).toFile(new File("figure/"+newClassCaller+".svg"));
					newClassCaller = classCaller;
					newPackageCaller = packageCaller;
					g.graphs().clear();
					g = mutGraph("project").setDirected(true);

				}catch(GraphvizException ex)
				{
					ex.printStackTrace();
					newClassCaller = classCaller;
					g.graphs().clear();
					g = mutGraph("project").setDirected(true);
				}
			}

			//Check if the module is a class, interface or annotation
			String rtn = nUtil.checkModuleType(sourcePath, url1, classCaller, url2, classCalled);
			String nodeNameCaller = rtn.split("\\|")[0] + ": [" + packageCaller + "."+ classCaller + "]" ;
			String nodeNameCalled = rtn.split("\\|")[1] +": [" + packageCalled + "." +classCalled + "]";
			String label = "[" + methodCaller + System.getProperty("line.separator") + "-->" + System.getProperty("line.separator") + methodCalled + "]";


			if (nUtil.getNodeByName(g, nodeNameCaller) == null)
				nCall = mutNode(nodeNameCaller);
			else
				nCall = nUtil.getNodeByName(g, nodeNameCaller);

			if (nodeNameCaller.equals(nodeNameCalled))
			{
				if (signal == 1)
				{
					Link link = Link.between(nCall, nCall).with(Label.of(label));
					nCall.addLink(link);
					g.add(nCall,nCall);
				}
			}
			else
			{
				if (nUtil.getNodeByName(g, nodeNameCalled) == null)
					nCaller = mutNode(nodeNameCalled);
				else
					nCaller = nUtil.getNodeByName(g, nodeNameCalled);

				Link link = Link.between(nCall, nCaller).with(Label.of(label));
				nCall.addLink(link);
				g.add(nCall,nCaller);
			}

			if (i == values.size() - 1)
			{
				List<MutableNode> nodes = new ArrayList<MutableNode>(g.nodes());
				for (MutableNode node : nodes ) 
				{
					String nodename = node.name().toString().substring(node.name().toString().indexOf("[")+1, node.name().toString().length()-1);
					String type = node.name().toString().substring(0, node.name().toString().indexOf("[")+1); 
					String newNodoname = nodename.split("\\.")[nodename.split("\\.").length-1];
					nodename = nodename.replaceAll("\\.", "\\/") + ".java";
					List<String> lStr = parserClass.operation(operation, parser, sourcePath, nodename);
					node.setName(type + newNodoname + "]");
					for (String ifStr : lStr) {

						if (!ifStr.isEmpty()) {
							String methods[] = ifStr.split("\\-");
							MutableNode comparison = mutNode(methods[1]).add(Color.RED,Shape.RECTANGLE, Color.RED.font());
							Link link = Link.between(node, comparison).with(Label.of(methods[0]),Color.RED, Color.RED.font());
							node.addLink(link);
							g.add(node,comparison);
						}
					}
				}

				try
				{
					g.graphAttrs().add(Label.of("Method Calls for :" + newPackageCaller + "." + newClassCaller ));
					Graphviz.fromGraph(g).height(800).width(1280).render(Format.SVG).toFile(new File("figure/"+newClassCaller+".svg"));
					g.graphs().clear();
					Graphviz.releaseEngine();

				}catch(GraphvizException ex)
				{
					ex.printStackTrace();
					newClassCaller = classCaller;
					newPackageCaller = packageCaller;
					g.graphs().clear();
					Graphviz.releaseEngine();
				}
			}
		}
	}

	public void createGraphWithPackageCall(List<String> values) throws IOException {

		Graphviz.useEngine(new GraphvizJdkEngine());
		MutableGraph g = mutGraph("project").setDirected(true);
		MutableNode nCall = null;
		MutableNode nCaller = null;
		String nCalls = "";
		NodeUtil nUtil = new NodeUtil(); 

		for (String str: values) {

			nCall = mutNode(str.split("\\,")[0]);
			nCaller = mutNode(str.split("\\,")[1]);
			nCalls = str.split("\\,")[2];

			if (nUtil.getNodeByName(g, nCall.name().toString()) != null)
				nCall = nUtil.getNodeByName(g, nCall.name().toString());

			if (nUtil.getNodeByName(g, nCaller.name().toString()) != null)
				nCaller = nUtil.getNodeByName(g, nCaller.name().toString());

			if (nCall.name().toString().equals(nCaller.name().toString()))
			{
				Link link = Link.between(nCall, nCall).with(Label.of(nCalls));
				nCall.addLink(link);
				g.add(nCall,nCall);
			}
			else
			{
				Link link = Link.between(nCall, nCaller).with(Label.of(nCalls));
				nCall.addLink(link);
				g.add(nCall,nCaller);
			}
		}

		g.graphAttrs().add(Label.of("Package Calls of the project"));
		Graphviz.fromGraph(g).totalMemory(100777216).height(800).width(1280).render(Format.SVG).toFile(new File("figure/"+"PackageCalls"+".svg"));
		g.graphs().clear();
		Graphviz.releaseEngine();
	}

	public void createGraphFromFile(String path) throws IOException {

		String csvFile = path;
		Graphviz.useEngine(new GraphvizJdkEngine());
		MutableGraph g = mutGraph("project").setDirected(true);
		MutableNode nCaller = null;
		MutableNode nCalled = null;
		MutableNode firstNode = null;
		MutableNode rtnNode = null;
		int i = 0;
		String method = "";
		NodeUtil nUtil = new NodeUtil(); 


		//Build reader instance
		CSVReader reader = new CSVReader(new FileReader(csvFile), ';', '"', 1);

		//Read all rows at once
		List<String[]> allRows = reader.readAll();

		//Read CSV line by line and use the string array as you want
		for(String[] row : allRows){

			nCaller = mutNode(row[0].split(";")[0]);
			nCalled = mutNode(row[1].split(";")[0]);
			method = row[2].split(";")[0];

			if (nUtil.getNodeByName(g, nCaller.name().toString()) != null)
				nCaller = nUtil.getNodeByName(g, nCaller.name().toString());

			if (nUtil.getNodeByName(g, nCalled.name().toString()) != null)
				nCalled = nUtil.getNodeByName(g, nCalled.name().toString());

			if (nCaller.name().toString().equals(nCalled.name().toString()))
			{
				Link link = Link.between(nCaller, nCaller).with(Label.of(method));
				nCaller.addLink(link);
				g.add(nCaller,nCaller);
			}
			else
			{
				Link link = Link.between(nCaller, nCalled).with(Label.of(method));
				nCaller.addLink(link);
				g.add(nCaller,nCalled);
			}

			if (i==0)
			{
				firstNode = nCaller;
				i++;
			}

			if (!row[3].split(";")[0].equals(""))
			{	
				rtnNode = nUtil.getNodeByName(g, row[3].split(";")[0]);
				Link link = Link.between(nCalled,rtnNode).with(Style.DOTTED);
				List<Link> lstLnk = new ArrayList<Link>(nCalled.links());

				if (nCalled.links().isEmpty())
					nCalled.addLink(link);
				else
				{
					for (Link lnk : lstLnk)
					{
						MutableNode tmp = (MutableNode) lnk.to();
						String nodeName = tmp.name().toString();
						if (!nodeName.equals(rtnNode.name().toString()))
							nCalled.addLink(link);
					}
				}
			}
		}

		MutableNode start = mutNode("[START]").add(Label.html("<b>[START]</b>"));
		Link link = Link.between(start, firstNode);
		start.addLink(link);
		g.add(start);

		g.graphAttrs().add(Label.of("Execution Trace"));
		Graphviz.fromGraph(g).totalMemory(100777216).height(800).width(1280).render(Format.SVG).toFile(new File("figure/"+"CustomGraph"+".svg"));
		g.graphs().clear();
		Graphviz.releaseEngine();

		reader.close();
	}

}
