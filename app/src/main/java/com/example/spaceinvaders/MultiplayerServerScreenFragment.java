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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
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
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Server started", Toast.LENGTH_SHORT).show());

                // Start receiving messages
                socketHandler.receiveMessages(message -> getActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Received: " + message, Toast.LENGTH_LONG).show()
                ));

                getActivity().runOnUiThread(() -> {
                    sendButton.setOnClickListener(v -> {
                        String message = messageInput.getText().toString();
                        new SendMessageTask(socketHandler).execute(message); // Verwenden Sie AsyncTask zum Senden der Nachricht
                        Toast.makeText(getContext(), "Message sent", Toast.LENGTH_SHORT).show();
                    });
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        return  view;
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