package ecutb.loffe.hangman.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

import java.util.ArrayList;

import ecutb.loffe.hangman.R;
import ecutb.loffe.hangman.entities.User;

public class APIActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String REQ_URL = "https://reqres.in/api/";

    private ArrayList<User> users;
    private ArrayList<String> responseArray;
    private Button btnGet, btnPut, btnPost, btnDelete;
    private TextView textViewApi;
    private ListView listViewApi;
    private RequestQueue reqQueue;
    private ImageView avatar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_p_i);

        btnGet = findViewById(R.id.getBtn);
        btnPut = findViewById(R.id.putBtn);
        btnPost = findViewById(R.id.postBtn);
        btnDelete = findViewById(R.id.deleteBtn);
        textViewApi = findViewById(R.id.apiTextView);
        listViewApi = findViewById(R.id.apiListView);
        avatar = findViewById(R.id.imageView);
        reqQueue = Volley.newRequestQueue(this);
        responseArray = new ArrayList<>();
        users = new ArrayList<>();
    }

    // methods for the buttons
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loginBtn:
                login();
                break;
            case R.id.getBtn:
                getRequest();
                break;
            case R.id.postBtn:
                postRequest();
                break;
            case R.id.putBtn:
                putRequest();
                break;
            case R.id.deleteBtn:
                deleteRequest();
                break;
        }
    }

    // return to login activity
    private void login() {
        finish();
    }

    // DELETE
    private void deleteRequest() {
        btnDelete.setEnabled(false);
        avatar.setVisibility(View.GONE);
        final JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.DELETE,
                "https://jsonplaceholder.typicode.com/posts/1",
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    responseArray.clear();
                    responseArray.add("SERVER RESPONSE: " + response.toString() + "\n...so DELETE was successful.");
                    fixListView(responseArray);
                }catch (Exception e){
                    e.printStackTrace();
                }
                btnDelete.setEnabled(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                String error = "DELETE FAILED";
                textViewApi.setText(error);
                btnDelete.setEnabled(true);
                e.printStackTrace();
            }
        });
        reqQueue.add(request);
        String message = request.getUrl();
        textViewApi.setText(message);
    }

    // PUT
    private void putRequest() {
        btnPut.setEnabled(false);
        avatar.setVisibility(View.GONE);
        try {
            JSONObject putData = new JSONObject();
            putData.put("id", "666");
            putData.put("email", "info@reqres.in");
            putData.put("first_name", "Loffe");
            putData.put("last_name", "Knutsson");

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.PUT,
                    REQ_URL + "users/1",
                    putData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    btnPut.setEnabled(true);
                    responseArray.clear();
                    responseArray.add(String.valueOf(response));
                    fixListView(responseArray);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    btnPut.setEnabled(true);
                    String error = "PUT FAILED";
                    textViewApi.setText(error);
                    e.printStackTrace();
                }
            });
            reqQueue.add(request);
            String message = "URL: " + request.getUrl();
            textViewApi.setText(message);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // POST
    private void postRequest() {
        btnPost.setEnabled(false);
        avatar.setVisibility(View.GONE);
        try {
            JSONObject postData = new JSONObject();
            postData.put("id", "666");
            postData.put("email", "notme@somewhere.com");
            postData.put("first_name", "Mr");
            postData.put("last_name", "Spamalot");

            final JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    REQ_URL + "users",
                    postData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    responseArray.clear();
                    responseArray.add(String.valueOf(response));
                    fixListView(responseArray);
                    btnPost.setEnabled(true);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String fail = "Post failed";
                    textViewApi.setText(fail);
                    error.printStackTrace();
                    btnPost.setEnabled(true);
                }
            });
            String post = "Now posted to " + request.getUrl();
            textViewApi.setText(post);
            reqQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // GET
    private void getRequest() {
        btnGet.setEnabled(false);
        responseArray.clear();
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, REQ_URL + "users?page=2", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject userObject = jsonArray.getJSONObject(i);
                        User user = new User(userObject.getInt("id"),
                                userObject.getString("email"),
                                userObject.getString("first_name"),
                                userObject.getString("last_name"),
                                userObject.getString("avatar"));
                        users.add(user);
                        responseArray.add(user.getFirst_name() + " " + user.getLast_name() + "\n" + user.getEmail());
                        avatar.setVisibility(View.VISIBLE);
                        Picasso.get().load(user.getAvatar()).fit().into(avatar);
                        fixListView(responseArray);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    btnGet.setEnabled(true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        btnGet.setEnabled(true);
        String viewString = "URL: " + request.getUrl();
        textViewApi.setText(viewString);
        reqQueue.add(request);
    }

    // populate the listview
    private void fixListView(ArrayList<String> list) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.api_textview, R.id.apiTxtViewLayout, list);
        listViewApi.setAdapter(arrayAdapter);
    }
}