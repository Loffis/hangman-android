package ecutb.loffe.hangman.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import ecutb.loffe.hangman.R;
import ecutb.loffe.hangman.database.DBHelper;
import ecutb.loffe.hangman.entities.Score;

public class HighscoreActivity extends AppCompatActivity implements View.OnClickListener {

    DBHelper dbHelper;
    ListView highscoreTable;
    ArrayAdapter<String> arrayAdapter;
    List<Score> scores = new ArrayList<>();
    List<String> top5 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        highscoreTable = findViewById(R.id.highscoreListView);

        getAllScores();

        // Very tedious process to fix the appearance of the highscore table.
        // Probably would've been easier in the xml layout
        final Typeface mTypeface = Typeface.createFromAsset(getAssets(), "fonts/jetbrains.ttf");
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.score_textview, getTop5Scores()) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                TextView item = (TextView) super.getView(position,convertView,parent);
                item.setTypeface(mTypeface);
                return item;
        }};
        highscoreTable.setAdapter(arrayAdapter);
    }

    // Button methods
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnPlay:
                startActivity(new Intent(HighscoreActivity.this, GameActivity.class));
                break;
            case R.id.btnQuit:
                finishAffinity();
                break;
        }
    }

    // Sort the score list, descending
    // Get top 5, or, of list is smaller than 5, the whole list
    // Inform if list is 0
    private List<String> getTop5Scores() {
        Collections.sort(scores);
        Collections.reverse(scores);

        top5.add(String.format(Locale.getDefault(), "%-10s",
                "SCORE") + "PLAYER");
        int i, listSize;
        if (scores.size() > 5) {
            listSize = 5;
        } else {
            listSize = scores.size();
        }

        if (listSize == 0) {
            top5.add("NO SCORES AVAILABLE");
        } else {
            for (i = 0; i < listSize; i++) {
                top5.add(String.format(Locale.getDefault(), "%-10d%s",scores.get(i).getScore(), scores.get(i).getUsername()));
            }
        }
        return top5;
    }

    // Get all scores from database, assign them to 'scores'
    public List<Score> getAllScores() {
        dbHelper = new DBHelper(this);
        scores = dbHelper.getHighscores();
        return scores;
    }
}