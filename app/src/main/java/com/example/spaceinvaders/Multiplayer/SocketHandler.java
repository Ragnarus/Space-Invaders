package com.example.spaceinvaders.Multiplayer;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketHandler {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader in;
    private BufferedWriter out;

    // Method to start the server and listen for incoming connections
    public void startServer(int port) throws Exception {
        serverSocket = new ServerSocket(port);
        clientSocket = serverSocket.accept(); // Wait for a client to connect
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
    }

    // Method to connect to a server (host)
    public void connectToServer(String host, int port) throws Exception {
        clientSocket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
    }

    // Method to send a message
    public void sendMessage(String message) throws Exception {
        if (out != null) {
            out.write(message + "\n");
            out.flush();
        }
    }

    public void receiveMessages(MessageListener listener) {
        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    listener.onMessageReceived(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Close resources
    public void close(){
        Log.e("SocketHandler", "in is: " +in);
        if (in != null) {
            Log.e("SocketHandler", "in is null");
            try {
                in = null;
                Log.e("SocketHandler", "close input stream");
            } catch (Exception e) {
                Log.e("SocketHandler", "Failed to close input stream: " + e.getMessage());
            }
        }
        if (out != null) {
            Log.e("SocketHandler", "out is null");
            try {
                out= null;
                Log.e("SocketHandler", "close output stream:");
            } catch (Exception e) {
                Log.e("SocketHandler", "Failed to close output stream: " + e.getMessage());
            }
        }
        if (clientSocket != null) {
            try {
                clientSocket.close();
                Log.e("SocketHandler", "close client socket");
            } catch (Exception e) {
                Log.e("SocketHandler", "Failed to close client socket: " + e.getMessage());
            }
        }
        if (serverSocket != null) {
            try {
                serverSocket.close();
                Log.e("SocketHandler", "close server socket");
            } catch (Exception e) {
                Log.e("SocketHandler", "Failed to close server socket: " + e.getMessage());
            }
        }
    }

    public interface MessageListener {
        void onMessageReceived(String message);
    }
}

