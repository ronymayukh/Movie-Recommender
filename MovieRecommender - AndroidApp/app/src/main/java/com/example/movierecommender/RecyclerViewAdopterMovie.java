package com.example.movierecommender;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.ls.LSOutput;

import java.util.ArrayList;

public class RecyclerViewAdopterMovie extends RecyclerView.Adapter<RecyclerViewAdopterMovie.MovieViewHolder> {

    Context context;
    ArrayList<Movie> movieArrayList;
    ArrayList<Float> movieRatings;


    public ArrayList<Float> getRatings(){
        return movieRatings;
    }


    RecyclerViewAdopterMovie(Context context, ArrayList<Movie> movieArrayList){
        this.context = context;
        this.movieArrayList = movieArrayList;
        this.movieRatings = new ArrayList<>();

        for (int i=0;i<movieArrayList.size();i++){
            movieRatings.add(0f);
        }

    }


    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_view_movies,parent,false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {

       Picasso.get().load("https://image.tmdb.org/t/p/w200/"+movieArrayList.get(position).getPosterUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.moviePosterCV);
        holder.tvTitleCV.setText(movieArrayList.get(position).getMovieTitle());

        holder.movieRatingCV.setRating(movieRatings.get(position));

    }

    @Override
    public int getItemCount() {
        return movieArrayList.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitleCV;
        ImageView moviePosterCV;
        RatingBar movieRatingCV;
        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvTitleCV = (TextView) itemView.findViewById(R.id.tvTitleCV);
            this.moviePosterCV = (ImageView) itemView.findViewById(R.id.moviePosterCV);
            this.movieRatingCV = itemView.findViewById(R.id.movieRatingCV);

            movieRatingCV.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    movieRatings.set(getAdapterPosition(),ratingBar.getRating());
                }
            });
        }
    }
}
