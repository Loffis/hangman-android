package ecutb.loffe.hangman.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import ecutb.loffe.hangman.R;
import ecutb.loffe.hangman.database.DBHelper;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView hangmanImageView;
    TextView resultTextView, secretWordView, winnerText;
    Button buttonRetry, buttonLogin, buttonQuit, buttonHighscore;
    SharedPreferences sp;
    int score;
    DBHelper dbHelper;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        hangmanImageView = findViewById(R.id.hangmanResultImageView);
        resultTextView = findViewById(R.id.resultTextView);
        secretWordView = findViewById(R.id.secretWordRevealed);
        buttonRetry = findViewById(R.id.buttonRetry);
        buttonQuit = findViewById(R.id.buttonQuit);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonHighscore = findViewById(R.id.buttonHighscore);
        winnerText = findViewById(R.id.winnerTextView);

        // Get name and score from shared preferences
        sp = getSharedPreferences("session", MODE_PRIVATE);
        String player = sp.getString("username", "no player");
        score = sp.getInt("score", 0);

        // Add score to database
        dbHelper = new DBHelper(this);
        dbHelper.addScore(player, score);

        // For Pexel image request
        requestQueue = Volley.newRequestQueue(this);

        // Title
        Objects.requireNonNull(getSupportActionBar()).setTitle("Hangman - " + player);

        // Get secretWord and result from Intent
        String secretWord = getIntent().getStringExtra("SECRET_WORD");
        String resultString = getIntent().getStringExtra("RESULT") + "\nScore: " + score;

        // If we have a winner, get a random pic
        // Else, let the hangman picture remain, but don't show the winner text
        if (getIntent().getBooleanExtra("WINNER", true)) {
            setWinnerPic();
        } else {
            winnerText.setText("");
        }

        // Show result and secretWord
        resultTextView.setText(resultString);
        secretWordView.setText(secretWord);
    }

    // Button methods
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonRetry:
                Intent intent = new Intent(ResultActivity.this, GameActivity.class);
                startActivity(intent);
                break;
            case R.id.buttonLogin:
                startActivity(new Intent(ResultActivity.this, LoginActivity.class));
                break;
            case R.id.buttonHighscore:
                startActivity(new Intent(ResultActivity.this, HighscoreActivity.class));
                break;
            case R.id.buttonQuit:
                finishAffinity();
                break;
        }
    }

    // Get an image from Pexels.com
    // First, get a list of 15 JSON image objects
    // then in the src object find the image size 'tiny'
    // add each one of those to an array
    // Get a random image of the 15
    // Picasso loads the url
    // Btw, the query search word is 'GOLD'!
    private void setWinnerPic() {
        final String[] tempArr = new String[16];
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://api.pexels.com/v1/search?query=gold&per_page=15", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("photos");
                    JSONObject photo;
                    JSONObject src;
                    String url;
                    for(int i = 0; i < jsonArray.length(); i++) { // 15 photo objects in here
                        photo = (JSONObject) jsonArray.get(i);
                        src = photo.getJSONObject("src");
                        url = src.getString("tiny");
                        tempArr[i] = url;
                    }
                    Random random = new Random();
                    url = tempArr[(random.nextInt(tempArr.length))];
                    Picasso.get().load(url).fit().into(hangmanImageView);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "563492ad6f91700001000001e74ce58377b24ed0898be9003241c7ff");
                return headers;
            }
        };
        requestQueue.add(request);
    }
}