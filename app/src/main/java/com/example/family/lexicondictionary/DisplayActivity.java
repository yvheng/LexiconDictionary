package com.example.family.lexicondictionary;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.family.lexicondictionary.Adapter.RecyclerViewAdapter;
import com.example.family.lexicondictionary.Model.Attachment;
import com.example.family.lexicondictionary.Model.Detail;
import com.example.family.lexicondictionary.Model.Word;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DisplayActivity extends AppCompatActivity {
    String[] languages = {"English", "Malay", "Mandarin"}; //for develop purpose only
    List<String> emotion = new ArrayList<String>();
    List<Bitmap> emoticon = new ArrayList<Bitmap>();
    int seekBarCurrent = 10000;
    double sentimentStrength=0;
    final static String addStatus= "ADD_STATUS";
    final static String editStatus= "EDIT_STATUS";
    final static String originalWordKey= "ORIGINAL_WORD";
    final static String translatedWordKey= "TRANSLATED_WORD";
    final static String translatedFromKey= "TRANSLATED_FROM";
    final static String translatedToKey= "TRANSLATED_TO";
    //List<String> emotionNameList;
    //ImageData[] emotionList;
    //List<Word> wordList;
    Word word;
    String wordUrl;
    Attachment attachment;
    Detail detail;

    Spinner translateFromList, translateToList;
    RecyclerView mRecyclerView;
    Button validateButton;
    EditText editTextOriginalWord;
    TextView textViewTranslatedWord, textViewSentiment;
    SeekBar seekBarSentiment;
    ImageButton imageButtonPlayPronunciation;
    ImageView imageViewPhoto;
    RatingBar ratingBarAccuracy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //ToDo: Add showProgress function in DisplayActivity

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

        seekBarSentiment.setMax(R.integer.seekBarMax);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seekBarSentiment.setMin(R.integer.seekBarMin);
        }*/
        seekBarSentiment.setProgress(seekBarCurrent);
        sentimentStrength = ((double)seekBarCurrent/1065680896.0)-1;
        //sentimentStrength = seekBarCurrent;
        textViewSentiment.setText(String.format("%.4f",sentimentStrength));

        /*seekBarSentiment.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarCurrent = progress;
                sentimentStrength = ((double)seekBarCurrent/1065680896.0)-1;
                //sentimentStrength = seekBarCurrent;
                textViewSentiment.setText(""+String.format("%.4f",sentimentStrength));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });*/

        //Disable input from user
        seekBarSentiment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        //rating by default
        ratingBarAccuracy.setRating(2.5f);

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
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        loadEmotion();
        mRecyclerView.setAdapter(new RecyclerViewAdapter(getApplicationContext(), emotion, emoticon));
        /*
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this,
                emotionNameList, emotionList);
        mRecyclerView.setAdapter(recyclerViewAdapter);
        */

        editTextOriginalWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textViewTranslatedWord.setText("");
                imageViewPhoto.setImageResource(R.mipmap.no_photo);
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
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                //Check if the required field is empty
                                if(!(editTextOriginalWord.getText()==null||
                                        editTextOriginalWord.getText().toString().equals("")||
                                        editTextOriginalWord.getText().toString().equals(" "))){
                                    translate();
                                }else{
                                    Toast.makeText(getApplicationContext(), "Original word is empty.",Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        DELAY
                );
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
    }

    private void loadEmotion(){
        emotion.clear();
        emoticon.clear();

        emotion.add("Happy");
        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.happy));
        emotion.add("Sad");
        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.sad));
        emotion.add("Smile");
        emoticon.add(BitmapFactory.decodeResource(getResources(), R.mipmap.smile));
    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }

    public void requestWord(){
        if(!isConnected())
            Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_SHORT).show();
        else{
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            wordUrl = getString(R.string.url_selectSpecWord);
            wordUrl += "originalContent="+editTextOriginalWord.getText().toString();
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
                                requestImage();
                            } catch (NullPointerException e) {
                                Toast.makeText(getApplicationContext(), "Original text is empty.", Toast.LENGTH_SHORT).show();
                            }catch(Exception e){
                                if(e.getMessage().equals("Index 0 out of range [0..0)")) {
                                    if(!(editTextOriginalWord.getText()==null||
                                            editTextOriginalWord.getText().toString().equals("")||
                                            editTextOriginalWord.getText().toString().equals(" ")))
                                        noResult();
                                }
                                else
                                    Toast.makeText(getApplicationContext(), "Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(getApplicationContext(), "Volley Error:" + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            queue.add(jsonObjectRequest);
        }
    }

    public void requestImage(){
        if(!isConnected())
            Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_SHORT).show();
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
                                if(e.getMessage().equals("Index 0 out of range [0..0)"))
                                    Toast.makeText(getApplicationContext(), "This translation has no image.", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(getApplicationContext(), "Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(getApplicationContext(), "Volley Error:" + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            queue.add(jsonObjectRequest);
        }
    }

    public void requestDetail(){
        if(!isConnected())
            Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_SHORT).show();
        else{
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            String url = getString(R.string.url_selectSpecDetail);
            url += "wordID="+word.getId();

            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(url,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                JSONObject recordResponse = (JSONObject) response.get(0);
                                int id = recordResponse.getInt("id");
                                double sentiment = recordResponse.getDouble("sentiment");
                                String emotion = recordResponse.getString("emotion");
                                double accuracy = recordResponse.getDouble("accuracy");

                                detail = new Detail(id, sentiment, emotion, accuracy, word.getId());

                                //setting into their respective fields
                                //seekBar
                                seekBarCurrent = (int)(sentiment*20000);
                                seekBarSentiment.setProgress(seekBarCurrent);
                                sentimentStrength = ((double)seekBarCurrent/1065680896.0)-1;
                                textViewSentiment.setText(""+String.format("%.4f",sentimentStrength));

                                //emotion
                                //ToDo: setting emotion?

                                //ratingBar
                                ratingBarAccuracy.setRating((float)accuracy);
                            }catch(Exception e){
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
                editTextOriginalWord.getText().toString().equals(" ")||
                textViewTranslatedWord.getText()==null||
                textViewTranslatedWord.getText().toString().equals(""))){
            //Check if this record is provided by user and not from original database
            if(!word.getStatus().equals("default")) {
                Intent intent = new Intent(this, AddActivity.class);
                //putExtra( KEY, VALUE);
                intent.putExtra("STATUS", editStatus);
                intent.putExtra(originalWordKey, editTextOriginalWord.getText().toString());
                intent.putExtra(translatedWordKey, textViewTranslatedWord.getText().toString());
                intent.putExtra(translatedFromKey, translateFromList.getSelectedItem().toString());
                intent.putExtra(translatedToKey, translateToList.getSelectedItem().toString());
                startActivity(intent);
            }else{
                Toast.makeText(getApplicationContext(), "Unable to edit default word.", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Original text is empty.", Toast.LENGTH_SHORT).show();
        }
    }

    private void translate(){
        //check internet & database connection
        if(!isConnected()){
            Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_SHORT).show();
        }else{
            requestWord();
        }
    }

    private void noResult(){
        Toast.makeText(getApplicationContext(),"No result found.",Toast.LENGTH_SHORT).show();
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
