import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class clientHandler {

    // get socket
    Socket getSocket() throws IOException{
        System.out.println("socket");
        return new Socket("127.0.0.1",5000);
    }

    // input stream reader
    private InputStreamReader getStreamReader(Socket socket) throws IOException{
        return new InputStreamReader(socket.getInputStream());
    }

    // buffered reader
    BufferedReader getReader(Socket socket) throws IOException{
        return new BufferedReader(getStreamReader(socket));
    }

    // printer writer
    PrintWriter getWriter(Socket socket) throws IOException{
        return new PrintWriter(socket.getOutputStream());
    }
}
