package com.example.popcorn.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.popcorn.Activities.EditReviewActivity;
import com.example.popcorn.Models.Review;
import com.example.popcorn.Networking.ApiService;
import com.example.popcorn.Networking.RetrofitClient;
import com.example.popcorn.R;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {
    private List<Review> reviewsList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private String currentUserId;
    private Context context;

    public ReviewsAdapter(List<Review> reviewsList, Context context) {
        this.reviewsList = reviewsList;
        this.context = context;
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        this.currentUserId = prefs.getString("userId", "");
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewsList.get(position);
        if (review.getUserId() != null) {
            holder.tvReviewerName.setText(review.getUserId().getUsername());
        } else {
            holder.tvReviewerName.setText("Anonymous");
        }

        holder.ratingBarSmall.setRating((float)review.getRating());
        holder.tvReviewText.setText(review.getReviewText());
        holder.tvReviewDate.setText(review.getCreatedAt() != null ? dateFormat.format(review.getCreatedAt()) : "No date");

        if (review.getUserId() != null && review.getUserId().getId().equals(currentUserId)) {
            holder.ivEdit.setVisibility(View.VISIBLE);
            holder.ivDelete.setVisibility(View.VISIBLE);

            holder.ivEdit.setOnClickListener(v -> {
                Intent intent = new Intent(context, EditReviewActivity.class);
                intent.putExtra("reviewId", review.getId());
                intent.putExtra("reviewText", review.getReviewText());
                intent.putExtra("rating", review.getRating());
                context.startActivity(intent);
            });

            holder.ivDelete.setOnClickListener(v -> {
                deleteReview(position);
            });
        } else {
            holder.ivEdit.setVisibility(View.GONE);
            holder.ivDelete.setVisibility(View.GONE);
        }
    }

    private void deleteReview(int position) {
        String reviewId = reviewsList.get(position).getId();
        JsonObject userIdJson = new JsonObject();
        userIdJson.addProperty("userId", currentUserId);

        ApiService apiService = RetrofitClient.getRetrofitInstance(context).create(ApiService.class);
        Call<ResponseBody> call = apiService.deleteReview(reviewId, userIdJson);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Review deleted successfully.", Toast.LENGTH_SHORT).show();
                    reviewsList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());
                } else {
                    Toast.makeText(context, "Failed to delete review: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Error deleting review: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvReviewerName, tvReviewText, tvReviewDate;
        RatingBar ratingBarSmall;
        ImageView ivEdit, ivDelete;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReviewerName = itemView.findViewById(R.id.tvReviewerName);
            ratingBarSmall = itemView.findViewById(R.id.ratingBarSmall);
            tvReviewText = itemView.findViewById(R.id.tvReviewText);
            tvReviewDate = itemView.findViewById(R.id.tvReviewDate);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}
