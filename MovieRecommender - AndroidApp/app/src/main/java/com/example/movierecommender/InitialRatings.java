package com.example.movierecommender;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

public class InitialRatings extends AppCompatActivity {

    ProgressBar progressBar;
    TextView tvInitial;
    Button btnSubmit;
    ArrayList<Float> movieRatings;
    ArrayList<Movie> movies;
    ArrayList<String> likedGenres;
    RecyclerView rvMovies;
    RecyclerViewAdopterMovie recyclerViewAdopterMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_ratings);

        likedGenres = getIntent().getStringArrayListExtra("likedGenres");

        movies = new ArrayList<Movie>();
        progressBar = findViewById(R.id.progressBar);
        tvInitial = findViewById(R.id.tvInitial);
        btnSubmit = findViewById(R.id.btnSubmit);
        rvMovies = findViewById(R.id.rvMovies);



        FetchMovies fetchMovies = new FetchMovies();
        fetchMovies.execute();



        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                movieRatings = recyclerViewAdopterMovie.getRatings();

                RateMoviesRegister rateMoviesRegister = new RateMoviesRegister();
                rateMoviesRegister.execute();

                progressBar.setVisibility(View.VISIBLE);
            }
        });




    }

    class RateMoviesRegister extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            for(int i = 0;i<movies.size();i++){
                if(movieRatings.get(i) == 0){
                    continue;
                }
                try {
                    APICalls.movieRating(USER_ID.userID,movies.get(i).getMovieId(),movieRatings.get(i).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            SharedPreferences sharedpreferences =  getSharedPreferences("userDetails", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("userId",USER_ID.userID);
            editor.commit();
            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            progressBar.setVisibility(View.GONE);
            startActivity(i);

        }
    }

    class FetchMovies extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            tvInitial.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            //have to be replaced with liked genres in a later stage


            ArrayList<String> topRecommenders = new ArrayList<>();

            try {
                for (int i=0;i<likedGenres.size();i++){
                    topRecommenders.addAll(APICalls.top10Movies(likedGenres.get(i)));
                }

                //eliminate redundancy at a later stage

                for(int i=0;i<topRecommenders.size();i++){
                    movies.add(APICalls.getMovieFromTMDB(topRecommenders.get(i)));
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            super.onPostExecute(voids);
            progressBar.setVisibility(View.GONE);
            tvInitial.setVisibility(View.GONE);

            recyclerViewAdopterMovie = new RecyclerViewAdopterMovie(InitialRatings.this,movies);
            rvMovies.setLayoutManager(new GridLayoutManager(InitialRatings.this,2));
            rvMovies.setAdapter(recyclerViewAdopterMovie);

        }
    }
}