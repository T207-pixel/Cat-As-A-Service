package org.example;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UDPServer {
    private final DatagramSocket datagramSocket;
    private InetAddress inetAddress;
    private int port;
    private byte[] buffer = new byte[12];    //messages which is sent from client are stored in this buffer
    private List<Map<Integer, String>> listOfMaps = new ArrayList<>();
    private Map<Integer, Integer> additionalList = new HashMap<>();
    private StringBuilder formedMsg = new StringBuilder();
    private int id;

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
            return "~ Eaten by the Cat&";
        }
        else {
            System.out.println("Entered dish is absent");
            return "~ The Cat is amused by #" + offeredDish + "&" + "\n";
        }

    }

    private void receivedMessages() throws IOException, SQLException {  //вероятно придется использовать отдельный поток
        //while(true) {
            String str = "000000000000";
            buffer = str.getBytes();
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
            datagramSocket.receive(datagramPacket);
            inetAddress = datagramPacket.getAddress();
            port = datagramPacket.getPort();
            String address = inetAddress.toString().replaceAll("^.", "").replaceAll("\\.", "");
            id = datagramPacket.getPort() + Integer.parseInt(address);
            String recStrPart = new String(datagramPacket.getData(), datagramPacket.getOffset(), datagramPacket.getLength()) + "|" + id;
            String[] part = divideStrOnParts(recStrPart);
            insertInMatrix(part); //тут ломается
            if (ifTilda()){
                stingProcessing();
            }
            System.out.println(listOfMaps);
        //}

    }

    private String[] divideStrOnParts(String str){ //работает неверно
        String[] outputStr = new String[3];
        for (int i = 0; i < 2; i++){
            int present = str.indexOf('|');
            if (present != -1){
                outputStr[i] = str.substring(0, present);
                str = str.substring(present + 1);
            }
        }
        outputStr[2] = str;
        return outputStr;
    }

    private void insertInMatrix(String[] part){
        int place = Integer.parseInt(part[0]);
        String textPart = part[1];
        int id = Integer.parseInt(part[2]);;
        Map<Integer, String> fragment = new HashMap<Integer, String>();
        int arrPlace = -1;
        fragment.put(place, textPart);
        for (Map.Entry<Integer, Integer> entry : additionalList.entrySet()) {
            if (entry.getValue().equals(id)) {
                arrPlace = entry.getKey();
            }
        }
        if (arrPlace >= 0){
            listOfMaps.get(arrPlace).put(place, textPart);
        } else {
            additionalList.put(listOfMaps.size(), id);
            listOfMaps.add(fragment);
            arrPlace = 0;
        }
    }

    private boolean ifTilda(){
        String str = "";
        for (int i = 0; i < listOfMaps.size(); i++) {
            for (int j = 1; j <= listOfMaps.get(i).size(); j++) {
                Object obj = listOfMaps.get(i).get(j);
                if (obj != null){
                    str = obj.toString();
                    if (str.indexOf('~') != -1){
                        if (checkLength(i)){
                            return true;
                        }
                    }

                }
            }
        }
        return false;
    }

    private boolean checkLength(int mapIndex){
        int testCounter = 0;
        //for (int i = 0; i < listOfMaps.size(); i++){
            for (int j = 0; j < listOfMaps.get(mapIndex).size(); j++){
                if (listOfMaps.get(mapIndex).containsKey(j))
                    testCounter++;
            }
            if (testCounter == listOfMaps.get(mapIndex).size()){
                createMsg(mapIndex);  //NEW
                return true;
            }
        return false;
    }

    private void createMsg(int mapIndex){  //NEW
        for (int i = 0; i < listOfMaps.get(mapIndex).size(); i++){
            String str = listOfMaps.get(mapIndex).get(i);
            formedMsg.append(str);
        }
        listOfMaps.remove(listOfMaps.get(mapIndex));
        additionalList.remove(mapIndex);
    }

    private void stingProcessing() throws SQLException, IOException { //NEW
        String messageFromClient = formedMsg.toString();
        messageFromClient = messageFromClient.replaceAll("&", "");
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
            formedMsg.delete(0, formedMsg.length());
            formedMsg.append(msg);
            sendMessage();
            // дальше какой-то код с буффером 3 строки нужно опять разбить сообщение от сервера на нужные куски и отправить
        }
    }

    private void sendMessage() throws IOException {
        if (!formedMsg.isEmpty()){
            sendParts(messageConverter());
        }
    }

    private String[] messageConverter(){
        int size = buffer.length - 3;
        String sendingMsg = String.valueOf(formedMsg);
        double linesTmp = Math.ceil(sendingMsg.length() / (float)size);
        int lines = (int)linesTmp;
        String[] msgArr = new String[lines];
        int position = 0;
        while (sendingMsg.length() >= size) {
            StringBuilder part = new StringBuilder();
            part.append(position);
            part.append("|");
            part.append(sendingMsg.substring(0, size));
            sendingMsg = sendingMsg.substring(size);
            msgArr[position] = String.valueOf(part);
            position++;
        }
        if (sendingMsg.length() > 0){
            int dif = size - sendingMsg.length();
            StringBuilder part = new StringBuilder();
            part.append(position);
            part.append("|");
            part.append(sendingMsg);
            for (int i = 0; i < dif; i++)
                part.append("&");
            msgArr[position] = String.valueOf(part);
        }
        return msgArr;
    }

    private void sendParts(String[] partsArr) throws IOException {
        //DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
        //InetAddress inetAddress = datagramPacket.getAddress();
        //int port = datagramPacket.getPort();
        for (String part : partsArr){
            buffer = part.getBytes();
            DatagramPacket response = new DatagramPacket(buffer, buffer.length, inetAddress, port);
            datagramSocket.send(response);
        }
        buffer = "".getBytes();
        formedMsg.setLength(0);
    }


    public void receiveThenSend() {
        while(true) {
            try {
                receivedMessages();

            } catch (IOException e) {
                e.printStackTrace();
                break;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public static void main(String[] args) throws SocketException, SQLException {
        DatagramSocket datagramSocket = new DatagramSocket(1234);
        UDPServer server = new UDPServer(datagramSocket);
        server.receiveThenSend();

//        String[] arr1 = {"2", "aaa", "2109"};
//        String[] arr3 = {"1", "ccc", "333"};
//        String[] arr2 = {"2", "bbb", "333"};
//        String[] arr4 = {"3", "dda~", "333"};
//        server.insertInMatrix(arr1);
//        server.insertInMatrix(arr3);
//        server.insertInMatrix(arr2);
//        server.insertInMatrix(arr4);
//        if (server.ifTilda()) {
////            if (server.checkLength()) {
////                server.stingProcessing();   // протестировать
////            }
//        }
    }





}