package com.example.family.lexicondictionary;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.family.lexicondictionary.Model.Attachment;
import com.example.family.lexicondictionary.Model.Word;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static com.example.family.lexicondictionary.DisplayActivity.addStatus;
import static com.example.family.lexicondictionary.DisplayActivity.editStatus;
import static com.example.family.lexicondictionary.DisplayActivity.originalWordKey;
import static com.example.family.lexicondictionary.DisplayActivity.translatedFromKey;
import static com.example.family.lexicondictionary.DisplayActivity.translatedToKey;
import static com.example.family.lexicondictionary.DisplayActivity.translatedWordKey;
import static com.example.family.lexicondictionary.LoginActivity.MY_PREFS_NAME;
import static com.example.family.lexicondictionary.LoginActivity.userIDKey;

public class AddActivity extends AppCompatActivity {
    String[] languages = {"English", "Malay", "Mandarin"}; //for develop purpose
    final static int chooseImageKey= 100;
    final static int chooseVoiceKey= 101;

    TextView textViewTitle, textViewOriginalWord,textViewTranslateFrom, textViewTranslateTo,
        textViewImageFileName, textViewVoiceFileName;
    EditText editTextTranslatedWord;
    View addingProgress, addingForm;
    Word word;
    Attachment attachment;
    Button buttonGallery, buttonVoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        String originalWord = extras.getString(originalWordKey);
        String translateFrom = extras.getString(translatedFromKey);
        String translateTo = extras.getString(translatedToKey);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        int userID = prefs.getInt(userIDKey, 0);

        word = new Word(0, originalWord, "", translateFrom,
                translateTo,"", null,userID, userID);

        textViewTitle = findViewById(R.id.textViewTitle);
        textViewTranslateFrom = findViewById(R.id.textViewTranslateFrom);
        textViewTranslateTo = findViewById(R.id.textViewTranslateTo);
        textViewOriginalWord = findViewById(R.id.textViewOriginal);
        editTextTranslatedWord = findViewById(R.id.editTextTranslated);
        addingProgress = findViewById(R.id.addingProgress);
        addingForm = findViewById(R.id.addingForm);
        buttonGallery = findViewById(R.id.buttonGallery);
        buttonVoice = findViewById(R.id.buttonVoice);
        textViewImageFileName = findViewById(R.id.textViewImageFileName);
        textViewVoiceFileName = findViewById(R.id.textViewVoiceFileName);

        showProgress(false);

