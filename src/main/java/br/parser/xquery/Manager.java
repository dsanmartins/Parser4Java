package br.parser.xquery;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.Close;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.DropDB;
import org.basex.core.cmd.Export;
import org.basex.core.cmd.Open;

public class Manager   {

	Context context; 
	CreateDB createDB;
	String path;
	Open openDb;
	Close closeDb;
	DropDB dropDB;
	Export export;

	public Manager(String path, String file, String dbName) throws BaseXException
	{
		// Create a database from a local or remote XML document or XML String
		context = new Context();
		createDB = new CreateDB(dbName, path + file);
		createDB.execute(context);
		this.path = path + file; 
		openDb = new Open(dbName);
		closeDb = new Close();
		export = new Export(this.path); 
		dropDB = new DropDB(dbName);
	}

	public void createDB(String path, String file, String dbName) throws BaseXException {
		// TODO Auto-generated method stub
		context = new Context();
		createDB = new CreateDB(dbName, path + file);
		createDB.execute(context);
		openDb = new Open(dbName);
	}

	public void openDB() throws BaseXException {
		openDb.execute(context);
	}

	public void closeDB() throws BaseXException {
		closeDb.execute(context);
	}

	public void exportDB() throws BaseXException {
		export.execute(context);
	}

	public void dropDB() throws BaseXException {
		dropDB.execute(context);
	}

	public Context getContext() {

		return this.context;
	}
}
