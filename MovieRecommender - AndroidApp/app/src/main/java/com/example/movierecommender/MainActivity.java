package com.example.movierecommender;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.service.controls.actions.FloatAction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ShareActionProvider;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    MoviePredicted moviePredicted;
    OnSwipeTouchListener swipe;
    ImageView ivView;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        progressBar = findViewById(R.id.progressBar);
        ivView = findViewById(R.id.ivMoviePoster);

        FetchInitialMovies fetchInitialMovies = new FetchInitialMovies();
        fetchInitialMovies.execute(USER_ID.userID);



        FloatingActionButton fab = findViewById(R.id.floatingActionButton);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Movie currentMovie = swipe.getCurrentMovie();

                Intent i = new Intent(MainActivity.this,MovieDetails.class);
                i.putExtra("MovieId",currentMovie.getMovieId());
                startActivity(i);

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuHelp:
                Toast.makeText(getApplicationContext(),"Help Menu",Toast.LENGTH_SHORT).show();
                break;

            case R.id.menuLikedMovies:
                Intent likeMovie = new Intent(getApplicationContext(),LikedMovies.class);
                startActivity(likeMovie);
                break;

            case R.id.menuAboutUs:
                Intent aboutUs = new Intent(getApplicationContext(),AboutUs.class);
                startActivity(aboutUs);
                break;

            case R.id.menuLogout:

                Intent logOut = new Intent(getApplicationContext(),UserLogin.class);
                SharedPreferences sharedpreferences =  getSharedPreferences("userDetails", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("userId","-1");
                editor.commit();
                startActivity(logOut);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class FetchInitialMovies extends AsyncTask<String,Void,ArrayList<Movie>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... strings) {
            ArrayList<Movie> movieArrayList = new ArrayList<>();
            try {
                ArrayList<String> movieIds = APICalls.rand10Movies(USER_ID.userID);
                Log.d("xD",movieIds.size()+"");
                for (int i=0;i<movieIds.size();i++){
                    movieArrayList.add(APICalls.getMovieFromTMDB(movieIds.get(i)));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return movieArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            super.onPostExecute(movies);

            moviePredicted = new MoviePredicted();
            moviePredicted.setMovie(movies);

            swipe = new OnSwipeTouchListener(MainActivity.this,moviePredicted,ivView);
            ivView.setOnTouchListener(swipe);
            progressBar.setVisibility(View.GONE);



        }
    }

}