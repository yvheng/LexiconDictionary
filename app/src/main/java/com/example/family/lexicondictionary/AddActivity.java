package com.example.family.lexicondictionary;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.family.lexicondictionary.Model.Word;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.family.lexicondictionary.DisplayActivity.addStatus;
import static com.example.family.lexicondictionary.DisplayActivity.editStatus;
import static com.example.family.lexicondictionary.DisplayActivity.originalWordKey;
import static com.example.family.lexicondictionary.DisplayActivity.translatedFromKey;
import static com.example.family.lexicondictionary.DisplayActivity.translatedToKey;
import static com.example.family.lexicondictionary.DisplayActivity.translatedWordKey;
import static com.example.family.lexicondictionary.LoginActivity.MY_PREFS_NAME;
import static com.example.family.lexicondictionary.LoginActivity.userIDKey;

public class AddActivity extends AppCompatActivity {
    String[] languages = {"English", "Malay", "Mandarin"}; //for develop purpose

    TextView textViewTitle, textViewOriginalWord,textViewTranslateFrom, textViewTranslateTo;
    EditText editTextTranslatedWord;
    View addingProgress, addingForm;
    Word word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        String originalWord = extras.getString(originalWordKey);
        String translateFrom = extras.getString(translatedFromKey);
        String translateTo = extras.getString(translatedToKey);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        int userID = prefs.getInt(userIDKey, 0);

        word = new Word(0, originalWord, "", translateFrom,
                translateTo,"", null,userID, userID);

        textViewTranslateFrom = findViewById(R.id.textViewTranslateFrom);
        textViewTranslateTo = findViewById(R.id.textViewTranslateTo);
        textViewOriginalWord = findViewById(R.id.textViewOriginal);
        editTextTranslatedWord = findViewById(R.id.editTextTranslated);
        addingProgress = findViewById(R.id.addingProgress);
        addingForm = findViewById(R.id.addingForm);

        showProgress(false);

        FloatingActionButton fab = findViewById(R.id.saveFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check internet connection again
                if (isConnected()) {
                    //check if translated word is empty
                    if(!(editTextTranslatedWord.getText().toString().equals("")||
                            editTextTranslatedWord.getText().toString().equals(" "))) {
                        showProgress(true);
                        String url = "http://i2hub.tarc.edu.my:8117/writeWord.php?";
                        word.setTranslatedContent(editTextTranslatedWord.getText().toString());

                        makeServiceCallAddWord(getApplicationContext(), url, word);
                    }else{
                        Toast.makeText(getApplicationContext(), "Translated Word is empty.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "No Internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textViewTitle = findViewById(R.id.textViewTitle);

        String status = extras.getString("STATUS");
        try {
            if (status.equals(addStatus)) {
                textViewTitle.setText(R.string.title_addNewEntry);
            } else if (status.equals(editStatus)) {
                textViewTitle.setText(R.string.title_editEntry);
            }
        }catch(NullPointerException e){

        }catch(Exception e){

        }
        setFields(originalWord, translateFrom, translateTo);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        addingForm.setVisibility(show ? View.GONE : View.VISIBLE);
        addingForm.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                addingForm.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        addingProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        addingProgress.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                addingProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    public void makeServiceCallAddWord(Context context, String url, final Word word) {
        //mPostCommentResponse.requestStarted();
        RequestQueue queue = Volley.newRequestQueue(context);

        //Send data
        try {
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            showProgress(false);
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showProgress(false);
                            Toast.makeText(getApplicationContext(), "Error :" + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("originalContent", word.getOriginalContent());
                    params.put("translatedContent", word.getTranslatedContent());
                    params.put("originalLanguage", word.getOriginalLanguage());
                    params.put("translatedLanguage", word.getTranslatedLanguage());
                    params.put("userID", String.valueOf(word.getUserID()));

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }
            };
            queue.add(postRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }

    private void setFields(String originalWord , String translateFrom, String translateTo){
        try{
            textViewOriginalWord.setText(originalWord);
        }catch(Exception e){
            textViewOriginalWord.setText("");
        }

        int i, u;
        try {
            textViewTranslateFrom.setText(translateFrom);
            textViewTranslateTo.setText(translateTo);
        }catch(Exception e){
            textViewTranslateFrom.setText("");
            textViewTranslateTo.setText("");
        }
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}
