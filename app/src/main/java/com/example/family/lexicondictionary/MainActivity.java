package com.example.family.lexicondictionary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.family.lexicondictionary.Adapter.HistoryListAdapter;
import com.example.family.lexicondictionary.Model.Word;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //SharedPreferences, store all history from that specific user
    String[] originalWordArray = {"Hello","YH"}; //for developing purpose
    String[] translatedWordArray = {"Hi", "Yv Heng"};
    boolean[] favoriteWord = {false, false}; //ToDo: Add this variable into Word model or create new Object
    List<Word> historyList;

    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        historyList = new ArrayList<>();

        historyList.clear();
        //loading history list
        for(int i=0; i < originalWordArray.length; i++) {
            Word historyWord = new Word(0, originalWordArray[i], translatedWordArray[i],
                    null, null, null,
                    null, 0, 0); //ToDo: Add more details in history list
            historyList.add(historyWord);
        }

        //setting history adapter to history list view
        HistoryListAdapter historyListAdapter = new HistoryListAdapter(this,
                R.layout.history_list, historyList);

        listView = findViewById(R.id.historyList);
        listView.setAdapter(historyListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Object o = listView.getItemAtPosition(position);
                //ToDo: Add function when the history is clicked
                //using originalWordArray[position] to get position of item clicked)

            }
        });


    }

    public void newTranslate(View v){
        Intent intent = new Intent(this, DisplayActivity.class);
        startActivity(intent);
    }
}
