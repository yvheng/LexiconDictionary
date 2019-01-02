package com.example.family.lexicondictionary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.family.lexicondictionary.Adapter.HistoryListAdapter;
import com.example.family.lexicondictionary.Model.Word;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static com.example.family.lexicondictionary.DisplayActivity.prefName;
import static com.example.family.lexicondictionary.LoginActivity.MY_PREFS_NAME;

public class MainActivity extends AppCompatActivity {
    //boolean[] favoriteWord = {false, false}; //ToDo: Add this variable into Word model or create new Object
    List<Word> historyList = new ArrayList<>();
    HistoryListAdapter historyListAdapter;
    SharedPreferences pref;
    final static String userIDKey= "USER_ID";

    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.historyList);
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
        historyList.clear();
        loadHistory();

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

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }

    public void loadHistory(){
        if(!isConnected()) {
            Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_SHORT).show();
        }
        else{
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            String url = getString(R.string.url_selectHistory);
            pref = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            url += "userID="+String.valueOf(pref.getInt(userIDKey, 0));

            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(url,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                for(int i=0; i<response.length(); i++) {
                                    JSONObject recordResponse = (JSONObject) response.get(i);
                                    String original = recordResponse.getString("original");
                                    String translation = recordResponse.getString("translation");

                                    Word historyWord = new Word(0, original, translation,
                                                null, null, null,
                                                null, 0, 0); //ToDo: Add more details in history list
                                    historyList.add(historyWord);
                                }
                                historyListAdapter = new HistoryListAdapter(getApplicationContext(),R.layout.history_list, historyList);
                                listView.setAdapter(historyListAdapter);
                            } catch (NullPointerException e) {
                                Toast.makeText(getApplicationContext(), "Error: Please re-login to load history", Toast.LENGTH_SHORT).show();
                            }catch(Exception e){
                                if(e.getMessage().equals("Index 0 out of range [0..0)")) {
                                    Toast.makeText(getApplicationContext(),"No history record",Toast.LENGTH_SHORT).show();
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
}