        FloatingActionButton fab = findViewById(R.id.saveFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check internet connection again
                if (isConnected()) {
                    //check if translated word is empty
                    if(!(editTextTranslatedWord.getText().toString().equals("")||
                            editTextTranslatedWord.getText().toString().equals(" "))) {
                        showProgress(true);
                        String url = getString(R.string.url_writeWord);
                        word.setTranslatedContent(editTextTranslatedWord.getText().toString());

                        makeServiceCallAddWord(getApplicationContext(), url, word);
                    }else{
                        Toast.makeText(getApplicationContext(), "Translated Word is empty.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "No Internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select image"),chooseImageKey);
            }
        });

        buttonVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select audio"),chooseVoiceKey);
            }
        });

        String status = extras.getString("STATUS");
        try {
            if (status.equals(addStatus)) {
                textViewTitle.setText(R.string.title_addNewEntry);
            } else if (status.equals(editStatus)) {
                textViewTitle.setText(R.string.title_editEntry);
            }
        }catch(NullPointerException e){

        }catch(Exception e){

        }
        setFields(originalWord, translateFrom, translateTo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //InputStream inputStream;
        if (requestCode == chooseImageKey && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toast.makeText(getApplicationContext(), "No image selected.",Toast.LENGTH_SHORT).show();
                return;
            }else {/*
                try {*/
                    //inputStream = this.getContentResolver().openInputStream(data.getData());
                    Uri uri = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        textViewImageFileName.setText(uri.toString());

                        String photo = getStringImage(bitmap);
                        /*//Display image
                        ImageView imageView = findViewById(R.id.imageViewPreview);
                        imageView.setImageBitmap(bitmap);*/
                        attachment.setPhoto(photo);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }/*
                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "File not found.", Toast.LENGTH_SHORT).show();
                }*/
            }
        }
        if(requestCode == chooseVoiceKey && resultCode == Activity.RESULT_OK){
            if (data == null) {
                Toast.makeText(getApplicationContext(), "No voice file selected.",Toast.LENGTH_SHORT).show();
                return;
            }else {
                Uri uri = data.getData();
                textViewVoiceFileName.setText(uri.toString());
                File audioFile = new File(uri.toString());
                String audio = getStringAudio(audioFile);
                attachment.setPronunciation(audio);
            }
        }
        if(resultCode == Activity.RESULT_CANCELED)
            Toast.makeText(getApplicationContext(), "Operation cancelled, no file selected.", Toast.LENGTH_SHORT).show();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        addingForm.setVisibility(show ? View.GONE : View.VISIBLE);
        addingForm.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                addingForm.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        addingProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        addingProgress.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                addingProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    public void makeServiceCallAddWord(Context context, String url, final Word word) {
        //mPostCommentResponse.requestStarted();
        RequestQueue queue = Volley.newRequestQueue(context);

        //Send data
        try {
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //check if attachment is chose
                            if(attachment.getPhoto()==null||
                                    attachment.getPronunciation()==null){
                                showProgress(false);
                                Intent returnIntent = new Intent();
                                setResult(Activity.RESULT_OK, returnIntent);
                                finish();
                            }else {
                                //attachment = new Attachment(); //attachment is added separately in onActivityResult
                                makeServiceCallAddAttachment(getApplicationContext(),
                                        getString(R.string.url_writeAttachment),
                                        attachment);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showProgress(false);
                            Toast.makeText(getApplicationContext(), "Error :" + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("originalContent", word.getOriginalContent());
                    params.put("translatedContent", word.getTranslatedContent());
                    params.put("originalLanguage", word.getOriginalLanguage());
                    params.put("translatedLanguage", word.getTranslatedLanguage());
                    params.put("userID", String.valueOf(word.getUserID()));

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }
            };
            queue.add(postRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void makeServiceCallAddAttachment(Context context, String url, final Attachment attachment) {
        RequestQueue queue = Volley.newRequestQueue(context);

        try {
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            showProgress(false);
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showProgress(false);
                            Toast.makeText(getApplicationContext(), "Error :" + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("photo", attachment.getPhoto());
                    params.put("pronunciation", attachment.getPronunciation());
                    params.put("wordID", String.valueOf(attachment.getWordID()));

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }
            };
            queue.add(postRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }

    private void setFields(String originalWord , String translateFrom, String translateTo){
        try{
            textViewOriginalWord.setText(originalWord);
        }catch(Exception e){
            textViewOriginalWord.setText("");
        }

        int i, u;
        try {
            textViewTranslateFrom.setText(translateFrom);
            textViewTranslateTo.setText(translateTo);
        }catch(Exception e){
            textViewTranslateFrom.setText("");
            textViewTranslateTo.setText("");
        }
    }

    public String getStringImage(Bitmap bmp) {
        //encode image to base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public String getStringAudio(File file){
        //encode audio to base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] audioBytes = baos.toByteArray();
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf)))
                baos.write(buf, 0, n);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return Base64.encodeToString(audioBytes, Base64.DEFAULT);
    }

    public static Bitmap getImageString(String input){
        //decode image to base64
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public static void getAudioString(String input){
        byte[] decoded = Base64.decode(input, Base64.DEFAULT);
        try
        {
            File file = new File(Environment.getExternalStorageDirectory() + "/pronunciation.wav");
            FileOutputStream os = new FileOutputStream(file, true);
            os.write(decoded);
            os.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}
