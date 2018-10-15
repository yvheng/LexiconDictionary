package com.example.family.lexicondictionary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.family.lexicondictionary.Adapter.HistoryListAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    //Add data in the array from database, can be done in onCreate with methods
    String[] originalWordArray = {""};
    String[] translatedWordArray = {""};

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HistoryListAdapter historyListAdapter = new HistoryListAdapter(this,
                originalWordArray, translatedWordArray);

        listView = (ListView) findViewById(R.id.historyList);
        listView.setAdapter(historyListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                //thing to do when it's clicked
                //using originalWordArray[position] to get position of item clicked)

            }
        });
    }
}
