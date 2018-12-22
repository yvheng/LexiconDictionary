package com.example.family.lexicondictionary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.family.lexicondictionary.Adapter.HistoryListAdapter;
import com.example.family.lexicondictionary.Model.Word;

import java.util.ArrayList;
import java.util.List;

import static com.example.family.lexicondictionary.DisplayActivity.prefName;

public class MainActivity extends AppCompatActivity {
    //SharedPreferences, store all history from that specific user
    /*String[] originalWordArray = new String[16];
    String[] translatedWordArray = new String[16];*/
    String[] originalWordArray = {"food","enak","beautiful"};
    String[] translatedWordArray = {"makanan","delicious","cantik"};
    boolean[] favoriteWord = {false, false}; //ToDo: Add this variable into Word model or create new Object
    List<Word> historyList;
    SharedPreferences pref;
    String[] historyWordID = new String[16];
    String[] historyOriginalWord = new String[16];
    String[] historyTranslatedWord = new String[16];

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

        /*
        pref = getApplicationContext().getSharedPreferences(prefName, 0);
        SharedPreferences.Editor editor = pref.edit();
        //loading history list

        int i;
        int count = 1;
        for(i=0; i<15;i++){
            historyOriginalWord[i]= "Original"+String.valueOf(count);
            historyTranslatedWord[i]= "Translated"+String.valueOf(count);
            count++;
        }
        try {
            i=1;
            while (!(pref.getString("historyID"+i, "").equals(null)||pref.getString("historyID"+i, "").equals(""))) {
                //not empty
                historyWordID[i - 1] = pref.getString("historyID" + i, "");
                i++;
            }
        }catch(Exception e){
            Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        try {
            if (i != 1) {
                for (i = 0; i < historyWordID.length; i++) {
                    originalWordArray[i] = pref.getString(historyOriginalWord[i], null);
                    translatedWordArray[i] = pref.getString(historyTranslatedWord[i], null);
                    Word historyWord = new Word(0, originalWordArray[i], translatedWordArray[i],
                            null, null, null,
                            null, 0, 0); //ToDo: Add more details in history list
                    historyList.add(historyWord);
                }
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }*/


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
