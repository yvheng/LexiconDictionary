package com.example.family.lexicondictionary;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.family.lexicondictionary.Model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.family.lexicondictionary.LoginActivity.userIDKey;

public class RegisterActivity extends AppCompatActivity {

    EditText editTextUsername, editTextPassword, editTextEmail;
    Button buttonSignUp;
    View mProgressView;
    View mRegisterFormView;

    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        mProgressView = findViewById(R.id.register_progress);
        mRegisterFormView = findViewById(R.id.registerForm);
        editTextEmail = findViewById(R.id.editTextEmail);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(editTextEmail.getText())) {
                    editTextEmail.setError("Empty field");
                    editTextEmail.requestFocus();
                }else if(TextUtils.isEmpty(editTextUsername.getText())){
                    editTextUsername.setError("Empty field");
                    editTextUsername.requestFocus();
                }else {
                    showProgress(true);
                    addUser(editTextUsername.getText().toString());
                }
            }
        });

        if(!isConnected())
            Toast.makeText(getApplicationContext(), "No Internet connection.", Toast.LENGTH_SHORT).show();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    public void addUser(final String username) {
        String url = getString(R.string.url_checkUser);

        //PreExecute
        if (!isConnected())
            Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_SHORT).show();
        else {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            url += "username=" + username;

            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(url,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                JSONObject recordResponse = (JSONObject) response.get(0);
                                int id = Integer.parseInt(recordResponse.getString("id"));

                                showProgress(false);
                                //username is used
                                Toast.makeText(getApplicationContext(), "Username is taken", Toast.LENGTH_SHORT).show();
                                editTextUsername.setError(getString(R.string.error_invalid_register));
                                editTextUsername.requestFocus();

                            } catch (Exception e) {
                                if (e.getMessage().equals("Index 0 out of range [0..0)")) {
                                    //username available
                                    //insertUser(username, editTextPassword.getText().toString());
                                    makeServiceCallAddUser(getApplicationContext(),
                                            getString(R.string.url_writeUser),
                                            username, editTextPassword.getText().toString(), editTextEmail.getText().toString());
                                    showProgress(false);
                                } else
                                    Toast.makeText(getApplicationContext(), "Error 1:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(getApplicationContext(), "Error 2:" + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            queue.add(jsonObjectRequest);
        }
    }

    public void makeServiceCallAddUser(Context context, String url, final String username, final String password, final String email) {
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
                            Toast.makeText(getApplicationContext(), "New user created.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Error :" + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", username);
                    params.put("password", password);
                    params.put("email", email);

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
}
