package com.example.browserfx;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer extends Application {

    private static final int PORT = 8080;
    private static final String RESPONSE_TEXT = "Welcome to Chouaib's Server!";
    private boolean isRunning = false;
    private ServerSocket serverSocket;

    @Override
    public void start(Stage primaryStage) throws Exception {

    }

    public void startServer() throws IOException {
        if (!isRunning) {
            serverSocket = new ServerSocket(PORT);
            isRunning = true;
            System.out.println("Server listening on port: " + PORT);

            new Thread(() -> {
                try {
                    while (!serverSocket.isClosed()) {
                        Socket clientSocket = serverSocket.accept();
                        handleClientRequest(clientSocket);
                    }
                } catch (IOException e) {
                    if (!serverSocket.isClosed()) {
                        e.printStackTrace();
                    }
                } finally {
                    stopServer();
                }
            }).start();
        }
    }


    public void stopServer() {
        if (isRunning && serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                isRunning = false;
            }
        }
    }


    public boolean isRunning() {
        return isRunning;
    }

    private void handleClientRequest(Socket clientSocket) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             DataOutputStream writer = new DataOutputStream(clientSocket.getOutputStream())) {
            reader.readLine();

            writer.writeBytes("HTTP/1.1 200 OK\r\n");
            writer.writeBytes("Content-Type: text/plain\r\n");
            writer.writeBytes("\r\n");
            writer.writeBytes(RESPONSE_TEXT);
            writer.flush();
        } finally {
            clientSocket.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
