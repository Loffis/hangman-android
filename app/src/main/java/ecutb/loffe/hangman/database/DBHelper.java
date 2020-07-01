package ecutb.loffe.hangman.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ecutb.loffe.hangman.entities.Player;
import ecutb.loffe.hangman.entities.Score;

public class DBHelper extends SQLiteOpenHelper {

    private static final String PLAYER_TABLE = "PLAYER_TABLE";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    private static final String SCORE_TABLE = "SCORE_TABLE";
    private static final String COLUMN_SCORE = "score";

    public DBHelper(@Nullable Context context) {
        super(context, "hangman.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create player table
        db.execSQL("CREATE TABLE " + PLAYER_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_USERNAME + " TEXT NOT NULL UNIQUE, " + COLUMN_PASSWORD + " TEXT NOT NULL);");

        // Create score table
        db.execSQL("CREATE TABLE " + SCORE_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_USERNAME + " TEXT, " + COLUMN_SCORE + " INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    // add score to database
    public boolean addScore(String username, int score) {
        Player player = findPlayerByName(username);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_USERNAME, player.getUsername());
        cv.put(COLUMN_SCORE, score);

        if (db.insert(SCORE_TABLE, null, cv) != -1) {
            db.close();
            return true;
        } else {
            db.close();
            return false;
        }
    }

    // add player to database
    public boolean addPlayer(Player player) {
        if (player.getUsername().length() == 0) {
            return false;
        }
        if (checkIfUserExists(player.getUsername())) {
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(COLUMN_USERNAME, player.getUsername());
        cv.put(COLUMN_PASSWORD, player.getPassword());

        if (db.insert(PLAYER_TABLE, null, cv) != -1) {
            db.close();
            return true;
        } else {
            db.close();
            return false;
        }
    }

    // find player by searching for user name
    public Player findPlayerByName(String username) {
        Player player = new Player(-1, "", "");
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + PLAYER_TABLE + " WHERE " + COLUMN_USERNAME + " = " + "'" + username + "'", null);
        if (cursor.moveToFirst()){
            int id = cursor.getInt(0);
            username = cursor.getString(1);
            String password = cursor.getString(2);

            player = new Player(id, username, password);
            /*
            player = new Player(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD)));

             */
        }
        cursor.close();
        db.close();
        return player;
    }

    // get a list of all players in the database
    // not used for the moment...
    public List<Player> getAllPlayers() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Player> players = new ArrayList<>();
        String getAllPlayersQuery = "SELECT * FROM " + PLAYER_TABLE;
        Cursor cursor = db.rawQuery(getAllPlayersQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String username = cursor.getString(1);
                String password = cursor.getString(2);

                Player player = new Player(id, username, password);
                players.add(player);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return players;
    }

    // check if username & password has a match in the database
    public boolean validate(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + PLAYER_TABLE + " WHERE " + COLUMN_USERNAME + " = " + "'" + username + "'" + " AND "
                + COLUMN_PASSWORD + " = " + "'" + password + "'", null);

        if (cursor.getCount() > 0) {
            cursor.close();
            db.close();
            return true;
        } else {
            cursor.close();
            db.close();
            return false;
        }
    }

    // check if username exists in the database
    public boolean checkIfUserExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + PLAYER_TABLE + " WHERE " + COLUMN_USERNAME + " = " + "'" + username + "'", null);

        if (cursor.getCount() > 0) {
            cursor.close();
            db.close();
            return true;
        } else {
            cursor.close();
            db.close();
            return false;
        }
    }

    // Get all scores
    public List<Score> getHighscores() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + SCORE_TABLE, null);
        List<Score> highscoreList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String username = cursor.getString(1);
                int score = cursor.getInt(2);
                Score scoreObj = new Score(id, username, score);
                highscoreList.add(scoreObj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return highscoreList;
    }
}
