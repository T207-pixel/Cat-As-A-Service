package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class TCPClient {
    private final Socket clientSocket;
    private final PrintWriter out;
    private final BufferedReader in;

    public TCPClient(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void start() throws IOException {        // заканчивать кормить кота тут один раз обязательно а дальше на рандом формирую n [1 - 5] сколько раз его еще можно погладить, при условии что строка имя пользоваткеля не менялось
        Scanner scanner = new Scanner(System.in);
        while (true){
            System.out.println("""
                    Would you like to stroke cat?
                    If yes -> enter username in format "@nickname"
                    If no -> enter "END\"""");
            String inputString = scanner.nextLine();
            String response = sendMessage(inputString);
            if (Objects.equals(response, "Cat has run away")){
                //System.out.println(response);
                System.out.println("!!!SERVER CLOSED CONNECTION!!!");
                break;
            } else if (response.equals("Session is closed")) {
                System.out.println("Current session with server is closed");
                break;
            } else {
                System.out.println(response);
            }
        }
    }

    public String sendMessage(String msg) throws IOException {
        out.println(msg);
        return in.readLine();
    }

    public void stop() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public static void main() throws IOException {
        TCPClient client = new TCPClient("5.42.220.74", 6666);
        client.start();
        client.stop();
    }


}

