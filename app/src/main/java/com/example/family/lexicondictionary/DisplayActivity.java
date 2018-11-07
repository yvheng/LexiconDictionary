package com.example.family.lexicondictionary;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

public class DisplayActivity extends AppCompatActivity {
    String[] languages = {"English", "Malay", "Mandarin"}; //for develop purpose only

    ProgressBar progressBar;
    Spinner translateFromList, translateToList, temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        translateFromList= (Spinner)findViewById(R.id.translateFrom);
        translateToList= (Spinner)findViewById(R.id.translateTo);
        progressBar= (ProgressBar)findViewById(R.id.progressBar) ;
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, languages);

        translateFromList.setAdapter(spinnerAdapter);
        translateToList.setAdapter(spinnerAdapter);
        translateFromList.setOnTouchListener(spinnerOnTouch);
        translateToList.setOnTouchListener(spinnerOnTouch);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        }else{
            Drawable progressDrawable = progressBar.getIndeterminateDrawable().mutate();
            progressDrawable.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            progressBar.setProgressDrawable(progressDrawable);
        }
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
