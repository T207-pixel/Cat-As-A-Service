// подключение по http://localhost:8080/
package org.example;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class HttpServer {

    public static void main(String[] args) throws Throwable {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            while (true) {
                Socket socket = serverSocket.accept();
                System.err.println("Client accepted");
                new Thread(new SocketProcessor(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class SocketProcessor implements Runnable {

        private final Socket socket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        private SocketProcessor(Socket socket) throws Throwable {
            this.socket = socket;
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
        }

        @Override
        public void run() {
            try {
                readInputHeaders();
                writeResponse("<html><body><h1>Feeders and dishes records</h1>" +
                                "<h2>Name - dish</h2>" +
                                "<ul>" + DatabaseLink.generateFeedersList() + "</ul>" +
                                "<h1>Strokers list</h1>" +
                                "<h2>Strokers</h2>" +
                                "<ul>" + DatabaseLink.generateStrokersList() + "</ul>" +
                                "</body></html>");
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            System.err.println("Client processing finished");
        }

        private void writeResponse(String s) throws Throwable {
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Server: YarServer/2023-07-23\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: " + s.length() + "\r\n" +
                    "Connection: close\r\n\r\n";
            String result = response + s;
            outputStream.write(result.getBytes());
            outputStream.flush();
        }

        private void readInputHeaders() throws Throwable {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while(true) {
                String line = bufferedReader.readLine();
                if(line == null || line.trim().length() == 0) {
                    break;
                }
            }
        }

    }

}
