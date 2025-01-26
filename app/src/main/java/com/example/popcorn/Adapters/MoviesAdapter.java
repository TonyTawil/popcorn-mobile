package com.example.popcorn.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.popcorn.Activities.MovieDetailsActivity;
import com.example.popcorn.Models.Movie;
import com.example.popcorn.Networking.FetchWatchlistTask;
import com.example.popcorn.Networking.FetchWatchedTask;
import com.example.popcorn.R;

import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {
    private Context context;
    private List<Movie> movieList;
    private boolean showDeleteIcon;
    private String listType;
    private FetchWatchlistTask fetchWatchlistTask;
    private FetchWatchedTask fetchWatchedTask;

    public MoviesAdapter(Context context, List<Movie> movieList, boolean showDeleteIcon, String listType) {
        this.context = context;
        this.movieList = movieList;
        this.showDeleteIcon = showDeleteIcon;
        this.listType = listType;
        this.fetchWatchlistTask = new FetchWatchlistTask(null, "", context);
        this.fetchWatchedTask = new FetchWatchedTask(null, "", context);
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.titleTextView.setText(movie.getTitle());
        Glide.with(context).load(movie.getPosterPath()).into(holder.posterImageView);

        String posterPath = movie.getPosterPath();
        if (posterPath != null && !posterPath.startsWith("http")) {
            posterPath = "https://image.tmdb.org/t/p/w500" + posterPath;
        }

        Glide.with(context)
                .load(posterPath)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.movie)
                .into(holder.posterImageView);

        holder.itemView.setOnClickListener(v -> {
            Log.d("Elie Raad","ID: " + movie.getMovieId());
            updateSharedPreferences(movie.getMovieId());
            Intent intent = new Intent(context, MovieDetailsActivity.class);
            intent.putExtra("movieId", movie.getMovieId());
            intent.putExtra("title", movie.getTitle());
            intent.putExtra("posterPath", movie.getPosterPath());
            intent.putExtra("plot", movie.getPlot());

            intent.putParcelableArrayListExtra("cast", movie.getCast() != null ? new ArrayList<>(movie.getCast()) : new ArrayList<>());
            intent.putParcelableArrayListExtra("crew", movie.getCrew() != null ? new ArrayList<>(movie.getCrew()) : new ArrayList<>());

            context.startActivity(intent);
        });

        if (showDeleteIcon) {
            holder.removeIcon.setVisibility(View.VISIBLE);
            holder.removeIcon.setOnClickListener(v -> {
                String userId = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("userId", null);
                if (userId != null) {
                    if ("watchlist".equals(listType)) {
                        fetchWatchlistTask.removeMovieFromWatchlist(userId, movie.getMovieId(), () -> {
                            movieList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, movieList.size());
                        });
                    } else if ("watched".equals(listType)) {
                        fetchWatchedTask.removeMovieFromWatched(userId, movie.getMovieId(), () -> {
                            movieList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, movieList.size());
                        });
                    }
                }
            });
        } else {
            holder.removeIcon.setVisibility(View.GONE);
        }
    }

    private void updateSharedPreferences(int movieId) {
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("movieId", movieId);
        editor.apply();
    }

    @Override
    public int getItemCount() {
        return movieList != null ? movieList.size() : 0;
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView posterImageView;
        ImageView removeIcon;

        public MovieViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            posterImageView = itemView.findViewById(R.id.posterImageView);
            removeIcon = itemView.findViewById(R.id.remove_icon);
        }
    }
}