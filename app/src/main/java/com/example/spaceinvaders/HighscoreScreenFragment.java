package com.example.spaceinvaders;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class HighscoreScreenFragment extends Fragment {

    private UserDAO userDAO;
    private TextView resultView;
    private Button back;
    public HighscoreScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDAO = new UserDAO(getContext());
        userDAO.open();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_highscore_screen, container, false);
        resultView = view.findViewById(R.id.resultView);
        back = view.findViewById(R.id.backbutton);

        back.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, StartScreenFragment.class, null).commit();
        });

        displayAllUsers();
        return view;
    }

    private void displayAllUsers() {
        Cursor cursor = userDAO.getAllUsers();
        StringBuilder result = new StringBuilder();
        int id = 1;
        while (cursor.moveToNext()) {
            //int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
            int score = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SCORE));
            result.append(id).append(": ").append(name).append(" - ").append(score).append("\n");
            id++;
        }
        resultView.setText(result.toString());
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Close the database
        if (userDAO != null) {
            userDAO.close();
        }
    }
}