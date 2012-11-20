/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver.configuration;

import java.util.ArrayList;
import java.util.HashMap;

public class ConfigVars {

	private static String serverRoot = "";
	private static String documentRoot = "";
	private static String logFile = "";
	private static String upload = "";
	private static String listen = "";
	private static String maxThread = "";
	private static String cacheEnabled = "";
	private static String persistentConnection = "";
	private static String tempDirectory = "";
	private static String defaultIcon = "";
	private static ArrayList<String> serverAdmin = new ArrayList<String>();
	private static ArrayList<String> directoryIndex = new ArrayList<String>();
	private static ArrayList<Directory> directory = new ArrayList<Directory>();
	private static HashMap<String, String> alias = new HashMap<String, String>();
	private static HashMap<String, String> scriptAlias = new HashMap<String, String>();
	private static HashMap<String, String> cgiHandler = new HashMap<String, String>();
	private static HashMap<String, String> addIconByType = new HashMap<String, String>();
	private static HashMap<String, String> addIcon = new HashMap<String, String>();

	private static HashMap<String, String> mimeTypes = new HashMap<String, String>();

	public static void setServerRoot(String sRoot) {
		serverRoot = sRoot;
	}

	public static void setServerAdmin(String sAdmin[]) {
		String[] mail;
		try {
			mail = sAdmin[1].split(",");

			for (int i = 0; i < mail.length; i++) {
				serverAdmin.add(mail[i].trim());
			}

		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Variable Error: " + sAdmin[0]
					+ " has no value!");
		}

	}

	public static void setUpload(String upld) {
		upload = upld;
	}

	public static void setListen(String lstn) {
		listen = lstn;
	}

	public static void setMaxThread(String max) {
		maxThread = max;
	}

	public static void setCacheEnabled(String cache) {
		cacheEnabled = cache;
	}

	public static void setPersistentConnection(String connection) {
		persistentConnection = connection;
	}

	public static void setDocumentRoot(String docRoot) {
		documentRoot = docRoot;
	}

	public static void setLogFile(String lFile) {
		logFile = lFile;
	}

	public static void setTempDirectory(String tempDir) {
		tempDirectory = tempDir;
	}

	public static void setDirectoryIndex(String[] defaultFile) {
		for (int i = 1; i < defaultFile.length; i++) {
			directoryIndex.add(defaultFile[i]);
		}
	}

	public static void setDefaultIcon(String path) {
		defaultIcon = path;
	}

	public static void setDirectory(String[] dirs) {
		if (dirs.length > 2) {
			for (int i = 2; i < dirs.length; i++) {
				dirs[1] = dirs[1] + " " +dirs[i];
			}
		}		 
		String dir = dirs[1].substring(0,
				dirs[1].indexOf(">"));
		 Directory eachDirectory = new Directory(dir);
		 directory.add(eachDirectory);
	}

	public static void setAuthType(String type) {
		directory.get(directory.size() - 1).setAuthType(type);
	}

	public static void setAuthUserFile(String authFile) {
		directory.get(directory.size() - 1).setAuthUserFile(authFile);
	}

	public static void setRequire(String[] req) {
		if (req[1].equals("user")
				|| req[1].equals("group")) {
			String[] users;
			try {
				users = req[2].split(",");
				for (int i = 0; i < users.length; i++) {
					directory.get(directory.size() - 1).setRequire(users[i]
							.trim());
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("Variable Error: "
						+ req[0] + " " + req[1]
						+ " has no value!");
			}
		} else {
			directory.get(directory.size() - 1).setRequire(req[1]);
			}
	}

	public static void setAuthName(String name) {
		directory.get(directory.size() - 1).setAuthName(name);
	}

	public static String getServerRoot() {
		return serverRoot;
	}

	public static ArrayList<String> getServerAdmin() {
		return serverAdmin;
	}

	public static String getListen() {
		return listen;
	}

	public static String getMaxThread() {
		return maxThread;
	}

	public static String getCacheEnabled() {
		return cacheEnabled;
	}

	public static String getPersistentConnection() {
		return persistentConnection;
	}

	public static String getDocumentRoot() {
		return documentRoot;
	}

	public static String getUpload() {
		return upload;
	}

	public static String getLogFile() {
		return logFile;
	}

	public static String getTempDirectory() {
		return tempDirectory;
	}

	public static ArrayList<String> getDirectoryIndex() {
		return directoryIndex;
	}

	public static String getDefaultIcon() {
		return defaultIcon;
	}

	public static void putAlias(String[] aliasArr) {
		if (aliasArr.length > 3) {
			for (int i = 3; i < aliasArr.length; i++) {
				aliasArr[2] =aliasArr[2] + " " + aliasArr[i];
			}
		
		}
		alias.put(aliasArr[1], aliasArr[2]);
		
	}

	public static String getAlias(String path) {
		return alias.get(path);
	}

	public static void putScriptAlias(String[] sAlias) {
		if (sAlias.length > 3) {
			for (int i = 3; i < sAlias.length; i++) {
				sAlias[2] = sAlias[2] + " " + sAlias[i];
			}
		}
		scriptAlias.put(sAlias[1], sAlias[2]);
	}

	public static String getScriptAlias(String path) {
		return scriptAlias.get(path);
	}

	public static void putCgiHandler(String[] handler) {
		cgiHandler.put(handler[2], handler[1]);
	}

	public static String getCgiHandler(String ext) {
		return cgiHandler.get(ext);
	}

	public static void putAddIconByType(String [] icons) {
		addIconByType.put(icons[2],
				icons[1]);;
	}

	public static String getAddIconByType(String path) {
		return addIconByType.get(path);
	}

	public static void putAddIcon(String [] icons) {
		for (int i = 2; i < icons.length; i++) {
			addIcon.put(icons[i],
					icons[1]);
		}
	}

	public static String getAddIcon(String ext) {
		return addIcon.get(ext);
	}

	public static void putMIME(String ext, String type) {
		mimeTypes.put(ext, type);
	}

	public static String getMIME(String ext) {
		return mimeTypes.get(ext);
	}
	
	public static ArrayList<Directory> getDirectoryArr() {
		return directory;
	}

	public static Directory getDirectory(int index) {
		return directory.get(index);
	}

	public static String getDirectory(Directory d) {
		return d.getDirectory();
	}

	public static String getType(Directory d) {
		return d.getType();
	}

	public static String getAuthUserFile(Directory d) {
		return d.getAuthUserFile();
	}

	public static ArrayList<String> getRequire(Directory d) {
		return d.getRequire();
	}

	public static String getAuthName(Directory d) {
		return d.getAuthName();
	}
}

