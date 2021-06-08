package com.example.movierecommender;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetails extends AppCompatActivity {
    Movie movie;
    ImageView ivPoster;
    TextView tvMovieTitle;
    TextView tvMovieOverview;
    TextView tvMovieGenre;
    TextView tvImdbRating;
    TextView tvPopularity;
    TextView tvTotalVotes ;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ivPoster = findViewById(R.id.ivPoster);
        tvMovieTitle = findViewById(R.id.tvTitle);
        tvMovieOverview = findViewById(R.id.tvOverView);
        tvMovieGenre = findViewById(R.id.tvGenre);
        tvImdbRating = findViewById(R.id.tvImdbRating);
        tvPopularity = findViewById(R.id.tvPopularity);
        tvTotalVotes = findViewById(R.id.tvVotes);
        progressBar = findViewById(R.id.progressBar);

        Intent i = getIntent();

        FetcMovieDetails fetcMovieDetails = new FetcMovieDetails();
        fetcMovieDetails.execute(i.getStringExtra("MovieId"));





    }

    class FetcMovieDetails extends AsyncTask<String,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                movie = APICalls.getMovieFromTMDB(strings[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            tvMovieTitle.setText(movie.getMovieTitle());
            Picasso.get().load("https://image.tmdb.org/t/p/w200/"+movie.getPosterUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(ivPoster);
            tvMovieOverview.setText(movie.getOverview());;
            tvMovieGenre.setText(movie.getGenres().toString());
            tvImdbRating.setText(movie.getImdbRating().toString());
            tvPopularity.setText(movie.getPopularity().toString());
            tvTotalVotes.setText(movie.getVoteCount().toString());
            progressBar.setVisibility(View.GONE);
        }
    }


}