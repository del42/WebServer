package webserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import webserver.configuration.ConfigVars;
import webserver.configuration.HttpdConf;
import webserver.configuration.MIME;
import webserver.request.BufferedInputStreamReader;
import webserver.request.HttpReqMsg;
import webserver.request.Request;
import webserver.response.HttpRespMsg;
import webserver.response.Response;

/**
 * This class is the entry point of the web server application. It reads the
 * configuration files and sets the configuration parameters. Then, it opens a
 * server socket and starts receiving the requests from the browser and
 * assigning them to the threads. Each thread is responsible for parsing the
 * request, preparing the response and sending the response back to the browser
 * through different objects and methods.
 */
public class WebServer {

    private ServerSocket serverSocket;
    private Socket socket = null;
    private static int tID = 0;
    private ExecutorService threadPoolExecutor;

    /**
     * This is the web server constructor. As soon as a web server object is
     * instantiated, it instantiates two other objects which read the
     * configuration files and set the configuration parameters for the web
     * server. Now, the web server is ready to receive the requests.
     */
    public WebServer() {
        // parse httpd.conf and mime.types files as the web server starts
        String userDir = System.getProperty("user.dir");
        String sep = System.getProperty("file.separator");

        System.out.println("Configuring the web server ..." + "\n");
        new HttpdConf(userDir + sep + "conf" + sep + "httpd.conf").processConfigFile();
        new MIME(userDir + sep + "conf" + sep + "mime.types").processMIMEFile();
    }

    /**
     * This method opens a server socket to receive the requests from the
     * browser. Then, it creates a thread pool with a capacity defined in
     * configuration parameters and starts accepting the incoming requests and
     * assigning them to the threads in the thread pool.
     *
     * @throws InterruptedException
     */
    public void webServerRun() throws InterruptedException {

        try {
            serverSocket = new ServerSocket(Integer.parseInt(ConfigVars.getListen()));
            System.out.println("Socket Opened on Port "
                    + ConfigVars.getListen() + ".");
        } catch (IOException e) {
            System.out.println("Failed to open socket on port "
                    + ConfigVars.getListen() + ".");
            System.exit(1);
        }

        int maxThread = Integer.parseInt(ConfigVars.getMaxThread());
        // thread pool with maxThread number
        threadPoolExecutor = Executors.newFixedThreadPool(maxThread);

        while (true) {
            try {
                socket = serverSocket.accept();
                threadPoolExecutor.execute(new ReqThread(socket, tID));
                if (tID == maxThread) {
                    tID = 0;
                } else {
                    tID++;
                }
            } catch (IOException e) {
                System.out.println("Accept Failed on: "
                        + ConfigVars.getListen());
                System.exit(1);
            }

        }
    }

    public static void main(String[] args) throws IOException,
            InterruptedException {
        WebServer ws = new WebServer();
        ws.webServerRun();
    }
}

/**
 * This class defines the functionalities of each thread. Each thread receives
 * the request assigned by the WebServer. It instantiates a Request object,
 * which parses the request and stores the extracted information in an
 * HttpReqMsg object. Then, it instantiates the Response object which prepares
 * the response in an HttpRespMsg object. Finally,the thread sends the respond
 * back to the server. The request and response information is saved on a log
 * file.
 */
class ReqThread implements Runnable {

    private Socket socket;
    private Request request;
    private Response response;
    private HttpReqMsg reqMsg;
    private HttpRespMsg respMsg;
    private FileWriter logFile;
    private BufferedInputStreamReader req;
    BufferedOutputStream out;
    private int tID;

    public ReqThread(Socket clientSocket, int id) {
        this.socket = clientSocket;
        this.tID = id;
        try {
            req = new BufferedInputStreamReader(new BufferedInputStream(socket.getInputStream()));
            out = new BufferedOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * This method is responsible for writing to the log file. For each request
     * and its response, the following information is saved: 1. the client's
     * name and IP address. 2. the request's type and resource 3. the HTTP
     * version 4. the response's status code, description, and body size (if
     * any) 5. the date and time of the response
     *
     * @throws IOException
     */
    synchronized public void writeLog() throws IOException {
        char quotation = '"';
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z");

        logFile = new FileWriter(ConfigVars.getLogFile(), true);
        PrintWriter lwrite = new PrintWriter(logFile, true);
        lwrite.print(socket.getLocalAddress().getHostAddress() + " -"
                + socket.getLocalAddress().getCanonicalHostName() + " "
                + quotation + reqMsg.getMethodName() + " " + reqMsg.getURI()
                + " " + respMsg.getVersion() + quotation + " "
                + respMsg.getStatusCode() + " " + respMsg.getDescription()
                + " ");
        if (respMsg.getBody() != null) {
            lwrite.print(Long.toString(respMsg.getBody().length()) + " Bytes");
        } else {
            lwrite.print("0 Bytes");
        }
        lwrite.println(" " + sdf.format(new Date(currentTime)));
    }

    /**
     * This method is invoked when each thread starts running. It instantiates
     * the objects and call the methods that perform the web server's main
     * functionalities.
     */
    public void run() {
        request = new Request(req);
        reqMsg = request.parseRequest();
        response = new Response(reqMsg, socket);
        try {
            respMsg = response.buildResponse();
        } catch (InternalServerErrorException e) {
            System.out.println("Internal Server Error");
            respMsg = e.getRespMsg();
        }

        try {
            writeLog();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        if (respMsg != null) {
            sendResponse();
        }

    }

    /**
     * This method sends the response back to the client. It sends the
     * response's first line which contains the status code, description and
     * HTTP version. Then, it send the headers and the reponse's body (if any).
     */
    public void sendResponse() {
        try {
            BufferedOutputStream out = new BufferedOutputStream(
                    socket.getOutputStream());
            PrintWriter writer = new PrintWriter(out, true);

            // writes the response's first line
            writer.println(respMsg.getVersion() + " " + respMsg.getStatusCode()
                    + " " + respMsg.getDescription());
            System.out.println(respMsg.getVersion() + " "
                    + respMsg.getStatusCode() + " " + respMsg.getDescription());

            // writes the headers
            Iterator<Entry<String, String>> it = respMsg.getHeaders().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
                writer.println(pairs.getKey() + ": " + pairs.getValue());
                it.remove();
            }

            // inserts a blank line if the response is not a cgi result
            if (!reqMsg.getCgiScript()) {
                writer.println("");
            }

            // write the response body if it contains one
            if (respMsg.getBody() != null) {
                byte[] body = new byte[(int) respMsg.getBody().length()];
                FileInputStream in = new FileInputStream(respMsg.getBody());
                BufferedInputStream bufferedIn = new BufferedInputStream(in);
                bufferedIn.read(body, 0, body.length);
                System.out.println(tID + " is sending..." + "\n");
                out.write(body, 0, body.length);
                out.flush();
            }
            out.close();
            socket.close();
        } catch (IOException ex) {
            System.out.println("Access Failed: " + ConfigVars.getListen()
                    + ex.getMessage());
        }
    }
}
