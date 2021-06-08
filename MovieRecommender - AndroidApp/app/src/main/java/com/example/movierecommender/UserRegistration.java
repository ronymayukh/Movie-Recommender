package com.example.movierecommender;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Console;
import java.util.ArrayList;

public class UserRegistration extends AppCompatActivity {

    EditText etName;
    EditText etEmail;
    EditText etPassword;
    EditText etPasswordConfirm;
    ArrayList<CheckBox> genre;
    ArrayList<String> likedGenre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        genre = new ArrayList<>();
        genre.add(findViewById(R.id.cbAnimation));
        genre.add(findViewById(R.id.cbComedy));
        genre.add(findViewById(R.id.cbFamily));
        genre.add(findViewById(R.id.cbAdventure));
        genre.add(findViewById(R.id.cbFantasy));
        genre.add(findViewById(R.id.cbRomance));
        genre.add(findViewById(R.id.cbDrama));
        genre.add(findViewById(R.id.cbAction));
        genre.add(findViewById(R.id.cbCrime));
        genre.add(findViewById(R.id.cbThriller));
        genre.add(findViewById(R.id.cbHorror));
        genre.add(findViewById(R.id.cbHistory));
        genre.add(findViewById(R.id.cbScienceFiction));
        genre.add(findViewById(R.id.cbMystery));
        genre.add(findViewById(R.id.cbWar));
        genre.add(findViewById(R.id.cbForeign));
        genre.add(findViewById(R.id.cbMusic));
        genre.add(findViewById(R.id.cbDocumentary));
        genre.add(findViewById(R.id.cbWestern));
        genre.add(findViewById(R.id.cbTVMovie));

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);

        Button btnReg = findViewById(R.id.btnReg);

        likedGenre = new ArrayList<>();

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etName.getText().toString().isEmpty()){
                    etName.setError("Enter Your Name");
                    return;
                }
                if(etEmail.getText().toString().isEmpty()){
                    etEmail.setError("Enter Your Email");
                    return;
                }
                if(etPassword.getText().toString().isEmpty()){
                    etPassword.setError("Enter a Password");
                    return;
                }
                if(etPasswordConfirm.getText().toString().isEmpty()){
                    etPasswordConfirm.setError("Enter the Password again");
                    return;
                }

                if (!etPassword.getText().toString().equals(etPasswordConfirm.getText().toString())){
                    etPasswordConfirm.setError("Your Passwords don't match");
                    return;
                }

                RegisterUser registerUser = new RegisterUser();
                registerUser.execute(etEmail.getText().toString(),etPassword.getText().toString(),etName.getText().toString());

            }
        });


    }

    class RegisterUser extends AsyncTask<String,Void,Integer>{

        @Override
        protected Integer doInBackground(String... strings) {

            try {
                int x1 = APICalls.userRegistration(strings[0],strings[1],strings[2]);
                if(x1 == 1){
                    int x2 = APICalls.userLogin(strings[0],strings[1]);


                    if(x2 == -1 || x2 == -2){
                        return -1;
                    }

                    USER_ID.userID = String.valueOf(x2);

                }

                return x1;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return -1;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(integer == 1){
                Toast.makeText(UserRegistration.this,"User Registered Successfully",Toast.LENGTH_SHORT).show();

                for (int i=0;i<genre.size();i++){
                    if(genre.get(i).isChecked()){
                        likedGenre.add(genre.get(i).getText().toString());
                    }
                }

                Intent i = new Intent(getApplicationContext(),InitialRatings.class);
                i.putExtra("likedGenres",likedGenre);
                startActivity(i);

            }else if(integer == 0){
                Toast.makeText(UserRegistration.this,"You are already Registered, Please try to log in",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(UserRegistration.this,"SOMETHING WENT WRONG!!",Toast.LENGTH_SHORT).show();
            }
        }
    }
}