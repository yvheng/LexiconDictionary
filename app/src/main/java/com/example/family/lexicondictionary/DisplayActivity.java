package com.example.family.lexicondictionary;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

public class DisplayActivity extends AppCompatActivity {
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        }else{
            Drawable progressDrawable = progressBar.getIndeterminateDrawable().mutate();
            progressDrawable.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            progressBar.setProgressDrawable(progressDrawable);
        }
    }
}
