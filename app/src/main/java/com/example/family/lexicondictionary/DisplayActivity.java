package com.example.family.lexicondictionary;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SeekBar;
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
import com.example.family.lexicondictionary.Adapter.RecyclerViewAdapter;
import com.example.family.lexicondictionary.Model.Attachment;
import com.example.family.lexicondictionary.Model.Word;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.family.lexicondictionary.LoginActivity.MY_PREFS_NAME;
import static com.example.family.lexicondictionary.MainActivity.userIDKey;

public class DisplayActivity extends AppCompatActivity {
    String[] languages = {"English", "Malay", "Mandarin"}; //for develop purpose only
    String[] moodtags = {
            "ecstasy",      "vigilance",    "rage",         "admiration",
            "joy",          "anticipation", "anger",        "trust",
            "serenity",     "interest",     "annoyance",    "acceptance",
            "pensiveness",  "distraction",  "apprehension", "boredom",
            "sadness",      "surprise",     "fear",         "disgust",
            "grief",        "amazement",    "terror",       "loathing"};
    List<String> historyOriginalWord = new ArrayList<>();
    List<String> historyTranslatedWord = new ArrayList<>();
    List<String> emotion = new ArrayList<>();
    List<Bitmap> emoticon = new ArrayList<>();
    int seekBarCurrent = 10000;
    boolean noResult=false;
    double sentimentStrength=0;
    String zeroValue = "0.0000";
    final static String addStatus= "ADD_STATUS";
    final static String editStatus= "EDIT_STATUS";
    final static String originalWordKey= "ORIGINAL_WORD";
    final static String translatedWordKey= "TRANSLATED_WORD";
    final static String translatedFromKey= "TRANSLATED_FROM";
    final static String translatedToKey= "TRANSLATED_TO";
    final static String prefName= "PRIVATE_PREF";
    Word word = new Word();
    String wordUrl;
    Attachment attachment;
    SharedPreferences pref;

    Spinner translateFromList, translateToList;
    RecyclerView mRecyclerView;
    Button validateButton;
    EditText editTextOriginalWord;
    TextView textViewTranslatedWord, textViewSentiment, textViewPleasantness, textViewAttention, textViewSensitivity, textViewAptitude, textViewNegative, textViewPositive;
    TextView textViewConcept1, textViewConcept2,textViewConcept3,textViewConcept4,textViewConcept5;
    SeekBar seekBarSentiment;
    ImageButton imageButtonPlayPronunciation;
    ImageView imageViewPhoto;
    RatingBar ratingBarAccuracy;
    View displayProgress;
    View displayForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        //Linking
        translateFromList= findViewById(R.id.translateFrom);
        translateToList= findViewById(R.id.translateTo);
        validateButton= findViewById(R.id.validateButton);
        editTextOriginalWord= findViewById(R.id.originalWord);
        textViewTranslatedWord= findViewById(R.id.translatedWord);
        mRecyclerView =  findViewById(R.id.recycler_view);
        seekBarSentiment = findViewById(R.id.seekBarSentiment);
        textViewSentiment = findViewById(R.id.textViewSentiment);
        imageButtonPlayPronunciation = findViewById(R.id.playPronunciation);
        imageViewPhoto = findViewById(R.id.imageViewPhoto);
        ratingBarAccuracy = findViewById(R.id.ratingBar);
        displayProgress = findViewById(R.id.displayProgress);
        displayForm = findViewById(R.id.displayForm);
        textViewPleasantness = findViewById(R.id.textViewPleasantness);
        textViewAttention = findViewById(R.id.textViewAttention);
        textViewSensitivity = findViewById(R.id.textViewSensitivity);
        textViewAptitude = findViewById(R.id.textViewAptitude);
        textViewNegative = findViewById(R.id.textViewNegative);
        textViewPositive = findViewById(R.id.textViewPositive);

