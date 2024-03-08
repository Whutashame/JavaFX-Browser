package com.example.browserfx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Controller {

    @FXML
    private Button requestButton;

    @FXML
    private TextArea responseArea;

    @FXML
    private void initialize() {
        requestButton.setOnAction(event -> {
            try {
                String response = getResponseFromServer();
                responseArea.setText(response);
            } catch (IOException e) {
                e.printStackTrace();
                responseArea.setText("Error: Failed to communicate with server.");
            }
        });
    }

    @FXML
    private Circle statusCircle;

    @FXML
    private Button toggleServerButton;
    @FXML
    private TextField urlInputArea;

    private HttpServer server;
    private Thread serverThread;

    public void toggleServer() {
        if (server == null || !server.isRunning()) {
            server = new HttpServer();
            serverThread = new Thread(() -> {
                try {
                    server.startServer();
                    Platform.runLater(() -> updateUI(true));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            serverThread.start();
        } else {
            server.stopServer();
            updateUI(false);
            server = null;
        }
    }

    private void updateUI(boolean isServerRunning) {
        if (isServerRunning) {
            statusCircle.setFill(Color.GREEN);
            toggleServerButton.setText("Turn Off");
        } else {
            statusCircle.setFill(Color.RED);
            toggleServerButton.setText("Turn On");
        }
    }

    private String getResponseFromServer() throws IOException {
        String urlString = urlInputArea.getText().trim();

        // Validate the URL string
        if (urlString.isEmpty()) {
            return "Error: URL is empty.";
        }
        if (!urlString.startsWith("http://") && !urlString.startsWith("https://")) {
            urlString = "http://" + urlString;
        }

        URL targetURL = new URL(urlString);
        URLConnection connection = targetURL.openConnection();
        connection.connect();

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
                response.append("\n");
            }
        }
        return response.toString();
    }
}
