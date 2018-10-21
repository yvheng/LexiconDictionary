package com.example.family.lexicondictionary;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.family.lexicondictionary.R;

public class AddActivity extends AppCompatActivity {
    String[] languages = {"English", "Malay", "Mandarin"}; //for develop purpose

    Spinner translateFromList, translateToList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        translateFromList = (Spinner)findViewById(R.id.translateFrom);
        translateToList = (Spinner)findViewById(R.id.translateTo) ;
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, languages);
        translateFromList.setAdapter(spinnerAdapter);
        translateToList.setAdapter(spinnerAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.saveFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save the entry into database with status for validation
            }
        });
    }

}
