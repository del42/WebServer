package webserver.configuration;

import java.util.ArrayList;

public class Directory {

	private String directory = "";
	private String authName = "";
	private ArrayList<String> require = new ArrayList<String>();
	private String authType = "";
	private String authUserFile = "";

	public Directory(String dir) {
		this.directory = dir;
	}

	public void setAuthType(String type) {
		authType = type;
	}

	public void setAuthUserFile(String authFile) {
		authUserFile = authFile;
	}

	public void setRequire(String req) {
		require.add(req);
	}

	public void setAuthName(String name) {
		authName = name;
	}

	public String getDirectory() {
		return directory;
	}

	public String getType() {
		return authType;
	}

	public String getAuthUserFile() {
		return authUserFile;
	}

	public ArrayList<String> getRequire() {
		return require;
	}

	public String getAuthName() {
		return authName;
	}
}


