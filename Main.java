package Controller;

import java.io.*;
import java.net.*;
import java.util.Scanner;

class Client extends Thread {
    private volatile boolean running = true;

    @Override
    public void run() {
        try {
            // Kết nối đến server có địa chỉ IP localhost và cổng 1234
            Socket socket = new Socket("localhost", 1234);

            // Tạo một luồng đầu ra để gửi dữ liệu tới server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Tạo một luồng đầu vào để nhận dữ liệu từ server
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Scanner để đọc dữ liệu từ người dùng
            Scanner scanner = new Scanner(System.in);

            while (running) {
                // Nhập và gửi dữ liệu cho server
                System.out.print("Nhập dữ liệu để gửi tới server (nhập '#' để đóng kết nối): ");
                String message = scanner.nextLine();
                out.println(message);

                if (message.equals("#")) { // Nếu người dùng nhập '#', đóng kết nối
                    running = false;
                }

                // Đọc và hiển thị dữ liệu từ server
                String serverResponse = in.readLine();
                System.out.println("Server gửi: " + serverResponse);
            }

            // Đóng các luồng và socket
            scanner.close();
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Server extends Thread {
    private volatile boolean running = true;

    @Override
    public void run() {
        try {
            // Tạo một server socket ở cổng 1234
            ServerSocket serverSocket = new ServerSocket(1234);

            System.out.println("Server đã khởi động và đang chờ kết nối...");

            // Chấp nhận kết nối từ client
            Socket clientSocket = serverSocket.accept();

            System.out.println("Client đã kết nối!");

            // Tạo một luồng đầu vào để nhận dữ liệu từ client
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Tạo một luồng đầu ra để gửi dữ liệu tới client
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            while (running) {
                // Đọc và hiển thị dữ liệu từ client
                String clientMessage = in.readLine();
                System.out.println("Client gửi: " + clientMessage);

                if (clientMessage.equals("#")) { // Nếu client gửi '#', đóng kết nối
                    running = false;
                }

                // Nhập và gửi dữ liệu cho client
                Scanner scanner = new Scanner(System.in);
                System.out.print("Nhập dữ liệu để gửi tới client (nhập '#' để đóng kết nối): ");
                String serverResponse = scanner.nextLine();
                out.println(serverResponse);

                if (serverResponse.equals("#")) { // Nếu người dùng nhập '#', đóng kết nối
                    running = false;
                }
            }

            // Đóng các luồng và socket
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        Client client = new Client();

        server.start();
        client.start();
    }
}
