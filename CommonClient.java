package org.example;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class CommonClient {
    public static void main(String[] args) throws IOException {
//        Thread thread1 = new Thread(UDPServerTask);
//        thread1.start();
//        Thread thread2 = new Thread(TCPServerTask);
//        thread2.start();
        boolean run = true;
        while (run) {
            System.out.print("""
                    Enter from options below please:
                    1 - Treat cat with a dish
                    2 - Stroke cat
                    (If you want to escape from program enter - 3)
                    Make your choice:\s""");
            Scanner scanner = new Scanner(System.in);
            int option;
            try {
                option = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                //e.printStackTrace();
                option = -1;
            }
            switch (option) {
                case 1 -> UDPClient.main();
                case 2 -> TCPClient.main();
                case 3 -> run = false;
                default -> System.out.println("Enter 1 or 2");
            }
        }
    }

//    static Runnable UDPServerTask = () -> {
//        try {
//            UDPServer.main();
//        } catch (SocketException e) {
//            throw new RuntimeException(e);
//        }
//    };

//    static Runnable TCPServerTask = () -> {
//      try {
//          TCPServer.main();
//      } catch (IOException | SQLException e) {
//          throw new RuntimeException(e);
//      }
//    };
}
