package com.example.family.lexicondictionary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import static com.example.family.lexicondictionary.DisplayActivity.addStatus;

public class AddPopUpWindow extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_pop_up_window);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int)(width * 0.7),(int)(height * 0.4));
    }

    public void buttonYes(View v){
        Intent intent = new Intent(this, AddActivity.class);
        intent.putExtra("STATUS", addStatus);
        startActivity(intent);
    }

    public void buttonNo(View v){

    }
}
