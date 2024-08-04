package com.example.spaceinvaders;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

public class MultiplayerLobbyFragment extends Fragment {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_multiplayer_lobby, container, false);

        Button startServerButton = view.findViewById(R.id.start_server_button);
        Button startClientButton = view.findViewById(R.id.start_client_button);
        EditText ipAddressInput = view.findViewById(R.id.ip_address_input);
        TextView ipAddressDisplay = view.findViewById(R.id.ip_address_display);

        String ownipAddress = getDeviceIPAddress();
        ipAddressDisplay.setText("Your IP Address: " + ownipAddress);

        startServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        startClientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipAddress = ipAddressInput.getText().toString();
                if (!ipAddress.isEmpty()) {

                }
            }
        });
        return view;
    }
    private String getDeviceIPAddress() {
        try {
            List<NetworkInterface> networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : networkInterfaces) {
                List<java.net.InetAddress> inetAddresses = Collections.list(networkInterface.getInetAddresses());
                for (java.net.InetAddress inetAddress : inetAddresses) {
                    if (!inetAddress.isLoopbackAddress() && inetAddress.isSiteLocalAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "Not Available";
    }
}