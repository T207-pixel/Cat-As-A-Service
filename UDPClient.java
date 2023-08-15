package org.example;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class UDPClient {
    private DatagramSocket datagramSocket;  // socket for communication
    private InetAddress inetAddress;    // IP address of server that we're going to be sending packet
    private byte[] buffer;  // info that's going to be sent
    private final int size = 4; // size of datagram
    String[] resMsg = new String[10];

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
            String str = "000000000000";
            buffer = str.getBytes();
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            datagramSocket.receive(response);
            String messageFromServer = new String(response.getData(), 0, response.getLength());
            String[] part = divideStrOnParts(messageFromServer);
            if (ifTilda(insertData(part))){
                break;
            }
        }
    }

    private String[] divideStrOnParts(String str){
        String[] outputStr = new String[2];
        int present = str.indexOf('|');
        outputStr[0] = str.substring(0, present);
        outputStr[1] = str.substring(present + 1);
        return outputStr;
    }

    private int insertData(String[] part){
        int place = Integer.parseInt(part[0]);
        String textPart = part[1];
        resMsg[place] = textPart;
        return place;
    }

    private boolean ifTilda(int place){
        StringBuilder outputMsg = new StringBuilder("");
        for (int i = 0; i <= place; i++){
            if (resMsg[i] == null){
                return false;
            }
            if (resMsg[i].indexOf('&') != -1){
                resMsg[i] = resMsg[i].replaceAll("\\n", "").replaceAll("&", "");
                for (int j = 0; j <= place; j++){
                    outputMsg.append(resMsg[j]);
                }
                System.out.println(outputMsg);
                for (String item : resMsg){
                    item = null;
                }
                return true;
            }
        }
        return false;
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
                receiveResponse();
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