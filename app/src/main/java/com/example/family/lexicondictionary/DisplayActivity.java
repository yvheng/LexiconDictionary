package com.example.family.lexicondictionary;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.family.lexicondictionary.Adapter.ImageData;
import com.example.family.lexicondictionary.Adapter.RecyclerViewAdapter;

import java.util.List;

public class DisplayActivity extends AppCompatActivity {
    String[] languages = {"English", "Malay", "Mandarin"}; //for develop purpose only
    final static String addStatus= "ADD_STATUS";
    final static String editStatus= "EDIT_STATUS";
    final static String originalWordKey= "ORIGINAL_WORD";
    final static String translatedWordKey= "TRANSLATED_WORD";
    final static String translatedFromKey= "TRANSLATED_FROM";
    final static String translatedToKey= "TRANSLATED_TO";
    List<String> emotionNameList;
    ImageData[] emotionList;

    ProgressBar progressBar;
    Spinner translateFromList, translateToList, temp;
    RecyclerView mRecyclerView;
    Button validateButton;
    EditText editTextOriginalWord;
    TextView textViewTranslatedWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        translateFromList= findViewById(R.id.translateFrom);
        translateToList= findViewById(R.id.translateTo);
        progressBar= findViewById(R.id.progressBar) ;
        validateButton= findViewById(R.id.validateButton);
        editTextOriginalWord= findViewById(R.id.originalWord);
        textViewTranslatedWord= findViewById(R.id.translatedWord);

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

        mRecyclerView =  findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                layoutManager.getOrientation());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        /*
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this,
                emotionNameList, emotionList);
        mRecyclerView.setAdapter(recyclerViewAdapter);
        */
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
        temp= findViewById(R.id.translateFrom);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, languages);
        temp.setAdapter(spinnerAdapter);

        temp.setSelection(translateFromList.getSelectedItemPosition());
        translateFromList.setSelection(translateToList.getSelectedItemPosition());
        translateToList.setSelection(temp.getSelectedItemPosition());
    }

    public void validate(View v){
        Intent intent = new Intent(this, AddActivity.class);
        //putExtra( KEY, VALUE);
        intent.putExtra("STATUS", editStatus);
        intent.putExtra(originalWordKey, editTextOriginalWord.getText());
        intent.putExtra(translatedWordKey, textViewTranslatedWord.getText());
        intent.putExtra(translatedFromKey, translateFromList.getSelectedItem().toString());
        intent.putExtra(translatedToKey, translateToList.getSelectedItem().toString());
        startActivity(intent);
    }

    private void noResult(){
        Toast.makeText(this,"No result found.",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, AddActivity.class);
        intent.putExtra("STATUS", addStatus);
        startActivity(intent);
    }
}
