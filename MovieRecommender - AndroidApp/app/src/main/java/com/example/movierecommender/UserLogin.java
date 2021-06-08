package com.example.movierecommender;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class UserLogin extends AppCompatActivity {

    EditText etEmail;
    EditText etPassword;
    ProgressBar progressBar;
    Button btnLogin;
    TextView tvDonthaveacount;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        tvDonthaveacount= findViewById(R.id.tvDonthaveacount);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        progressBar = findViewById(R.id.progressBar);
        btnLogin = findViewById(R.id.btnLogin);

        sharedpreferences =  getSharedPreferences("userDetails", Context.MODE_PRIVATE);

        String userId = sharedpreferences.getString("userId","-1");
        if(!userId.equals("-1")){
            USER_ID.userID = userId;
            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
        }


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login login = new Login();
                String userEmail = etEmail.getText().toString();
                String userPassword = etPassword.getText().toString();
                if(userEmail.isEmpty()){
                    etEmail.setError("Enter an email Id");
                    return;
                }
                if(userPassword.isEmpty()){
                    etPassword.setError("Enter a password");
                    return;
                }
                login.execute(userEmail,userPassword);
            }
        });


        tvDonthaveacount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),UserRegistration.class);
                startActivity(i);
            }
        });



    }

    class Login extends AsyncTask<String, Void, Integer>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... strings) {
            try {

                return APICalls.userLogin(strings[0],strings[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return -2;
        }

        @Override
        protected void onPostExecute(Integer userID) {
            super.onPostExecute(userID);
            progressBar.setVisibility(View.GONE);
            if(userID == -1){
                etEmail.setError("USER NOT FOUND!!");
                Toast.makeText(UserLogin.this,"ENTER VALID USER CREDENTIALS!!",Toast.LENGTH_LONG).show();
            }
            else if(userID == -2){
                Toast.makeText(UserLogin.this,"SOMETHING WENT WRONG!!",Toast.LENGTH_LONG).show();
            }else{
                USER_ID.userID = userID.toString();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("userId",USER_ID.userID);
                editor.commit();
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }



        }
    }
}