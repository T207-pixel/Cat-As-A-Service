package org.example;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class UDPClient {
    private DatagramSocket datagramSocket;  // socket for communication
    private InetAddress inetAddress;    // IP address of server that we're going to be sending packet
    private byte[] buffer;  // info that's going to be sent

    public UDPClient(DatagramSocket datagramSocket, InetAddress inetAddress) {
        this.datagramSocket = datagramSocket;
        this.inetAddress = inetAddress;
    }

    public void sendThenReceive() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                System.out.println("""
                        1) Go back to menu -> Enter "END"
                        2) Enter your nickname and dish in format "@Name - Dish~\"""");
                String inputString = scanner.nextLine();
                ////////////////////////////////////////////---SEND-REQUEST---/////////////////////////////////////////
                buffer = inputString.getBytes();
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, inetAddress, 1234); // inetAddress - is where we want to send it to, port is hardcoded have to change
                datagramSocket.send(datagramPacket);
                ////////////////////////////////////////---RECEIVE-RESPONSE---//////////////////////////////////////////
                byte[] buffer = new byte[512];  // allocate bytes for response message
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(response);
                String messageFromServer = new String(response.getData(), 0, response.getLength());
                System.out.println(messageFromServer);
                if (inputString.equals("END")){
                    break;
                }

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public static void main() throws SocketException, UnknownHostException {
        DatagramSocket datagramSocket = new DatagramSocket();
        InetAddress inetAddress = InetAddress.getByName("5.42.220.74"); // server IP destination address
        UDPClient client = new UDPClient(datagramSocket, inetAddress);
        client.sendThenReceive();
    }

}