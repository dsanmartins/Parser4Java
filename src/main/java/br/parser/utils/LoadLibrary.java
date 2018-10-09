package br.parser.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

public class LoadLibrary {

	List <String> lStr = new ArrayList<String>();

	public List<String> getListOfAllConfigFiles(String directoryName, String filter)
	{
		File directory = new File(directoryName);
		Collection<?> collection = FileUtils.listFiles(directory, new WildcardFileFilter(filter), null);
		List<?> lfu = new ArrayList<>(collection);
		for (int i = 0; i < lfu.size(); i++)
			lStr.add(lfu.get(i).toString());

		return lStr;
	}
}
