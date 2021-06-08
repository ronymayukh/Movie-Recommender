package com.example.movierecommender;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class MoviePredicted {
    ArrayList<Movie> movieList;
    Integer position;
    Boolean updateCalled;

    MoviePredicted(){
        position = 0;
        updateCalled = false;
    }


    public synchronized Movie getMovie(){
        Movie reqMovie = movieList.get(position);
        position++;
        if((position+2)>=movieList.size()){
            Log.d("xD","Taking it on me !");
            FetchInitialMovies fetchInitialMovies = new FetchInitialMovies();
            fetchInitialMovies.execute(USER_ID.userID);
        }
        Log.d("INFOX","MOVIES TOTAL "+movieList.size()+", MOVIES USED "+position);
        return reqMovie;
    }

    public synchronized void setMovie(ArrayList<Movie> movie){
        movieList = movie;
        Log.d("xD","Movies updated "+movieList.size());
        position = 0;
    }

    class FetchInitialMovies extends AsyncTask<String,Void,ArrayList<Movie>> {



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

            setMovie(movies);


        }
    }

}
