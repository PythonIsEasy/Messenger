import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    // global vars
    private BufferedReader reader;
    private ArrayList messages;


    public static void main(String [] args) throws IOException {
        new Server().start();  // server start
    }

    private class ClientHandler implements Runnable {

         ClientHandler(Socket clientSocket) throws IOException {
            try {
                // open the streams
                InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
                reader = new BufferedReader(inputStreamReader);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // read messages and send them back
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    sendMessage(message);
                    System.out.println(message);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    private void start() throws IOException {
        messages = new ArrayList();

        try {
            ServerSocket serverSocket = new ServerSocket(5000);

            while (true) {

                // accept new client
                Socket socket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                messages.add(writer);
                Thread t = new Thread(new ClientHandler(socket));  // new thread
                t.start(); // start the thread
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // send message function
    private void sendMessage(String message){
        for (Object message1 : messages) {
            try {
                PrintWriter writer = (PrintWriter) message1;
                writer.println(message);
                writer.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

