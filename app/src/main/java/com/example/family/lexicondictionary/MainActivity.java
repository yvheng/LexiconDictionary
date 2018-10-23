package com.example.family.lexicondictionary;

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
    String[] languages = {"English", "Malay", "Mandarin"}; //for develop purpose

    //SharedPreferences, store all history from that specific user
    String[] originalWordArray = {"Hello","YH"}; //to be added later
    String[] translatedWordArray = {"Hi", "Yv Heng"};
    boolean[] favoriteWord = {false, false};

    TextView textViewTranslatedWord;

    Spinner translateFromList, translateToList, temp;

    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HistoryListAdapter historyListAdapter = new HistoryListAdapter(this,
                originalWordArray, translatedWordArray, favoriteWord);

        translateFromList= (Spinner)findViewById(R.id.translateFrom);
        translateToList= (Spinner)findViewById(R.id.translateTo);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, languages);

        translateFromList.setAdapter(spinnerAdapter);
        translateToList.setAdapter(spinnerAdapter);
        translateFromList.setOnTouchListener(spinnerOnTouch);
        translateToList.setOnTouchListener(spinnerOnTouch);

        listView = (ListView) findViewById(R.id.historyList);
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

    private View.OnTouchListener spinnerOnTouch = new View.OnTouchListener(){
        public boolean onTouch(View v, MotionEvent event){
            if(event.getAction() == MotionEvent.ACTION_UP){
                //translate(); or refresh();
            }
            return true;
        }
    };

    public void exchange(View v){
        temp= (Spinner)findViewById(R.id.translateFrom);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, languages);
        temp.setAdapter(spinnerAdapter);
        temp.setSelection(translateFromList.getSelectedItemPosition());
        translateFromList.setSelection(translateToList.getSelectedItemPosition());
        translateToList.setSelection(temp.getSelectedItemPosition());
    }
}
