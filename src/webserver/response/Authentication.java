package webserver.response;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import webserver.InternalServerErrorException;
import webserver.configuration.ConfigVars;
import webserver.configuration.Directory;
import webserver.encryption.Base64Decoder;
import webserver.encryption.Base64FormatException;
import webserver.request.HttpReqMsg;

public class Authentication {
	private Response response;
	private HttpReqMsg reqMsg;
	private HttpRespMsg respMsg;
	private String path;

	public Authentication(Response resp) {
		this.response = resp;
		this.reqMsg = resp.getRequest();
		this.respMsg = resp.getResponse();
		this.path = resp.getPath();
	}

	public void checkAuthentication() throws InternalServerErrorException {
		ArrayList<Directory> dirArray = ConfigVars.getDirectoryArr();
		// check path
		for (int i = 0; i < dirArray.size(); i++) {
			Directory d = ConfigVars.getDirectory(i);
			String dir = d.getDirectory();
			if (path.contains(dir)) {
				response.setAuthorized(false);
				checkCredentials(d);
				return;
			}
		}
				response.setAuthorized(true);
	}

	private void checkCredentials(Directory d) throws InternalServerErrorException {
		boolean isPriorEndLine = true;
		String nextLine;
		if (reqMsg.getHeader("Authorization") != null) {
			String[] temp = reqMsg.getHeader("Authorization").trim().split(" ");
			String type = temp[0];

			if (type.equals("Basic")) {
				// extracting username and password from the header
				String credentials = temp[1];
				Base64Decoder decoder = new Base64Decoder(credentials);
				String namePassword = "";
				try {
					namePassword = decoder.processString();
				} catch (Base64FormatException e) {
					System.out
							.println("Incorrectly formatted authentication string: "
									+ e);
					throw new InternalServerErrorException(respMsg);
				}

				String[] namePass = namePassword.split(":");
				String name = namePass[0];
				String passwd = namePass[1];
				try {
					String passwdPath = d.getAuthUserFile();
					BufferedReader passwdFile = new BufferedReader(
							new FileReader(passwdPath));

					while (isPriorEndLine) {
						nextLine = passwdFile.readLine();
						if (nextLine != null && nextLine.trim().length() > 0) {
							String[] savedNamePass = nextLine.split(":");
							String savedName = savedNamePass[0];
							if (name.equals(savedName)) {
								String savedPass = savedNamePass[1].trim();
								
								 decoder = new Base64Decoder(savedPass);
								 String decodedSavedPass = decoder.processString();
								if (passwd.equals(decodedSavedPass)) {
									response.setAuthorized(true);
									return;
								} 
								/*else {
									response.setAuthorized(false);
									respMsg.setStatusCode("403");
									respMsg.setDescription("Forbidden");
									return;
								}*/
							}
						} else {
							if (nextLine == null) {
								isPriorEndLine = false;
							}
						}
					}
					response.setAuthorized(false);
					respMsg.setStatusCode("403");
					respMsg.setDescription("Forbidden");
					respMsg.setBody(new File(ConfigVars.getServerRoot() + "/error/403.html"));
				} catch (FileNotFoundException ex) {
					System.out.println("No password file was found.");
					throw new InternalServerErrorException(respMsg);
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}  catch (Base64FormatException e) {
					 System.out
					 .println("Incorrectly formatted authentication string: "
					 + e);
					 throw new InternalServerErrorException(respMsg);
					 
				 }
			}
		} else {
			respMsg.setStatusCode("401");
			respMsg.setDescription("Unauthorized");
			respMsg.setHeader("WWW-Authenticate",
					d.getType() + " realm=" + d.getAuthName());
			response.setAuthorized(false);
		}
	}
}
