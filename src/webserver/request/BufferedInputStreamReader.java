package webserver.request;

import java.io.BufferedInputStream;
import java.io.IOException;

public class BufferedInputStreamReader {

	private BufferedInputStream in;

	public BufferedInputStreamReader(BufferedInputStream in) {
		this.in = in;
	}

	public int read() throws IOException {
		return in.read();
	}

	public int read(byte[] b) throws IOException {
		return in.read(b);
	}

	public int read(byte[] b, int off, int len) throws IOException {
		return in.read(b, off, len);
	}

	public long skip(long n) throws IOException {
		return in.skip(n);
	}

	public void close() throws IOException {
		in.close();
	}

	/**
	 * This method reads the input stream line by line. The end of line is
	 * indicated by "\r" or "\n" or "\r\n", which are used to stop reading the
	 * input stream.
	 * 
	 * @return a line
	 * @throws IOException
	 */
	public String readLine() throws IOException {
		char c = (char) in.read();
		String line = "";
		while (c != '\r' && c != '\n') {
			line = line + c;
			c = (char) in.read();
		}
		scan(c);
		return line;
	}

	private void scan(char c) throws IOException {
		if (!in.markSupported())
			return;
		in.mark(1);
		if (c == '\r') {
			char cNext = (char) in.read();
			if (cNext != '\n')
				in.reset();
		}
	}

	public int available() throws IOException {
		return in.available();
	}


}
