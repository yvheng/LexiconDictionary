package com.example.family.lexicondictionary.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.family.lexicondictionary.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<String> emotionName;
    private List<ImageData> imageData;
    private LayoutInflater layoutInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public RecyclerViewAdapter(Context context, List<String> emotionName, List<ImageData> imageData) {
        this.layoutInflater = LayoutInflater.from(context);
        this.imageData = imageData;
        this.emotionName = emotionName;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.recycler_view, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.emotion.setImageResource(imageData.get(position).getImageUrl());
        viewHolder.emotionName.setText(emotionName.get(position));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return emotionName.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView emotion;
        TextView emotionName;

        ViewHolder(View itemView) {
            super(itemView);
            emotion = itemView.findViewById(R.id.emotion);
            emotionName = itemView.findViewById(R.id.emotionName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public String getEmotionName(int id) {
        return emotionName.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
