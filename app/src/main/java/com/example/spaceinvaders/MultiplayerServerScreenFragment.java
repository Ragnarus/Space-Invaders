package com.example.spaceinvaders;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MultiplayerServerScreenFragment extends Fragment {
    private SocketHandler socketHandler;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_multiplayer_server_screen, container, false);

        EditText messageInput = view.findViewById(R.id.message_input);
        Button sendButton = view.findViewById(R.id.send_button);

        new Thread(() -> {
            try {
                socketHandler = new SocketHandler();
                socketHandler.startServer(12345); // Port-Nummer
                ServerStart();

                // Start receiving messages
                socketHandler.receiveMessages(message -> getActivity().runOnUiThread(() ->
                        getmessage(message)
                ));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        sendButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString();
            sendmessage(message);
        });

        return  view;
    }

    public void ServerStart(){
        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Server started", Toast.LENGTH_SHORT).show());
        String message1 = "Start";
        new SendMessageTask(socketHandler).execute(message1);
    }

    public void getmessage(String message){
        Toast.makeText(getContext(), "Received: " + message, Toast.LENGTH_LONG).show();
    }

    public void sendmessage(String message){
        new SendMessageTask(socketHandler).execute(message); // Verwenden Sie AsyncTask zum Senden der Nachricht
        Toast.makeText(getContext(), "Message sent", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (socketHandler != null) socketHandler.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}