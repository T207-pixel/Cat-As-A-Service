package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class TCPServerMulti {
    private ServerSocket serverSocket;

    @SuppressWarnings("InfiniteLoopStatement")
    public void start(int port) throws IOException, SQLException {
        serverSocket = new ServerSocket(port);
        while (true)
            new TCPClientToServer(serverSocket.accept()).start();
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    private static class TCPClientToServer{
        private final Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public TCPClientToServer(Socket socket) {
            clientSocket = socket;
        }

        public void start() throws IOException, SQLException {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;
            String usernamePrev = "";
            int catsPatience = 0;
            boolean isFed;
            while (true){
                inputLine = in.readLine();
                if (inputLine != null) {
                    isFed = DatabaseLink.catsPermission(inputLine);
                    if (inputLine.equals("END")){
                        out.println("Session is closed");
                        System.out.println("Session with client is closed");
                        break;  // server closes connection
                    }
                    if (isFed){
                        if (usernamePrev.equals(inputLine)){
                            if (catsPatience == 0){
                                out.println("Cat has run away");
                                System.out.println("Cat has run away");
                                break;  // server closes connection
                            }
                            --catsPatience;
                        } else {
                            DatabaseLink.addRecordInStrokersListOuter(inputLine);
                            catsPatience = generateNumInInterval(1, 3);
                            usernamePrev = inputLine;
                        }
                        inputLine = "Response is: " + inputLine;
                        out.println("Tolerated by the Cat");
                        System.out.println(inputLine);
                    } else {
                        out.println("Scratched by the Cat");
                        System.out.println("Scratched by the cat");
                    }
                }
            }
            close();
        }

        private int generateNumInInterval(int min, int max){
            return (int)((Math.random() * (max - min)) + min);
        }

        private void close() throws IOException {
            in.close();
            out.close();
            clientSocket.close();
        }

    }

    public static void main(String[] args) throws IOException, SQLException {
        TCPServerMulti mainServer = new TCPServerMulti();
        mainServer.start(6666);
        mainServer.stop();
    }
}
