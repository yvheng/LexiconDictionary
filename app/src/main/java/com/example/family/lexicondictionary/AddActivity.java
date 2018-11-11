package com.example.family.lexicondictionary;

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

import static com.example.family.lexicondictionary.DisplayActivity.addStatus;
import static com.example.family.lexicondictionary.DisplayActivity.editStatus;
import static com.example.family.lexicondictionary.DisplayActivity.originalWordKey;
import static com.example.family.lexicondictionary.DisplayActivity.translatedFromKey;
import static com.example.family.lexicondictionary.DisplayActivity.translatedToKey;
import static com.example.family.lexicondictionary.DisplayActivity.translatedWordKey;

public class AddActivity extends AppCompatActivity {
    String[] languages = {"English", "Malay", "Mandarin"}; //for develop purpose
    String status;

    TextView textViewTitle;
    EditText editTextOriginalWord, editTextTranslatedWord;
    Spinner translateFromList, translateToList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        translateFromList = findViewById(R.id.translateFrom);
        translateToList = findViewById(R.id.translateTo) ;
        editTextOriginalWord = findViewById(R.id.editTextOriginal);
        editTextTranslatedWord = findViewById(R.id.editTextTranslated);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, languages);
        translateFromList.setAdapter(spinnerAdapter);
        translateToList.setAdapter(spinnerAdapter);

        FloatingActionButton fab = findViewById(R.id.saveFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save the entry into database with status for validation

                //if(connection_done)
                    Toast.makeText(AddActivity.this, "New entry added.",Toast.LENGTH_LONG).show();
            }
        });

        textViewTitle = findViewById(R.id.textViewTitle);

        checkStatus();
        if(status.equals(addStatus)){
            textViewTitle.setText(R.string.title_addNewEntry);
        }else if(status.equals(editStatus)){
            textViewTitle.setText(R.string.title_editEntry);
        }
        setFields();
    }

    private void setFields(){
        Bundle extras = getIntent().getExtras();

        String originalWord = extras.getString(originalWordKey);
        String translatedWord = extras.getString(translatedWordKey);
        try{
            editTextOriginalWord.setText(originalWord);
            editTextTranslatedWord.setText(translatedWord);
        }catch(Exception e){

        }

        int i, u;
        for(i=0;i<languages.length-1;i++){
            if(languages[i].equals(extras.getString(translatedFromKey)))
                break;
        }
        for(u=0;u<languages.length-1;u++){
            if(languages[u].equals(extras.getString(translatedToKey)))
                break;
        }
        try{
            translateFromList.setSelection(i);
            translateToList.setSelection(u);
        }catch (Exception e){

        }
    }

    private void checkStatus(){
        Bundle extras = getIntent().getExtras();
        status = extras.getString("STATUS");
    }
}
