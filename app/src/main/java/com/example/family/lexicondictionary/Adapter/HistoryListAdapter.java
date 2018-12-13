package com.example.family.lexicondictionary.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.family.lexicondictionary.Model.Word;
import com.example.family.lexicondictionary.R;

import java.util.List;

public class HistoryListAdapter extends ArrayAdapter<Word> {
    public HistoryListAdapter(@NonNull Context context, int resource, @NonNull List<Word> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Word word = getItem(position);

        LayoutInflater inflater  = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.history_list, parent, false);

        TextView originalWord, translatedWord;

        originalWord = rowView.findViewById(R.id.originalWord);
        translatedWord = rowView.findViewById(R.id.translatedWord);

        try {
            originalWord.setText(word.getOriginalContent());
            translatedWord.setText(word.getTranslatedContent());
        }catch(NullPointerException ex){
            Toast.makeText(this.getContext(), "History list is empty",Toast.LENGTH_SHORT).show();
        }

        return rowView;
    }
}
