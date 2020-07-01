package ecutb.loffe.hangman.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ecutb.loffe.hangman.entities.Player;
import ecutb.loffe.hangman.R;
import ecutb.loffe.hangman.database.DBHelper;

public class LoginActivity extends AppCompatActivity {

    EditText name, password;
    Button loginBtn, addBtn, apiBtn;
    Player player;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        name = findViewById(R.id.editTextUserName);
        password = findViewById(R.id.editTextPassword);
        loginBtn = findViewById(R.id.btnLogin);
        addBtn = findViewById(R.id.btnAddUser);
        apiBtn = findViewById(R.id.btnAPI);
        dbHelper = new DBHelper(this);

    }

    // When clicking Login, validate credentials
    // If ok, save shard preferences and start GameActivity
    // Else, show dialog
    public void clickLogin(View view) {
        if (dbHelper.validate(name.getText().toString().toUpperCase(), password.getText().toString())) {
            Intent intent = new Intent(LoginActivity.this, GameActivity.class);
            if (setPlayer(name.getText().toString().toUpperCase())) {
                startActivity(intent);
            }
        } else {
            AlertDialog.Builder alertDB = new AlertDialog.Builder(LoginActivity.this);
            alertDB.setMessage("Incorrect login credentials.");
            alertDB.setPositiveButton("I'll try again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                }
            });
            AlertDialog noLogin = alertDB.create();
            noLogin.show();
        }
    }

    // Add username to shared preferences
    public boolean setPlayer(String username){
        SharedPreferences sp = getSharedPreferences("session", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username", username);
        editor.apply();
        return true;
    }

    // Adding player. Just enter a unique name and a password with length > 0.
    // If username length is 0 or username exists in db, show dialog.
    public void clickAdd(View view) {
        player = new Player(-1, name.getText().toString().toUpperCase(), password.getText().toString());
        if (dbHelper.addPlayer(player)) {
            Toast.makeText(LoginActivity.this, "added " + player.getUsername(), Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder alertDB = new AlertDialog.Builder(LoginActivity.this);
            if (player.getUsername().length() < 1) {
                alertDB.setMessage("Player name is empty.\nPlayer not added.");
            } else {
                alertDB.setMessage("Player '" + player.getUsername() + "' already exists.\nPlayer not added.");
            }
            alertDB.setPositiveButton("Alright", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                }
            });
            AlertDialog noAdd = alertDB.create();
            noAdd.show();
        }
    }

    // API button, doesn't need login for entering API Activity
    public void clickAPI(View view) {
        startActivityForResult(new Intent(LoginActivity.this, APIActivity.class), 1);
    }
}