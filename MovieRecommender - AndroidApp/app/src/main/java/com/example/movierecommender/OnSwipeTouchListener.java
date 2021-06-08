package com.example.movierecommender;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class OnSwipeTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;
    MoviePredicted moviePredicted;
    Movie movie;
    ImageView imageView;
    Context context;


    public OnSwipeTouchListener (Context ctx, MoviePredicted moviePredicted, ImageView imageView){
        gestureDetector = new GestureDetector(ctx, new GestureListener());
        this.context = ctx;
        this.moviePredicted = moviePredicted;
        this.movie = moviePredicted.getMovie();
        this.imageView = imageView;
        setMovieOnScreen(movie);

    }

    private void setMovieOnScreen(Movie movie_to_show){
        Picasso.get().load("https://image.tmdb.org/t/p/w200/"+movie_to_show.getPosterUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(imageView);

    }

    public Movie getCurrentMovie(){
        return movie;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        public void onLongPress(MotionEvent e) {

            RateMovie rateMovie = new RateMovie();
            rateMovie.execute(movie.getMovieId(),"3");

            movie = moviePredicted.getMovie();
            setMovieOnScreen(movie);

        }


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                        result = true;
                    }
                }
                else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                    result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void onSwipeRight() {

        RateMovie rateMovie = new RateMovie();
        rateMovie.execute(movie.getMovieId(),"4");
        System.out.println(USER_ID.userID);
        movie = moviePredicted.getMovie();
        setMovieOnScreen(movie);
    }

    public void onSwipeLeft() {
        RateMovie rateMovie = new RateMovie();
        rateMovie.execute(movie.getMovieId(),"2");

        movie = moviePredicted.getMovie();
        setMovieOnScreen(movie);
    }

    public void onSwipeTop() {
        RateMovie rateMovie = new RateMovie();
        rateMovie.execute(movie.getMovieId(),"5");


        if(!moviePredicted.updateCalled){
            moviePredicted.updateCalled = true;
            FetchMovieUserBased asyncTask = new FetchMovieUserBased();
            asyncTask.execute(USER_ID.userID,movie.getMovieTitle());
        }

        movie = moviePredicted.getMovie();
        setMovieOnScreen(movie);
    }

    public void onSwipeBottom() {
        RateMovie rateMovie = new RateMovie();
        rateMovie.execute(movie.getMovieId(),"1");

        movie = moviePredicted.getMovie();
        setMovieOnScreen(movie);
    }



    class FetchMovieUserBased extends AsyncTask<String,Void,ArrayList<Movie>>{


        @Override
        protected ArrayList<Movie> doInBackground(String... strings) {

            ArrayList<Movie> moviesList = new ArrayList<Movie>();

            try {
                ArrayList<String> movieIds = APICalls.userSpecificPrediction(strings[0],strings[1]);
                //ArrayList<String> movieIds = APICalls.rand10Movies(strings[0]);

                for(int i=0;i<movieIds.size();i++){
                    moviesList.add(APICalls.getMovieFromTMDB(movieIds.get(i)));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return moviesList;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            super.onPostExecute(movies);
            Log.d("INFOX",movies.size()+"");
            moviePredicted.setMovie(movies);
            moviePredicted.updateCalled = false;
        }
    }


    class RateMovie extends AsyncTask<String,Void,Boolean>{

        @Override
        protected Boolean doInBackground(String... strings) {

            try {
                return APICalls.movieRating(USER_ID.userID,strings[0],strings[1]);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if(!success){
                Toast.makeText(context,"SOMETHING WENT WRONG",Toast.LENGTH_SHORT).show();
            }
        }
    }






}
