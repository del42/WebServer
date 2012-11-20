package webserver.configuration;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * This class contains all the configuration parameters obtained from parsing
 * the httpd.conf and mime.types files. These parameters are used by different methods to 
 * 
 */
public class HttpdConf {

	private BufferedReader configFile;
	private boolean hasMore;
	private String nextLine;
	private static final HashMap<String, String> confVar = new HashMap<String, String>();

	public HttpdConf(String cFile) {
		try {
			this.configFile = new BufferedReader(new FileReader(cFile));
		} catch (FileNotFoundException ex) {
			System.out.println("httpd.conf file was not found!");
			System.exit(1);
		}
		hasMore = true;
		setConfVar();
	}

	private static void setConfVar() {
		confVar.put("Alias", "putAlias");
		confVar.put("ScriptAlias", "putScriptAlias");
		confVar.put("ServerAdmin", "setServerAdmin");
		confVar.put("DirectoryIndex", "setDirectoryIndex");
		confVar.put("require", "setRequire");
		confVar.put("CgiHandler", "putCgiHandler");
		confVar.put("AddIcon", "putAddIcon");
		confVar.put("AddIconByType", "putAddIconByType");
		confVar.put("<Directory", "setDirectory");
	}

	public void processConfigFile() {
		while (hasMoreVar()) {
			getNextVar();
		}
	}

	private boolean hasMoreVar() {
		try {
			nextLine = configFile.readLine();
			if (nextLine == null)
				hasMore = false;
		} catch (IOException e) {
			System.out.println("Reading File Error: " + e.getMessage());
			System.exit(1);
		}
		return hasMore;
	}

	private void getNextVar() {
		String temp[]; // store the tokens
		String delimiter = "\\s+";
		nextLine = nextLine.trim();
		if (!nextLine.startsWith("#") && nextLine.length() != 0) {
			try {
				temp = nextLine.split(delimiter);
				setConfVars(temp);
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("Variable Error: " + nextLine
						+ " has no value!");
			}
		}
	}

	private void setConfVars(String[] varInfo) {

		// remove "\"" from the httpd.conf variable configurations
		for (int i = 0; i < varInfo.length; i++) {
			if (varInfo[i].contains("\"")) {
				varInfo[i] = varInfo[i].replace("\"", "").trim();
			}
		}

		try {
			if (confVar.get(varInfo[0]) == null
					&& !varInfo[0].equals("</Directory>")) {

				if (varInfo.length > 2) {
					for (int i = 2; i < varInfo.length; i++) {
						varInfo[1] = varInfo[1] + " " + varInfo[i];
					}
				}

				Method method = Class.forName(
						"webserver.configuration.ConfigVars").getMethod(
						"set" + varInfo[0], String.class);
				method.invoke(null, varInfo[1]);
			} else {
				if (!varInfo[0].equals("</Directory>")) {
					Method method = Class.forName(
							"webserver.configuration.ConfigVars").getMethod(
							confVar.get(varInfo[0]), varInfo.getClass());
					method.invoke(null, new Object[] { varInfo });
				}
			}

		} catch (IllegalAccessException ex) {
			System.out.println(ex.getMessage());
		} catch (IllegalArgumentException ex) {
			System.out.println(ex.getMessage());
		} catch (InvocationTargetException ex) {
			System.out.println(ex.getMessage());
		} catch (ClassNotFoundException ex) {
			System.out.println(ex.getMessage());
		} catch (NoSuchMethodException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		} catch (SecurityException ex) {
			System.out.println(ex.getMessage());
		}

	}
}
