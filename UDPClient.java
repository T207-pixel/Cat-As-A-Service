package org.example;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class UDPClient {
    private DatagramSocket datagramSocket;  // socket for communication
    private InetAddress inetAddress;    // IP address of server that we're going to be sending packet
    private byte[] buffer;  // info that's going to be sent
    private final int size = 4; // size of datagram

    public UDPClient(DatagramSocket datagramSocket, InetAddress inetAddress) {
        this.datagramSocket = datagramSocket;
        this.inetAddress = inetAddress;
    }

    private String[] messageConverter(String origMsg){
        double linesTmp = Math.ceil(origMsg.length() / (float)size);
        int lines = (int)linesTmp;
        String[] msgArr = new String[lines];
        int position = 0;
        while (origMsg.length() >= size) {
            StringBuilder part = new StringBuilder();
            part.append(position);
            part.append("|");
            part.append(origMsg.substring(0, size));
            origMsg = origMsg.substring(size);
            msgArr[position] = String.valueOf(part);
            position++;
        }
        if (origMsg.length() > 0){
            int dif = size - origMsg.length();
            StringBuilder part = new StringBuilder();
            part.append(position);
            part.append("|");
            part.append(origMsg);
            for (int i = 0; i < dif; i++)
                part.append("&");
            msgArr[position] = String.valueOf(part);
        }
        return msgArr;
    }

    private void sendParts(String[] partsArr) throws IOException {
        for (String part : partsArr){
            buffer = part.getBytes();
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, inetAddress, 1234); // inetAddress - is where we want to send it to, port is hardcoded have to change
            datagramSocket.send(datagramPacket);
        }
        buffer = "".getBytes();
    }

    private void receiveResponse() throws IOException { //тут застряли, нужно отсторитровать сообщения, обработать приходящие строки и выывести на экран нотрмально
        String resMsg = "";
        while(true) {
            //удалить первые 2 символа
            String str = "000000000000";
            buffer = str.getBytes();
            //byte[] buffer = new byte[512];
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            datagramSocket.receive(response);
            String messageFromServer = new String(response.getData(), 0, response.getLength());
            messageFromServer = messageFromServer.substring(2);
            resMsg = resMsg + messageFromServer;
            if (resMsg.indexOf('&') != -1){
                resMsg = resMsg.replaceAll("&", "");
                break;
            }
        }
        System.out.println(resMsg);
    }

    public void sendThenReceive() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                System.out.println("""
                        1) Go back to menu -> Enter "END"
                        2) Enter your nickname and dish in format "@Name - Dish~\"""");
                String inputString = scanner.nextLine();
                if (inputString.equals("END")){
                    break;
                }
                String[] msgArr = messageConverter(inputString);
                sendParts(msgArr);
                ////////////////////////////////////////////---SEND-REQUEST---/////////////////////////////////////////
//                buffer = inputString.getBytes(); // уже тут нужно передавать разделенные кусочки
//                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, inetAddress, 1234); // inetAddress - is where we want to send it to, port is hardcoded have to change
//                datagramSocket.send(datagramPacket);
                ////////////////////////////////////////---RECEIVE-RESPONSE---//////////////////////////////////////////

                receiveResponse();
                //byte[] buffer = new byte[512];  // allocate bytes for response message
                //DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                //datagramSocket.receive(response);
                //String messageFromServer = new String(response.getData(), 0, response.getLength());
                //System.out.println(messageFromServer);

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public static void main() throws SocketException, UnknownHostException {
        DatagramSocket datagramSocket = new DatagramSocket();
        InetAddress inetAddress = InetAddress.getByName("5.42.220.74"); // server IP destination address
        UDPClient client = new UDPClient(datagramSocket, inetAddress); // PORT + IPADDRESS/ порядок_сообщения_id/содержание сообщения
        client.sendThenReceive();
    }

}