        textViewConcept1 = findViewById(R.id.textViewConcept1);
        textViewConcept2 = findViewById(R.id.textViewConcept2);
        textViewConcept3 = findViewById(R.id.textViewConcept3);
        textViewConcept4 = findViewById(R.id.textViewConcept4);
        textViewConcept5 = findViewById(R.id.textViewConcept5);

        seekBarSentiment.setMax(R.integer.seekBarMax);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seekBarSentiment.setMin(R.integer.seekBarMin);
        }*/
        seekBarSentiment.setProgress(R.integer.seekBarMax/2);
        seekBarCurrent = R.integer.seekBarMax/2;
        sentimentStrength = ((double)seekBarCurrent/1065680896.0)-1;
        //sentimentStrength = seekBarCurrent;
        textViewSentiment.setText(String.format(Locale.getDefault(),"%.4f",sentimentStrength));

        textViewPleasantness.setText(zeroValue);
        textViewAttention.setText(zeroValue);
        textViewSensitivity.setText(zeroValue);
        textViewAptitude.setText(zeroValue);

        //Disable input from user
        seekBarSentiment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        //Array adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, languages);
        //Setting spinner adapter and onTouchListener
        translateFromList.setAdapter(spinnerAdapter);
        translateToList.setAdapter(spinnerAdapter);
        translateToList.setSelection(1);

        translateFromList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                noResult=false;
                translate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                translateFromList.setSelection(0);
            }
        });
        translateToList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                noResult=false;
                translate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                translateFromList.setSelection(1);
            }
        });

        //LayoutManager for recyclerView
        LinearLayoutManager layoutManager= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        //Divider for recyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                layoutManager.getOrientation());
        //Setting layoutManager and divider to recyclerView
        mRecyclerView.setLayoutManager(layoutManager);
        //mRecyclerView.addItemDecoration(dividerItemDecoration);
        loadEmotion("","");

        editTextOriginalWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textViewTranslatedWord.setText("");
                imageViewPhoto.setImageResource(R.mipmap.no_photo);
                textViewSentiment.setText(zeroValue);
                textViewPleasantness.setText(zeroValue);
                textViewAttention.setText(zeroValue);
                textViewSensitivity.setText(zeroValue);
                textViewAptitude.setText(zeroValue);
                seekBarSentiment.setProgress(R.integer.seekBarMax/2);
                textViewPositive.setTypeface(null, Typeface.NORMAL);
                textViewNegative.setTypeface(null, Typeface.NORMAL);
                noResult=false;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            private Timer timer=new Timer();
            private final long DELAY = 2000; // milliseconds
            @Override
            public void afterTextChanged(Editable s) {
                timer.cancel();
                timer = new Timer();
                /*try {
                    timer.schedule(
                            new TimerTask() {
                                @Override
                                public void run() {
                                    //Check if the required field is empty
                                    if (!(editTextOriginalWord.getText() == null ||
                                            editTextOriginalWord.getText().toString().equals("") ||
                                            editTextOriginalWord.getText().toString().equals(" "))) {
                                        translate();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Original word is empty.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },
                            DELAY
                    );
                }catch(Exception e){
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }*/
                try {
                    timer.schedule(
                            new TimerTask() {
                                private Handler updateUI = new Handler(){
                                    @Override
                                    public void dispatchMessage(Message msg) {
                                        super.dispatchMessage(msg);

                                        if (!(editTextOriginalWord.getText() == null ||
                                                editTextOriginalWord.getText().toString().equals("") ||
                                                editTextOriginalWord.getText().toString().equals(" "))) {
                                            translate();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Original word is empty.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                };
                                @Override
                                public void run() {
                                    try{
                                        updateUI.sendEmptyMessage(0);
                                    }catch(Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            },
                            DELAY
                    );
                }catch(Exception e){
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageButtonPlayPronunciation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToDo: Play audio file

            }
        });

        if(!isConnected()){
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
        }

        for(int i=1; i<=15;i++){
            historyOriginalWord.add("Original"+String.valueOf(i));
            historyTranslatedWord.add("Translated"+String.valueOf(i));
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        /*displayForm.setVisibility(show ? View.GONE : View.VISIBLE);
        displayForm.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                displayForm.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        displayProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        displayProgress.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                displayProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });*/
    }

    private void loadEmotion(String concept1, String concept2){
        emotion.clear();
        emoticon.clear();
        boolean concept1Found=false, concept2Found=false;
        //ToDo: Change algo to make more efficient using similar code below:
        //emoticon.add(BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(moodtags[i], null, getPackageName())));
        if(!(concept1.equals("")||concept2.equals(""))) {
            for (int i = 0; i < moodtags.length; i++) {
                if (concept1.equals(moodtags[i])&& !concept1Found) {
                    emotion.add(moodtags[i]);
                    concept1Found=true;
                    if(i==0)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.ecstasy));
                    else if(i==1)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.vigilance));
                    else if(i==2)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.rage));
                    else if(i==3)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.admiration));
                    else if(i==4)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.joy));
                    else if(i==5)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.anticipation));
                    else if(i==6)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.anger));
                    else if(i==7)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.trust));
                    else if(i==8)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.serenity));
                    else if(i==9)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.interest));
                    else if(i==10)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.annoyance));
                    else if(i==11)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.acceptance));
                    else if(i==12)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.pensiveness));
                    else if(i==13)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.distraction));
                    else if(i==14)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.apprehension));
                    else if(i==15)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.boredom));
                    else if(i==16)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.sadness));
                    else if(i==17)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.surprise));
                    else if(i==18)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.fear));
                    else if(i==19)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.disgust));
                    else if(i==20)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.grief));
                    else if(i==21)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.amazement));
                    else if(i==22)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.terror));
                    else if(i==23)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.loathing));
                }//end inner if
                if (concept2.equals(moodtags[i])&&!concept2Found) {
                    emotion.add(moodtags[i]);
                    concept2Found=true;
                    if(i==0)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.ecstasy));
                    else if(i==1)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.vigilance));
                    else if(i==2)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.rage));
                    else if(i==3)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.admiration));
                    else if(i==4)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.joy));
                    else if(i==5)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.anticipation));
                    else if(i==6)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.anger));
                    else if(i==7)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.trust));
                    else if(i==8)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.serenity));
                    else if(i==9)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.interest));
                    else if(i==10)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.annoyance));
                    else if(i==11)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.acceptance));
                    else if(i==12)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.pensiveness));
                    else if(i==13)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.distraction));
                    else if(i==14)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.apprehension));
                    else if(i==15)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.boredom));
                    else if(i==16)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.sadness));
                    else if(i==17)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.surprise));
                    else if(i==18)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.fear));
                    else if(i==19)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.disgust));
                    else if(i==20)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.grief));
                    else if(i==21)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.amazement));
                    else if(i==22)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.terror));
                    else if(i==23)
                        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.loathing));
                }//end inner if
            }//end forLoop
        }//end if
        mRecyclerView.setAdapter(new RecyclerViewAdapter(getApplicationContext(), emotion, emoticon));
    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }

    public void requestWord(){
        if(!isConnected()) {
            showProgress(false);
            Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_SHORT).show();
        }
        else{
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            wordUrl = getString(R.string.url_selectSpecWord);
            wordUrl += "originalContent="+editTextOriginalWord.getText().toString();
            try{
                word.setOriginalContent(editTextOriginalWord.getText().toString());}
            catch(NullPointerException e){

            }
            wordUrl += "&originalLanguage="+translateFromList.getSelectedItem().toString();
            wordUrl += "&translatedLanguage="+translateToList.getSelectedItem().toString();

            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(wordUrl,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                JSONObject recordResponse = (JSONObject) response.get(0);
                                int id = Integer.parseInt(recordResponse.getString("id"));
                                String originalContent = recordResponse.getString("originalContent");
                                String translatedContent = recordResponse.getString("translatedContent");
                                String originalLanguage = recordResponse.getString("originalLanguage");
                                String translatedLanguage = recordResponse.getString("translatedLanguage");
                                String status = recordResponse.getString("status");
                                Date dateTimeAdded = Date.valueOf(recordResponse.getString("dateTimeAdded"));
                                int userID = Integer.parseInt(recordResponse.getString("userID"));
                                int lastEditUserID = Integer.parseInt(recordResponse.getString("lastEditUserID"));

                                word = new Word(id, originalContent, translatedContent, originalLanguage,
                                        translatedLanguage,status, dateTimeAdded, userID, lastEditUserID);

                                //set retrieved word to the textView
                                textViewTranslatedWord.setText(word.getTranslatedContent());

                                //ToDo: add sharedPref
                                /*pref = getApplicationContext().getSharedPreferences(prefName, 0);
                                SharedPreferences.Editor editor = pref.edit();

                                int i=1;
                                try {
                                    i=1;
                                    while (!(pref.getString("historyID"+i, "").equals(null)||pref.getString("historyID"+i, "").equals(""))) {
                                        //not empty
                                        historyWordID.set(i-1, pref.getString("historyID" + i, ""));
                                        i++;
                                    }
                                }catch(NullPointerException e){
                                    Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                try {
                                    if (historyWordID.size() >= 15)
                                        Toast.makeText(getApplicationContext(), "Maximum translation history count.", Toast.LENGTH_SHORT).show();
                                    else {
                                        editor.putString("historyID"+historyWordID.size()+1, "historyID" + historyWordID.size()+1);
                                        editor.putString(historyOriginalWord.get(historyWordID.size()), word.getOriginalContent());
                                        editor.putString(historyTranslatedWord.get(historyWordID.size()), word.getTranslatedContent());
                                        editor.apply();
                                    }
                                }catch(ArrayIndexOutOfBoundsException e){
                                    Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }*/
                                writeHistory(getApplicationContext(), getString(R.string.url_writeHistory));
                                requestImage();
                            } catch (NullPointerException e) {
                                showProgress(false);
                                Toast.makeText(getApplicationContext(), "Original text is empty.", Toast.LENGTH_SHORT).show();
                            }catch(Exception e){
                                showProgress(false);
                                if(e.getMessage().equals("Index 0 out of range [0..0)")) {
                                    if(!(editTextOriginalWord.getText()==null||
                                            editTextOriginalWord.getText().toString().equals("")||
                                            editTextOriginalWord.getText().toString().equals(" "))){
                                        noResult=true;
                                        requestDetail();
                                    }
                                }
                                else
                                    Toast.makeText(getApplicationContext(), "Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            showProgress(false);
                            Toast.makeText(getApplicationContext(), "Volley Error:" + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            queue.add(jsonObjectRequest);
        }
    }

    private void writeHistory(Context context, String url) {
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

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Error :" + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("original", word.getOriginalContent());
                    params.put("translation", word.getTranslatedContent());
                    pref = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                    params.put("userID", String.valueOf(pref.getInt(userIDKey, 0)));
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

    public void requestImage(){
        if(!isConnected()) {
            showProgress(false);
            Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_SHORT).show();
        }
        else{
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            String url = getString(R.string.url_selectSpecImage);
            url += "wordID="+word.getId();

            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(url,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                JSONObject recordResponse = (JSONObject) response.get(0);
                                int id = recordResponse.getInt("id");
                                String photo = recordResponse.getString("photo");
                                String pronunciation = recordResponse.getString("pronunciation");

                                attachment = new Attachment(id, photo, pronunciation, word.getId());

                                //decode photo and pronunciation
                                Bitmap bitmap = getImageString(photo);
                                getAudioString(pronunciation);

                                //set retrieved image to the imageView
                                imageViewPhoto.setImageBitmap(bitmap);

                                //set audio to play
                                //File file = new File(Environment.getExternalStorageDirectory() + "/pronunciation.wav");
                                requestDetail();
                            }catch(Exception e){
                                showProgress(false);
                                if(e.getMessage().equals("Index 0 out of range [0..0)")) {
                                    Toast.makeText(getApplicationContext(), "This translation has no image.", Toast.LENGTH_SHORT).show();
                                    requestDetail();
                                }
                                else
                                    Toast.makeText(getApplicationContext(), "Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            showProgress(false);
                            Toast.makeText(getApplicationContext(), "Volley Error:" + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            queue.add(jsonObjectRequest);
        }
    }

    public void requestDetail(){
        if(!isConnected()) {
            showProgress(false);
            Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_SHORT).show();
        }
        else{
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            //String url = getString(R.string.url_selectSpecDetail);
            String url = getString(R.string.url_selectSpecTDetail);
            url += "text="+editTextOriginalWord.getText().toString();

            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(url,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                JSONObject recordResponse = (JSONObject) response.get(0);
                                int id = recordResponse.getInt("id");

                                requestDetail2(id);
                            }catch(Exception e){
                                showProgress(false);
                                if(e.getMessage().equals("Index 0 out of range [0..0)")){
                                    if(noResult)
                                        noResult();
                                    else
                                        Toast.makeText(getApplicationContext(), "This translation has no other details.", Toast.LENGTH_SHORT).show();
                                }
                                else
                                    Toast.makeText(getApplicationContext(), "Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            showProgress(false);
                            Toast.makeText(getApplicationContext(), "Volley Error:" + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            queue.add(jsonObjectRequest);
        }
    }

    public void requestDetail2(int id){
        if(!isConnected()) {
            showProgress(false);
            Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_SHORT).show();
        }
        else{
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            //String url = getString(R.string.url_selectSpecDetail);
            String url = getString(R.string.url_selectSpecTDetail2);
            url += "textID="+id;

            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(url,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                JSONObject recordResponse = (JSONObject) response.get(0);
                                word.setStatus("None");

                                double pleasantness = recordResponse.getDouble("pleasantness");
                                double attention = recordResponse.getDouble("attention");
                                double sensitivity = recordResponse.getDouble("sensitivity");
                                double aptitude = recordResponse.getDouble("aptitude");
                                String polarity = recordResponse.getString("value");
                                double intensity = recordResponse.getDouble("intensity");

                                String sconcept1 = recordResponse.getString("sconcept1");
                                String sconcept2 = recordResponse.getString("sconcept2");
                                String concept3 = recordResponse.getString("concept3");
                                String concept4 = recordResponse.getString("concept4");
                                String concept5 = recordResponse.getString("concept5");

                                String mconcept1 = recordResponse.getString("mconcept1");
                                String mconcept2 = recordResponse.getString("mconcept2");

                                //setting into their respective fields
                                textViewPleasantness.setText(String.format(Locale.getDefault(),"%.4f",pleasantness));
                                textViewAttention.setText(String.format(Locale.getDefault(),"%.4f",attention));
                                textViewSensitivity.setText(String.format(Locale.getDefault(),"%.4f",sensitivity));
                                textViewAptitude.setText(String.format(Locale.getDefault(),"%.4f",aptitude));

                                textViewConcept1.setText(sconcept1);
                                textViewConcept2.setText(sconcept2);
                                textViewConcept3.setText(concept3);
                                textViewConcept4.setText(concept4);
                                textViewConcept5.setText(concept5);

                                loadEmotion(mconcept1, mconcept2);

                                //seekBar
                                if(polarity.equals("positive")) {
                                    textViewNegative.setTypeface(null, Typeface.NORMAL);
                                    textViewPositive.setTypeface(null, Typeface.BOLD);
                                }
                                else if (polarity.equals("negative")){
                                    textViewNegative.setTypeface(null, Typeface.BOLD);
                                    textViewPositive.setTypeface(null, Typeface.NORMAL);
                                }
                                seekBarCurrent = (int)((intensity+1.0000)*(R.integer.seekBarMax/2));
                                seekBarSentiment.setProgress(seekBarCurrent);
                                textViewSentiment.setText(String.format(Locale.getDefault(),"%.4f",intensity));

                                //emotion
                                //ToDo: setting emotion(moodTags)

                                //ToDo: getting value from database
                                //ratingBar
                                ratingBarAccuracy.setRating((float)2.5);

                                if(noResult) {
                                    noResult();
                                    Toast.makeText(getApplicationContext(), "This word has no translation record.", Toast.LENGTH_LONG).show();
                                }

                                showProgress(false);
                            }catch(Exception e){
                                showProgress(false);
                                if(e.getMessage().equals("Index 0 out of range [0..0)"))
                                    Toast.makeText(getApplicationContext(), "This translation has no details.", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(getApplicationContext(), "Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            showProgress(false);
                            Toast.makeText(getApplicationContext(), "Volley Error:" + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            queue.add(jsonObjectRequest);
        }
    }

    public void exchange(View v){
        int temp=0;
        temp = translateFromList.getSelectedItemPosition();

        translateFromList.setSelection(translateToList.getSelectedItemPosition());
        translateToList.setSelection(temp);
    }

    public void validate(View v){
        //Check if the required field is empty
        if(!(editTextOriginalWord.getText()==null||
                editTextOriginalWord.getText().toString().equals("")||
                editTextOriginalWord.getText().toString().equals(" "))){
            //Check if this record is provided by user and not from original database
            if(!word.getStatus().equals("default")) {
                Intent intent = new Intent(this, AddActivity.class);
                //putExtra( KEY, VALUE);
                intent.putExtra("STATUS", editStatus);
                intent.putExtra(originalWordKey, editTextOriginalWord.getText().toString());
                intent.putExtra(translatedWordKey, textViewTranslatedWord.getText().toString());
                intent.putExtra(translatedFromKey, translateFromList.getSelectedItem().toString());
                intent.putExtra(translatedToKey, translateToList.getSelectedItem().toString());
                //ToDo: put emotion, sentiment
                startActivityForResult(intent, 2); //edit = 2
            }else{
                //default word added by loading word into db (admin)
                Toast.makeText(getApplicationContext(), "Unable to edit default word.", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Original text is empty.", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearField(){
        editTextOriginalWord.setText("");
        textViewTranslatedWord.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==2){
            clearField();
            if(resultCode == Activity.RESULT_OK){
                Toast.makeText(getApplicationContext(), "Word validated..", Toast.LENGTH_SHORT).show();
            }
            if(resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(getApplicationContext(), "Validation cancelled.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void translate(){
        //check internet & database connection
        if(!isConnected()){
            Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_SHORT).show();
        }else{
            showProgress(true);
            requestWord();
        }
    }

    private void noResult(){
        Toast.makeText(getApplicationContext(),"No translation found.",Toast.LENGTH_SHORT).show();
        String originalWord = editTextOriginalWord.getText().toString();
        String translateFrom = translateFromList.getSelectedItem().toString();
        String translateTo = translateToList.getSelectedItem().toString();

        Intent intent = new Intent(this, AddPopUpWindow.class);
        intent.putExtra("STATUS", addStatus);
        intent.putExtra(originalWordKey, originalWord);
        intent.putExtra(translatedFromKey, translateFrom);
        intent.putExtra(translatedToKey, translateTo);
        startActivity(intent);
    }

    public static Bitmap getImageString(String input){
        //decode image to base64
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
    public static void getAudioString(String input){
        byte[] decoded = Base64.decode(input, Base64.DEFAULT);
        try
        {
            File file = new File(Environment.getExternalStorageDirectory() + "/pronunciation.wav");
            FileOutputStream os = new FileOutputStream(file, true);
            os.write(decoded);
            os.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
