package br.parser.database;

public class SqliteDb extends Db
{
	String sDriverForClass = "org.sqlite.JDBC";

	public SqliteDb(String sDriverKey, String sUrlKey) throws Exception
	{

		init(sDriverForClass, sUrlKey);

		if(conn != null)
		{
			//System.out.println("Connected OK using " + sDriverKey + " to " + sUrlKey);
		}
		else
		{
			System.out.println("Connection failed");
		}
	}
	
}
