package com.example.spaceinvaders.Multiplayer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SendMessageTask {

    private static ExecutorService executor = Executors.newSingleThreadExecutor();
    private SocketHandler socketHandler;

    public SendMessageTask(SocketHandler socketHandler) {
        this.socketHandler = socketHandler;
    }

    public void execute(String message) {
        executor.execute(() -> {
            try {
                socketHandler.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}


