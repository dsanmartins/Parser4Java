package br.parser.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

public class XqueryFile {

	private static final String propFileName = "xquery.properties";
	private static Properties props;

	public static Properties getQueries() throws SQLException {
		
		InputStream is = Property.class.getClassLoader().getResourceAsStream(propFileName);
		if (is == null){
			throw new SQLException("Unable to load property file: " + propFileName);
		}
		//singleton
		if(props == null)
		{
			props = new Properties();
			try 
			{
				props.loadFromXML(is);
			} catch (IOException e) 
			{
				throw new SQLException("Unable to load property file: " + propFileName + "\n" + e.getMessage());
			}
			finally 
			{
			    if (null != is)
			    {
			        try
			        {
			        	is.close();
			        }
			        catch (Exception e)
			        {
			            e.printStackTrace();
			        }
			    }
			}
		}
		return props;
	}

	public static String getQuery(String query) throws SQLException{
		return getQueries().getProperty(query);
	}

}