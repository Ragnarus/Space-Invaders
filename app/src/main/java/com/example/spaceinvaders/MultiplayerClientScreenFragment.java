package com.example.spaceinvaders;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MultiplayerClientScreenFragment extends Fragment {

    private SocketHandler socketHandler;
    private String ipAddress;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_multiplayer_client_screen, container, false);
        EditText messageInput = view.findViewById(R.id.message_input);
        Button sendButton = view.findViewById(R.id.send_button);
        ipAddress = getArguments().getString("IP_ADDRESS");
        Log.d("MultiplayerServerScreenFragment", "Received data: " + ipAddress);

        new Thread(() -> {
            try {
                socketHandler = new SocketHandler();
                socketHandler.connectToServer(ipAddress, 12345); // IP-Adresse und Port des Servers


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

        return view;
    }
    public void getmessage(String message){
        Toast.makeText(getContext(), "Received: " + message, Toast.LENGTH_LONG).show();
    }

    public void sendmessage(String message){
        new SendMessageTask(socketHandler).execute(message); // Verwenden Sie AsyncTask zum Senden der Nachricht
        Toast.makeText(getContext(), "Message sent", Toast.LENGTH_SHORT).show();
    }
}