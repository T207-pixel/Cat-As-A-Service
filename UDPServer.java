package org.example;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UDPServer {
    private final DatagramSocket datagramSocket;
    private byte[] buffer = new byte[256];    //messages which is sent from client are stored in this buffer

    public UDPServer(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
    }

    private static String getClientInputParams(String msg, String regex){
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(msg);
        boolean matchFound = matcher.find();
        if(matchFound) {
            return matcher.group(0);
        } else {
            return "Incorrect format, in name or dish";
        }
    }

    private static String dishPresentsInMenu(String userName, String offeredDish) throws SQLException {
        Map<Integer, String> dishesSet = DatabaseLink.getCatsMenu();
        boolean dishPresents = dishesSet.containsValue(offeredDish);
        if (dishPresents) {
            DatabaseLink.addRecordInFeedList(userName, offeredDish);
            System.out.println("Dish present in cats menu");
            return "~ Eaten by the Cat";
        }
        else {
            System.out.println("Entered dish is absent");
            return "~ The Cat is amused by #" + offeredDish + "\n";
        }

    }

    public void receiveThenSend() {
        while(true) {
            try {
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);  // implements a connectionless packet delivery service (no connection between client and server,
                                                                                            // each packet sent or received on a datagram socket is individually addressed and routed
                                                                                            // buffer - holds data that receives from client. buffer.length - quantity of symbols that should be read from a datagram
                datagramSocket.receive(datagramPacket); // blocking method, program will be halted here until this method returns
                InetAddress inetAddress = datagramPacket.getAddress();      //IP information that it came from
                                                                            // that IP is used to send a datagram back to the client
                int port = datagramPacket.getPort(); //we need a port number that the packet was sent over
                String messageFromClient = new String(datagramPacket.getData(), datagramPacket.getOffset(), datagramPacket.getLength()); // 2param - offset for reading from start
                String msg;

                if (messageFromClient.equals("END")){
                    msg = "Current session is stopped";
                    System.out.println("END");
                    //break;
                } else {
                    messageFromClient = messageFromClient.replaceAll(".$", "");
                    String clientName = UDPServer.getClientInputParams(messageFromClient, "\\b\\w+");
                    String clientDish = UDPServer.getClientInputParams(messageFromClient, "\\b\\w+$");
                    System.out.print("Message from client: " + messageFromClient + "\nClient name: " +
                            clientName + "\nClient dish: " + clientDish + "\n");
                    msg = UDPServer.dishPresentsInMenu(clientName, clientDish);
                }
                buffer = msg.getBytes();
                DatagramPacket response = new DatagramPacket(buffer, buffer.length, inetAddress, port);
                datagramSocket.send(response);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) throws SocketException {
        DatagramSocket datagramSocket = new DatagramSocket(1234);
        UDPServer server = new UDPServer(datagramSocket);
        server.receiveThenSend();
    }

}