package com.example.family.lexicondictionary;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.family.lexicondictionary.Adapter.ImageData;
import com.example.family.lexicondictionary.Adapter.RecyclerViewAdapter;

import java.util.List;

public class DisplayActivity extends AppCompatActivity {
    String[] languages = {"English", "Malay", "Mandarin"}; //for develop purpose only
    int seekBarCurrent = 5000;
    double sentimentStrength=0;
    final static String addStatus= "ADD_STATUS";
    final static String editStatus= "EDIT_STATUS";
    final static String originalWordKey= "ORIGINAL_WORD";
    final static String translatedWordKey= "TRANSLATED_WORD";
    final static String translatedFromKey= "TRANSLATED_FROM";
    final static String translatedToKey= "TRANSLATED_TO";
    List<String> emotionNameList;
    ImageData[] emotionList;

    Spinner translateFromList, translateToList, temp;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seekBarSentiment.setMin(R.integer.seekBarMin);
        }
        seekBarSentiment.setProgress(seekBarCurrent);
        sentimentStrength = (double)seekBarCurrent/2131296256.0;
        textViewSentiment.setText(""+String.format("%.4f",sentimentStrength));

        seekBarSentiment.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarCurrent = progress;
                sentimentStrength = (double)seekBarCurrent/2131296256.0;
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
        translateFromList.setOnTouchListener(spinnerOnTouch);
        translateToList.setOnTouchListener(spinnerOnTouch);

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

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //Check if the required field is empty
                if(!(editTextOriginalWord.getText()==null||
                        editTextOriginalWord.getText().toString().equals("")||
                        editTextOriginalWord.getText().toString().equals(" "))){
                    translate();
                }else{
                    emptyField();
                }
            }
        });
    }

    private View.OnTouchListener spinnerOnTouch = new View.OnTouchListener(){
        public boolean onTouch(View v, MotionEvent event){
            if(event.getAction() == MotionEvent.ACTION_UP){
                translate();
            }
            return true;
        }
    };

    public void exchange(View v){
        temp= findViewById(R.id.translateFrom);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, languages);
        temp.setAdapter(spinnerAdapter);

        temp.setSelection(translateFromList.getSelectedItemPosition());
        translateFromList.setSelection(translateToList.getSelectedItemPosition());
        translateToList.setSelection(temp.getSelectedItemPosition());
    }

    public void validate(View v){
        //Check if the required field is empty
        if(!(editTextOriginalWord.getText()==null||
                editTextOriginalWord.getText().toString().equals("")||
                editTextOriginalWord.getText().toString().equals(" "))){
            //Check if this record is provided by user and not from original database
            if(true) { //to be added
                Intent intent = new Intent(this, AddActivity.class);
                //putExtra( KEY, VALUE);
                intent.putExtra("STATUS", editStatus);
                intent.putExtra(originalWordKey, editTextOriginalWord.getText());
                intent.putExtra(translatedWordKey, textViewTranslatedWord.getText());
                intent.putExtra(translatedFromKey, translateFromList.getSelectedItem().toString());
                intent.putExtra(translatedToKey, translateToList.getSelectedItem().toString());
                startActivity(intent);
            }
        }else{
            emptyField();
            }

    }

    private void translate(){
        String result=""; //translatedWord
        //check internet & database connection

        //send originalWordText to database

        //check for result

        if(result.equals(null)||
                result.equals("")){
            noResult();
        }else{
            textViewTranslatedWord.setText(result);
        }
    }

    private void emptyField(){
        Toast.makeText(this, "Original word is empty.",Toast.LENGTH_LONG).show();
    }

    private void noResult(){
        Toast.makeText(this,"No result found.",Toast.LENGTH_SHORT).show();

        //Display popup message to ask user
        Intent intent = new Intent(this, AddPopUpWindow.class);
        startActivity(intent);
    }
}
