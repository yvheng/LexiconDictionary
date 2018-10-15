package com.example.family.lexicondictionary.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.family.lexicondictionary.R;

/**
 * Created by Yv Heng on 15/10/2018.
 */

public class HistoryListAdapter extends ArrayAdapter {
    //to reference the Activity
    private final Activity context;
    //to store the list of original word
    private final String[] originalWordArray;
    //to store the list of translated word
    private final String[] translatedWordArray;

    public HistoryListAdapter(Activity context, String[] originalWordArray, String[] translatedWordArray) {
        super(context, R.layout.history_list, originalWordArray);

        this.context = context;
        this.originalWordArray = originalWordArray;
        this.translatedWordArray = translatedWordArray;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.history_list, null, true);

        //this code gets references to objects in the history_list.xml file
        TextView originalWord = (TextView) rowView.findViewById(R.id.originalWord);
        TextView translatedWord = (TextView) rowView.findViewById(R.id.translatedWord);

        //this code sets the values of the objects to values from the arrays
        originalWord.setText(originalWordArray[position]);
        translatedWord.setText(translatedWordArray[position]);

        return rowView;
    }

}