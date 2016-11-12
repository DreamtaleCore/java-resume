import java.io.OutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;

/*
  HTTP Response = Status-Line
    *(( general-header | response-header | entity-header ) CRLF)
    CRLF
    [ message-body ]
    Status-Line = HTTP-Version SP Status-Code SP Reason-Phrase CRLF
*/

class Response {

    private static final int BUFFER_SIZE = 1024;
    private Request request;
    private OutputStream output;

    Response(OutputStream output) {
        this.output = output;
    }

    void setRequest(Request request) {
        this.request = request;
    }

    void sendStaticResource() throws IOException {
        byte[] bytes = new byte[BUFFER_SIZE];
        FileInputStream fis = null;
        try {
            // web file -> outputStream
            File file = new File(HttpServer.WEB_ROOT, request.getUri());
            if (file.exists()) {
                fis = new FileInputStream(file);
                int ch = fis.read(bytes, 0, BUFFER_SIZE);
                while (ch != -1) {
                    output.write(bytes, 0, ch);
                    ch = fis.read(bytes, 0, BUFFER_SIZE);
                }
            } else {
                // file not found
                String errorMessage = "HTTP/1.1 404 File Not Found\r\n" + "Content-Type: text/html\r\n"
                        + "Content-Length: 23\r\n" + "\r\n" + "<h1>File Not Found</h1>";
                output.write(errorMessage.getBytes());
            }
        } catch (Exception e) {
            // thrown if cannot instantiate a File object
            System.out.println(e.toString());
            System.out.println("Here!!!");
            // if the path is a directory, then try again
            try {
                File file = new File(HttpServer.WEB_ROOT, request.getUri() + HttpServer.DEFAULT_FILE);
                if(file.exists()) {
                    fis = new FileInputStream(file);
                    int ch = fis.read(bytes, 0, BUFFER_SIZE);
                    while (ch != -1) {
                        output.write(bytes, 0, ch);
                        ch = fis.read(bytes, 0, BUFFER_SIZE);
                    }
                }
            } catch (Exception e1) {
                System.out.println(e1.toString());
            }
        } finally {
            if (fis != null)
                fis.close();
        }
    }
}