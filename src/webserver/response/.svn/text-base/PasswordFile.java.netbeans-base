package webserver.response;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import webserver.encryption.Base64Encoder;

public class PasswordFile {
	String username = null;
	String password = null;

	public void htpasswd(String path, String username) {

		BufferedWriter bw = null;
		try {
			this.username = username;
			System.out.println("Please enter New Password:\n");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));
			StringTokenizer st = new StringTokenizer(in.readLine());
			password = st.nextToken();

			System.out.println("Re-Enter Password:\n");
			BufferedReader in1 = new BufferedReader(new InputStreamReader(
					System.in));
			StringTokenizer st1 = new StringTokenizer(in1.readLine());

			if (password.equals(st1.nextToken())) {
				Base64Encoder encoder = new Base64Encoder(password);
				String userpasswd = username + ":" + encoder.processString();

				File file = new File(path);

				if (!file.exists()) {
					file.createNewFile();
				}
				BufferedReader reader = new BufferedReader(new FileReader(file));

				String nextLine = reader.readLine();
				while (nextLine != null) {
					if (!nextLine.contains(userpasswd)) {
						nextLine = reader.readLine();
					} else {
						System.out
								.println("Username and Password already exist.");
						System.exit(1);
					}

				}
				if (nextLine == null) {
					bw = new BufferedWriter(new FileWriter(path, true));
					bw.write(userpasswd);
					bw.write(System.getProperty("line.separator"));
					bw.flush();
					System.out
							.println("Username and Password are successfully saved.");
				}
			} else {
				System.out.println("Passwords do not match.");
				htpasswd(path, username);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args) {
		PasswordFile passwd = new PasswordFile();
		if (args[0].equals("-c")) {

			for (int i = 2; i < args.length - 1; i++) {
				args[1] = args[1] + " " + args[i];
			}
			passwd.htpasswd(args[1], args[args.length - 1]);
		}

	}

}
