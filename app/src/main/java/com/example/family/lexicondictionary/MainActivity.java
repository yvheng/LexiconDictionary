package com.example.family.lexicondictionary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.family.lexicondictionary.Adapter.HistoryListAdapter;

public class MainActivity extends AppCompatActivity {
    //SharedPreferences, store all history from that specific user
    String[] originalWordArray = {"Hello","YH"}; //to be added later
    String[] translatedWordArray = {"Hi", "Yv Heng"};
    boolean[] favoriteWord = {false, false};

    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HistoryListAdapter historyListAdapter = new HistoryListAdapter(this,
                originalWordArray, translatedWordArray, favoriteWord);

        listView = findViewById(R.id.historyList);
        listView.setAdapter(historyListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Object o = listView.getItemAtPosition(position);
                //thing to do when it's clicked
                //using originalWordArray[position] to get position of item clicked)

            }
        });
    }

    public void newTranslate(View v){
        Intent intent = new Intent(this, DisplayActivity.class);
        startActivity(intent);
    }
}
