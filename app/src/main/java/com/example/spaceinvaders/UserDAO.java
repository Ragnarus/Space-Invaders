package com.example.spaceinvaders;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDAO {
    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase database;

    private static final String[] allColumns = {
            DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_NAME,
            DatabaseHelper.COLUMN_SCORE
    };

    public UserDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Create
    public void insertUser(String name, int score) {
        // Check if the database has 10 users
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, new String[]{"COUNT(*) AS count"},
                null, null, null, null, null);
        cursor.moveToFirst();
        int userCount = cursor.getInt(cursor.getColumnIndexOrThrow("count"));
        cursor.close();


        if(score>getLowestScore()){
            // If there are already 10 users, delete the one with the lowest score and the highest ID
            if (userCount >= 10) {
                Cursor lowestScoreCursor = database.query(DatabaseHelper.TABLE_NAME, allColumns,
                        null, null, null, null, DatabaseHelper.COLUMN_SCORE + " ASC, " + DatabaseHelper.COLUMN_ID + " DESC", "1");

                if (lowestScoreCursor != null && lowestScoreCursor.moveToFirst()) {
                    int id = lowestScoreCursor.getInt(lowestScoreCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                    database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
                    lowestScoreCursor.close();
                }
            }

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_NAME, name);
            values.put(DatabaseHelper.COLUMN_SCORE, score);
            database.insert(DatabaseHelper.TABLE_NAME, null, values);
        }
    }

    public int getLowestScore() {
        int lowestScore = -1; // Standardwert, falls keine Daten vorhanden sind

        // Query to get the lowest score from the table
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, new String[]{DatabaseHelper.COLUMN_SCORE},
                null, null, null, null, DatabaseHelper.COLUMN_SCORE + " ASC", "1");

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                lowestScore = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SCORE));
            }
            cursor.close();
        }

        return lowestScore;
    }
    // Read
    public Cursor getAllUsers() {
        return database.query(DatabaseHelper.TABLE_NAME, allColumns,
                null, null, null, null, DatabaseHelper.COLUMN_SCORE+" DESC");
    }


    // Delete
    public void deleteUser(int id) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // Delete user with the lowest score, and if there are ties, the one with the highest ID (most recently added)
    public void deleteUserWithLowestScore() {
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, allColumns,
                null, null, null, null, DatabaseHelper.COLUMN_SCORE + " ASC, " + DatabaseHelper.COLUMN_ID + " DESC", "1");

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
            database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
            cursor.close();
        }
    }
}

