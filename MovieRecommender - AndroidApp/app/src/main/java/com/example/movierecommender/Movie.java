package com.example.movierecommender;

import java.util.ArrayList;

public class Movie {
    String movieId;
    String movieTitle;
    ArrayList<String> genres;
    Float imdbRating;
    Float popularity;
    Long voteCount;
    String overview;
    String posterUrl;

    public Movie(String movieId, String movieTitle, String posterUrl, ArrayList<String> genres, Float imdbRating, Float popularity, Long voteCount, String overview) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.genres = genres;
        this.imdbRating = imdbRating;
        this.popularity = popularity;
        this.voteCount = voteCount;
        this.overview = overview;
        this.posterUrl = posterUrl;
    }

    public String getMovieId() {
        return movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public Float getImdbRating() {
        return imdbRating;
    }

    public Float getPopularity() {
        return popularity;
    }

    public Long getVoteCount() {
        return voteCount;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterUrl(){
        return posterUrl;
    }
}
