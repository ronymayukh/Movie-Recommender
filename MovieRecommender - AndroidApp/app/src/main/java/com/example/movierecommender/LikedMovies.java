package com.example.movierecommender;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class LikedMovies extends AppCompatActivity {
    RecyclerView rvMovieList;
    ArrayList<Movie> movies;
    ArrayList<Float> ratings;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_movies);
        progressBar = findViewById(R.id.progressBarLikedMovies);
        progressBar.setVisibility(View.VISIBLE);
        movies = new ArrayList<>();
        ratings = new ArrayList<>();
        rvMovieList = findViewById(R.id.rvMovieList);

        UserLikedMovies userLikedMovies = new UserLikedMovies();
        userLikedMovies.execute();

    }

    class UserLikedMovies extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                APICalls.userLikedMovies(movies,ratings,USER_ID.userID);
                Log.d("MOVIE",ratings.toString());
                Log.d("MOVIE",movies.toString());
            }catch (Exception e){

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            progressBar.setVisibility(View.GONE);
            System.out.println(movies.size());
            RecyclerViewAdopterLikedMovie recyclerViewAdopterLikedMovie = new RecyclerViewAdopterLikedMovie();
            rvMovieList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            rvMovieList.setAdapter(recyclerViewAdopterLikedMovie);

        }
    }

    class RecyclerViewAdopterLikedMovie extends RecyclerView.Adapter<LikedMovies.RecyclerViewAdopterLikedMovie.LikedMovieViewHolder>{

        @NonNull
        @NotNull
        @Override
        public LikedMovieViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.liked_movie_list,parent,false);

            return new LikedMovieViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull LikedMovieViewHolder holder, int position) {
            Picasso.get().load("https://image.tmdb.org/t/p/w200/"+movies.get(position).getPosterUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(holder.imageView);
            holder.movieTitle.setText(movies.get(position).getMovieTitle());
            holder.ratingBar.setRating(ratings.get(position));

        }

        @Override
        public int getItemCount() {
            return movies.size();
        }


        class LikedMovieViewHolder extends RecyclerView.ViewHolder{
            RatingBar ratingBar;
            TextView movieTitle;
            ImageView imageView;
            public LikedMovieViewHolder(@NonNull @NotNull View itemView) {
                super(itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getApplicationContext(),MovieDetails.class);
                        i.putExtra("MovieId",movies.get(getAdapterPosition()).getMovieId());
                        startActivity(i);
                    }
                });
                ratingBar = (RatingBar)itemView.findViewById(R.id.movieUserRating);
                movieTitle = (TextView)itemView.findViewById(R.id.movieTitleLikedList);
                imageView = (ImageView)itemView.findViewById(R.id.moviePosterLikedList);
            }
        }
    }


}