package com.example.family.lexicondictionary;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import static com.example.family.lexicondictionary.DisplayActivity.addStatus;
import static com.example.family.lexicondictionary.DisplayActivity.originalWordKey;
import static com.example.family.lexicondictionary.DisplayActivity.translatedFromKey;
import static com.example.family.lexicondictionary.DisplayActivity.translatedToKey;
import static com.example.family.lexicondictionary.LoginActivity.MY_PREFS_NAME;
import static com.example.family.lexicondictionary.LoginActivity.userIDKey;

public class AddPopUpWindow extends AppCompatActivity {

    String originalWord;
    String translateFrom;
    String translateTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_pop_up_window);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width * 0.7), (int) (height * 0.4));

        Bundle extras = getIntent().getExtras();
        originalWord = extras.getString(originalWordKey);
        translateFrom = extras.getString(translatedFromKey);
        translateTo = extras.getString(translatedToKey);
    }

    public void buttonYes(View v){
        Intent intent = new Intent(this, AddActivity.class);
        intent.putExtra(originalWordKey, originalWord);
        intent.putExtra(translatedFromKey, translateFrom);
        intent.putExtra(translatedToKey, translateTo);
        intent.putExtra("STATUS", addStatus);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1){
            if(resultCode == Activity.RESULT_OK){
                Toast.makeText(getApplicationContext(), "New word added.", Toast.LENGTH_LONG).show();
                finish();
            }
            if(resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(getApplicationContext(), "Operation cancelled.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    public void buttonNo(View v){
        onBackPressed();
    }
}
