package com.example.movierecommender;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.Console;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class APICalls {

    public static Movie getMovieFromTMDB(String movieId) throws Exception {

        URL url = new URL("https://api.themoviedb.org/3/movie/" + movieId + "?api_key=1fcffe26c42c02fdc74eefcc87c30217");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
        Scanner sc = new Scanner(in);
        String responseBody = "";
        while (sc.hasNext()) {
            responseBody += sc.nextLine();
            responseBody += "\n";
        }

        JSONObject jsonObject = new JSONObject(responseBody);
        String movieTitle = jsonObject.getString("title");
        float imdbRating = (float) jsonObject.getDouble("vote_average");
        long voteCount = jsonObject.getLong("vote_count");
        float moviePopularity = (float) jsonObject.getDouble("popularity");
        String posterPath = jsonObject.getString("poster_path");
        String movieOverview = jsonObject.getString("overview");


        JSONArray jsonArrayGenre = jsonObject.getJSONArray("genres");
        ArrayList<String> genre = new ArrayList<String>();

        for(int i=0;i<jsonArrayGenre.length();i++){
            JSONObject genreJsonObject = new JSONObject(jsonArrayGenre.get(i).toString());
            genre.add(genreJsonObject.getString("name"));
        }



        Movie movie = new Movie(movieId, movieTitle, posterPath, genre, imdbRating, moviePopularity, voteCount, movieOverview);

        Log.d("INFOX","FETCHED MOVIE FROM TMDB"+movie.getMovieTitle());
        return movie;
    }

    public static void userLikedMovies(ArrayList<Movie> movies, ArrayList<Float> ratings, String userId) throws Exception{

        String str = USER_ID.localIP+"/getUserLikedMovies/"+userId;
        URL url = new URL(str);

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
        Scanner sc = new Scanner(in);

        String responseBody = "";
        while (sc.hasNext()) {
            responseBody += sc.nextLine();
            responseBody += "\n";
        }

        JSONArray jsonArray = new JSONArray(responseBody);

        for(int i=0;i< jsonArray.length();i++){
            JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
            String movieId = jsonObject.getString("movieId");
            String movieRating = jsonObject.getString("userRating");
            Log.d("INFOX","FETCHED MOVIE FROM USER LIKED "+movieId+" RATED "+movieRating);
            ratings.add(Float.valueOf(movieRating));
            movies.add(getMovieFromTMDB(movieId));
        }
    }

    public static Boolean movieRating(String userId, String movieId, String rating) throws Exception{
        String str = USER_ID.localIP+"/movieRating/"+ movieId +"/"+userId+"/"+ rating;
        URL url = new URL(str);

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED){
            Log.d("INFOX","RATED MOVIE"+movieId+" "+rating);
            return true;
        }
        Log.d("INFOX","FAILED TO RATE MOVIE"+movieId+" "+rating);
        return false;
    }

    public static Integer userLogin(String userEmail, String password) throws  Exception{
        String str = USER_ID.localIP+"/user/login/"+ userEmail +"/"+ password;
        URL url = new URL(str);

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED){
            BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
            Scanner sc = new Scanner(in);
            String s = sc.nextLine();
            return Integer.valueOf(s);
        }else{
            if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST){
                return -1;
            }else{
                return -2;
            }

        }
    }

    public static Integer userRegistration(String userEmail, String password, String userName) throws  Exception{

        String str = USER_ID.localIP+"/user/register/"+userName+"/"+userEmail+"/"+password;
        URL url = new URL(str);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED){
            return 1;
        }else if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST){
            return 0;
        }

        return -1;
    }

    public static ArrayList<String> top10Movies(String genre) throws Exception{
        String str = USER_ID.localIP+"/top10/"+genre;
        URL url = new URL(str);

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
        Scanner sc = new Scanner(in);
        String movieListString = sc.nextLine();

         movieListString = movieListString.substring(1,movieListString.length()-1);

         String[] movieArrayString = movieListString.split(", ");

         ArrayList<String> required = new ArrayList<String>(Arrays.asList(movieArrayString));
        Log.d("INFOX","TOP 10 of "+genre+" "+required);
         return required;

    }

    public static ArrayList<String> rand10Movies(String userId) throws Exception{
        String str = USER_ID.localIP+"/getRand/"+userId;
        URL url = new URL(str);

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
        Scanner sc = new Scanner(in);
        String movieListString = sc.nextLine();

        movieListString = movieListString.substring(1,movieListString.length()-1);

        String[] movieArrayString = movieListString.split(",");

        ArrayList<String> required = new ArrayList<String>(Arrays.asList(movieArrayString));
        Log.d("INFOX","RAND 10 "+required);
        return required;

    }


    public static ArrayList<String> userSpecificPrediction(String userID,String movieName) throws Exception{
        String str = USER_ID.localIP+"/userSpecific/"+userID+"/"+movieName;
        Log.d("INFOX","USER SPECIFIC CALLED "+str);
        URL url = new URL(str);

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
        Scanner sc = new Scanner(in);

        if(!sc.hasNext()){
            return rand10Movies(userID);
        }

        String movieListString = sc.nextLine();

        if(movieListString.equals("[]")){
            return rand10Movies(userID);
        }

        movieListString = movieListString.substring(1,movieListString.length()-1);





        String[] movieArrayString = movieListString.split(", ");

        ArrayList<String> required = new ArrayList<String>(Arrays.asList(movieArrayString));
        Log.d("INFOX","USER SPECIFIC "+required);
        return required;
    }


}
