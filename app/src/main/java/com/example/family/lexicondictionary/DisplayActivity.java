package com.example.family.lexicondictionary;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.family.lexicondictionary.Adapter.ImageData;
import com.example.family.lexicondictionary.Model.Word;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class DisplayActivity extends AppCompatActivity {
    String[] languages = {"English", "Malay", "Mandarin"}; //for develop purpose only
    int seekBarCurrent = 0;
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

    Spinner translateFromList, translateToList;
    RecyclerView mRecyclerView;
    Button validateButton;
    EditText editTextOriginalWord;
    TextView textViewTranslatedWord, textViewSentiment;
    SeekBar seekBarSentiment;

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

        seekBarSentiment.setMax(R.integer.seekBarMax);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seekBarSentiment.setMin(R.integer.seekBarMin);
        }*/
        seekBarSentiment.setProgress(seekBarCurrent);
        sentimentStrength = ((double)seekBarCurrent/1065680896.0)-1;
        //sentimentStrength = seekBarCurrent;
        textViewSentiment.setText(""+String.format("%.4f",sentimentStrength));

        seekBarSentiment.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        /*
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this,
                emotionNameList, emotionList);
        mRecyclerView.setAdapter(recyclerViewAdapter);
        */

        editTextOriginalWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textViewTranslatedWord.setText("");
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

        if(!isConnected()){
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
        }
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

            wordUrl = "http://i2hub.tarc.edu.my:8117/selectSpecWord.php?";
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

                                word = new Word(id, originalContent, translatedContent, originalLanguage,
                                        translatedLanguage,status, dateTimeAdded, userID);

                                //set retrieved word to the textView
                                textViewTranslatedWord.setText(word.getTranslatedContent());
                            } catch (NullPointerException e) {
                                Toast.makeText(getApplicationContext(), "Original text is empty.", Toast.LENGTH_SHORT).show();
                            }catch(Exception e){
                                if(e.getMessage().equals("Index 0 out of range [0..0)")) {
                                    if(!(editTextOriginalWord.getText()==null||
                                            editTextOriginalWord.getText().toString().equals("")||
                                            editTextOriginalWord.getText().toString().equals(" ")||
                                            textViewTranslatedWord.getText()==null||
                                            textViewTranslatedWord.getText().toString().equals("")))
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

        //Display popup message to ask user
        Intent intent = new Intent(this, AddPopUpWindow.class);
        startActivity(intent);
    }
}
