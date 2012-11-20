package webserver.response;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import webserver.InternalServerErrorException;
import webserver.configuration.ConfigVars;
import webserver.encryption.Base64Decoder;
import webserver.encryption.Base64FormatException;
import webserver.request.HttpReqMsg;

public class CGIHandler {

	private Socket socket;
	private HttpReqMsg reqMsg;
	private HttpRespMsg respMsg;
	private String getArgs = "";
	private String extra = "";
	private String scriptName;
	private String path;

	private Map<String, String> environments;

	public CGIHandler(Response resp) {
		this.socket = resp.getSocket();
		this.reqMsg = resp.getRequest();
		this.respMsg = resp.getResponse();
		this.path = resp.getPath();
		this.scriptName = "";
	}

	public void buildProcess() throws InternalServerErrorException {
		String scriptExePath;
		ProcessBuilder pb;
		scriptExePath = "/usr/bin/perl";

		parseURI();

		// check to see if the resource (script) exists
		File script = new File(path);
		if (!script.exists()) {
			respMsg.setStatusCode("404");
			respMsg.setDescription("Not Found");
			respMsg.setBody(new File(ConfigVars.getServerRoot()
					+ "/error/404.html"));
			reqMsg.setCgiScript(false);
		} else {
			if (script.exists()) {
				BufferedReader br;
				try {
					br = new BufferedReader(new FileReader(script));
					String line = br.readLine();
					scriptExePath = line.replace("#!", "");
					scriptExePath = scriptExePath.replace("\"", "");
				} catch (FileNotFoundException e) {
					System.out.println("Script Not Found!");
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}

			}

			if (getArgs.trim().length() > 0) {
				pb = new ProcessBuilder(scriptExePath, scriptName, getArgs);
			} else {
				pb = new ProcessBuilder(scriptExePath, scriptName);
			}

			environments = pb.environment();
			setEnvVar();

			String directory = "";
			String[] tempPath = path.substring(1).split("/");
			for (int i = 0; i < tempPath.length - 1; i++) {
				directory = directory + "/" + tempPath[i];
			}

			pb.directory(new File(directory));

			Process p = null;

			try {
				p = pb.start();
			} catch (IOException e) {
				System.out.println(e.getMessage());
				throw new InternalServerErrorException(respMsg);
			}

			if (reqMsg.getMethodName().equals("POST")
					&& reqMsg.getBody() != null) {
				sendPostParameters(p);
			}
			try {
				InputStream result = p.getInputStream();
				OutputStream fOut = new FileOutputStream("cgiResult");
				byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = result.read(buffer)) != -1) {
					fOut.write(buffer, 0, bytesRead);
				}
				result.close();
				fOut.close();

				respMsg.setStatusCode("200");
				respMsg.setDescription("OK");
				File cgiResult = new File("cgiResult");
				/*
				 * BufferedReader br = new BufferedReader(new
				 * FileReader(cgiResult)); int length = 0; String headerLines =
				 * br.readLine(); while (headerLines.trim().length() >0) {
				 * length = length + headerLines.length(); headerLines =
				 * br.readLine(); } Integer len = new Integer(length); long
				 * contentLength = cgiResult.length() - len.longValue();
				 * respMsg.setHeader("Content-Length",
				 * Long.toString(contentLength));
				 */
				respMsg.setBody(cgiResult);

			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	private void parseURI() {
		if (reqMsg.getMethodName().equals("GET") && path.contains("?")) {
			getQueryString();
		}
		// check for extra
		if (path.contains("extra")) {
			int index = path.indexOf("extra");
			extra = path.substring(index - 1);
			path = path.substring(0, index - 1);
		}

		String temp[] = path.substring(1).split("/");
		scriptName = temp[temp.length - 1];
	}

	private void getQueryString() {
		// extract the parameters the parameters in GET method
		int index = path.indexOf("?");
		getArgs = path.substring(index + 1);
		path = path.substring(0, index);
	}

	private void setEnvVar() {
		// Non-request-specific environment variables
		environments.put("SERVER_SOFTWARE", "CSC867/1.0");
		environments.put("SERVER_NAME", "SFSUCSC867");
		environments.put("GATEWAY_INTERFACE", "CGI/1.1");
		// Request-specific environment variables
		environments.put("SERVER_PROTOCOL", reqMsg.getVersion());
		environments.put("SERVER_PORT", ConfigVars.getListen());
		environments.put("REQUEST_METHOD", reqMsg.getMethodName());

		if (extra.trim().length() > 0) {
			environments.put("PATH_INFO", extra);
			environments.put("PATH_TRANSLATED", ConfigVars.getDocumentRoot()
					+ extra);
		} else {
			environments.put("PATH_INFO", "");
			environments.put("PATH_TRANSALTED", "");
		}
		environments.put("SCRIPT_NAME", scriptName);
		environments.put("QUERY_STRING", getArgs);
		environments.put("REMOTE_HOST", socket.getLocalAddress()
				.getCanonicalHostName());
		environments.put("REMOTE_ADDR", socket.getLocalAddress()
				.getHostAddress());

		// Request Headers
		HashMap<String, String> headers = reqMsg.getAllHeaders();

		Iterator<Entry<String, String>> it = headers.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> pair = (Map.Entry<String, String>) it
					.next();
			if (HeaderEnvVar.get(pair.getKey()) != null) {
				if (!pair.getKey().equals("Authorization")) {
					environments.put(HeaderEnvVar.get(pair.getKey()),
							pair.getValue());
				} else {
					environments.put(HeaderEnvVar.get(pair.getKey()),
							getRemoteUser(pair.getValue()));
				}
			} else {
				String tempName = pair.getKey().toUpperCase();
				if (tempName.contains("-")) {
					tempName = tempName.replace("-", "_");
				}
				tempName = "HTTP_" + tempName;
				environments.put(tempName, pair.getValue());
			}
		}
	}

	// send post parameters to cgi
	private void sendPostParameters(Process p) {
		BufferedOutputStream postParams = new BufferedOutputStream(
				p.getOutputStream());
		byte[] body = reqMsg.getBody();
		String len = environments.get("CONTENT_LENGTH").trim();
		int length = Integer.parseInt(len);
		try {
			postParams.write(body, 0, length);
			postParams.flush();
			postParams.close();
		} catch (IOException e) {
			System.out
					.println("An error occured when sending post parameters to script!");
		}

	}

	private String getRemoteUser(String user) {
		String[] userPass = null;
		String[] temp = user.trim().split(" ");
		try {
			Base64Decoder decoder = new Base64Decoder(temp[1]);
			String decodedUserPass = decoder.processString();
			userPass = decodedUserPass.split(":");
		} catch (Base64FormatException e) {
			e.printStackTrace();
		}
		return userPass[0];
	}

}

class HeaderEnvVar {
	private static final HashMap<String, String> headerEnvVar = new HashMap<String, String>();
	static {
		headerEnvVar.put("Content-Type", "CONTENT_TYPE");
		headerEnvVar.put("Content-Length", "CONTENT_LENGTH");
		headerEnvVar.put("Authorization", "REMOTE_USER");
	}

	public static String get(String header) {
		return headerEnvVar.get(header);
	}

}