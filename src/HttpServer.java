import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    static String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";
    static final String DEFAULT_FILE = "index.html";

    // The command to close server
    private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

    public static void main(String[] args) {

        if (args.length > 0) {
            WEB_ROOT = args[0];
        }
        System.out.println(WEB_ROOT);
        HttpServer server = new HttpServer();
        //等待连接请求
        server.await();
    }

    private void await() {
        ServerSocket serverSocket = null;
        int port = 2345;
        try {
            serverSocket = new ServerSocket(port, 1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Waiting for a request
        while (true) {
            Socket socket;
            InputStream input;
            OutputStream output;
            try {
                // Waiting for a connection
                socket = serverSocket.accept();
                input = socket.getInputStream();
                output = socket.getOutputStream();

                // Create Request and parse it
                Request request = new Request(input);
                request.parse();
                // Check weather to shut down the server
                if (request.getUri().equals(SHUTDOWN_COMMAND)) {
                    break;
                }

                Response response = new Response(output);
                response.setRequest(request);
                response.sendStaticResource();

                // close socket
                socket.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}