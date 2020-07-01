package ecutb.loffe.hangman.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import ecutb.loffe.hangman.R;

public class GameActivity extends AppCompatActivity {

    private ImageView hangmanImage;
    private TextView secretWordTextView, guessedLetters, scoreBoard;
    private EditText guessedChar;
    private Button btnGuess;

    private String secretWord;
    private String displayString;
    private StringBuilder usedLetters;
    private char[] displayChars;
    private int wrongGuesses, score;
    public final int MAXGUESS = 7;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        hangmanImage = findViewById(R.id.hangmanImageView);
        secretWordTextView = findViewById(R.id.secretWordTextView);
        guessedChar = findViewById(R.id.editTextGuess);
        btnGuess = findViewById(R.id.btnGuess);
        guessedLetters = findViewById(R.id.wrongLettersView);
        scoreBoard = findViewById(R.id.scoreView);

        sp = getSharedPreferences("session", 0);
        String player = sp.getString("username", "no player");

        // set title
        Objects.requireNonNull(getSupportActionBar()).setTitle("Hangman - " + player);

        // show notification
        showNotification("Hangman", "Welcome to the game!");

        init();
        showWordAndScore();
        game();
    }
    


    /**
     *  **** "@param" gives error, therefore "@" is left out. ****
     *
     * param secretWord     random word from getRandomWord()
     * param displayChars   char-array for working with the secret word during game
     *                      filled with "_" to start with
     * param usedLetters    StringBuilder with the guessed chars
     * param wrongGuesses   number of wrong guesses
     * param displayString  the string to display for the player
     * hangmanImage         image view
     * guessedChar          the last guuessed char
     * score                player's score
     */
    private void init() {
        secretWord = getRandomWord().toUpperCase();
        displayChars = new char[secretWord.length()];
        usedLetters = new StringBuilder();
        wrongGuesses = 0;

        Arrays.fill(displayChars, '_');
        displayString = fixDisplayWord(new String(displayChars));

        hangmanImage.setImageResource(R.drawable.pic0);
        guessedChar.setText("");

        score = 0;
    }

    /**
     * param secretWordTextView     textview to show displayString in
     * param guessedLetters         textview for guessed chars
     * param scoreBoard             textview for score
     */
    private void showWordAndScore() {
        secretWordTextView.setText(displayString);
        String wrongLetterString = "GUESSED: " + usedLetters;
        guessedLetters.setText(wrongLetterString);
        String scoreDisplay = "Score: " + score;
        scoreBoard.setText(scoreDisplay);
    }


    // When clicking Guess button, start game()
    // Entered char is evaluated in checkGuess()
    private void game() {
        btnGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGuess(guessedChar.getText().toString());
                showWordAndScore();
            }
        });
    }

    // Make guessed char to upperCase
    // Check if char exists in usedLetters
    // Check if userInput.length() is 0
    // If not 0 and not earlier guessed, evaluate char in guess()
    private boolean checkGuess(String userInput) {
        userInput = userInput.toUpperCase();
        String alreadyGuessed;
        if (!usedLetters.toString().contains(userInput)){
            return guess(userInput.charAt(0));
        }
        if (userInput.length() != 0) {
            alreadyGuessed = "'" + userInput + "'" + " is already guessed";
        } else {
            alreadyGuessed = "You haven't entered anything!";
        }
        Toast.makeText(GameActivity.this, alreadyGuessed, Toast.LENGTH_SHORT).show();
        return false;
    }

    // If char exists in secretWord:
    // add 1 to score
    // add the char to usedLetters
    // make Toast to inform player
    // update scoreboard and show chars guessed
    // If char doesn't exist in secretWord:
    // subtract 1 from score
    // add the char to usedLetters
    // make Toast to inform player
    // add 1 to wrongGuesses
    // check if wrongGuesses has reached MAXGUESSES
    public boolean guess(char letter){
        if(secretWord.contains(letter + "")){
            Toast.makeText(GameActivity.this, letter + " IS CORRECT!", Toast.LENGTH_LONG).show();
            score++;
            for(int i = 0; i < secretWord.length(); i++){
                if(letter == secretWord.charAt(i)){
                    displayChars[i] = letter;
                }
            }
            usedLetters.append(letter);
            displayString = fixDisplayWord(new String(displayChars));
            showWordAndScore();
            if (String.valueOf(displayChars).equals(secretWord)) {
                win();
            }
            return true;
        }
        Toast.makeText(GameActivity.this, letter + " IS WRONG!", Toast.LENGTH_SHORT).show();
        score--;
        wrongGuesses++;
        usedLetters.append(letter);
        if (wrongGuesses < MAXGUESS) {
            switchImage(hangmanImage);
        } else {
            switchImage(hangmanImage);
            lose();
        }
        return false;
    }

    // If GIVE UP button is clicked, show dialog
    // Giving up results in -5 pts
    // Giving up starts lose() method
    public void clickGiveUP(View view) {
        AlertDialog.Builder alertDB = new AlertDialog.Builder(GameActivity.this);
        alertDB.setMessage("Giving up results in -5 pts.");
        alertDB.setPositiveButton("Alright", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                score -= 5;
                lose();
            }
        }).setNegativeButton("No way!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onPause();
            }
        });
        AlertDialog dialog = alertDB.create();
        dialog.show();
    }

    // When losing:
    // put score in shared preferences
    // fix appearance of displayed secret word
    // add strings to Intent, for the ResultActivity
    // start ResultActivity
    public void lose() {
        editor = sp.edit();
        editor.putInt("score", score);
        editor.apply();
        Intent intent = new Intent(GameActivity.this, ResultActivity.class);
        secretWord = fixDisplayWord(secretWord);
        String message = displayString + "\n\nThe secret word was:\n" + secretWord;
        intent.putExtra("RESULT", "You lost!");
        intent.putExtra("SECRET_WORD", message);
        intent.putExtra("WINNER", false);
        startActivity(intent);
    }

    // When winning:
    // make a String to show in ResultActivity, depending on amount of wrong guesses
    // add 10 to score
    // add score to shared preferences
    // add strings to Intent
    // start ResultActivity
    public void win() {
        Intent intent = new Intent(GameActivity.this, ResultActivity.class);
        switch (wrongGuesses) {
            case 0:
                intent.putExtra("RESULT", "You won!\nWith no wrong guesses at all!");
                break;
            case 1:
                intent.putExtra("RESULT", "You won!\nWith only 1 wrong guess!");
                break;
            default:
                intent.putExtra("RESULT", "You won!\nWith " + wrongGuesses + " wrong guesses.");
                break;
        }
        score += 10;
        editor = sp.edit();
        editor.putInt("score", score);
        editor.apply();
        intent.putExtra("SECRET_WORD", displayString);
        intent.putExtra("WINNER", true);
        startActivity(intent);
    }

    // Show the right picture depending on amount of wrong guesses
    public void switchImage(View view) {
        switch (wrongGuesses) {
            case 1:
                hangmanImage.setImageResource(R.drawable.pic1);
                break;
            case 2:
                hangmanImage.setImageResource(R.drawable.pic2);
                break;
            case 3:
                hangmanImage.setImageResource(R.drawable.pic3);
                break;
            case 4:
                hangmanImage.setImageResource(R.drawable.pic4);
                break;
            case 5:
                hangmanImage.setImageResource(R.drawable.pic5);
                break;
            case 6:
                hangmanImage.setImageResource(R.drawable.pic6);
                break;
            case 7:
                hangmanImage.setImageResource(R.drawable.pic7);
                break;
        }
    }

    // Method for getting a random word from CSV file with words
    // Swedish version!
    private String getRandomWord(){
        String[] tempArr = new String[0];
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.words);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = bufferedReader.readLine();

            while (line != null) {
                tempArr = line.split(",");
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Random random = new Random();
        return tempArr[(random.nextInt(tempArr.length))];
    }

    // "Fix" the displayed secret word
    // i.e. add a space between each char
    public String fixDisplayWord(String str) {
        StringBuilder newString = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            newString.append(str.charAt(i));
            if(i != str.length()-1) {
                newString.append(" ");
            }
        }
        return newString.toString();
    }

    // Show notification
    void showNotification(String title, String message) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("CHANNEL_1",
                    "CHANNEL_HANGMAN",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Hangman game");
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "CHANNEL_1")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true);
        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        assert mNotificationManager != null;
        mNotificationManager.notify(0, mBuilder.build());
    }
}