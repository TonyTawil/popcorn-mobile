package com.example.popcorn.Networking;

import com.example.popcorn.DTOs.CreditsResponse;
import com.example.popcorn.DTOs.LoginUser;
import com.example.popcorn.DTOs.MovieResponse;
import com.example.popcorn.DTOs.MoviesResponse;
import com.example.popcorn.DTOs.ReviewRequest;
import com.example.popcorn.DTOs.ReviewResponse;
import com.example.popcorn.DTOs.TrailerResponse;
import com.example.popcorn.DTOs.UserResponse;
import com.example.popcorn.DTOs.VerificationResponse;
import com.example.popcorn.DTOs.WatchlistAddRequest;
import com.example.popcorn.DTOs.WatchlistAddResponse;
import com.example.popcorn.DTOs.WatchlistRemoveRequest;
import com.example.popcorn.DTOs.WatchlistRequest;
import com.example.popcorn.DTOs.WatchlistResponse;
import com.example.popcorn.DTOs.WatchedAddRequest;
import com.example.popcorn.DTOs.WatchedAddResponse;
import com.example.popcorn.DTOs.WatchedRemoveRequest;
import com.example.popcorn.DTOs.WatchedRequest;
import com.example.popcorn.DTOs.WatchedResponse;
import com.example.popcorn.DTOs.MovieDetailsResponse;
import com.example.popcorn.Models.Movie;
import com.example.popcorn.Models.Review;
import com.example.popcorn.Models.User;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("api/auth/signup")
    Call<UserResponse> createUser(@Body User user);

    @POST("api/auth/login")
    Call<UserResponse> loginUser(@Body LoginUser loginUser);

    @GET("api/auth/is-verified/{userId}")
    Call<VerificationResponse> checkEmailVerified(@Path("userId") String userId);

    @POST("api/mobile/watchlist/get")
    Call<WatchlistResponse> fetchWatchlist(@Body WatchlistRequest request);

    @POST("api/mobile/watchlist/add")
    Call<WatchlistAddResponse> addToWatchlist(@Body WatchlistAddRequest request);

    @POST("api/mobile/watchlist/remove")
    Call<ResponseBody> removeFromWatchlist(@Body WatchlistRemoveRequest request);

    @POST("api/mobile/watched/get")
    Call<WatchedResponse> fetchWatched(@Body WatchedRequest request);

    @POST("api/mobile/watched/add")
    Call<WatchedAddResponse> addToWatched(@Body WatchedAddRequest request);

    @POST("api/mobile/watched/remove")
    Call<ResponseBody> removeFromWatched(@Body WatchedRemoveRequest request);

    @POST("api/mobile/reviews/add")
    Call<ReviewResponse> addReview(@Body ReviewRequest reviewRequest);

    @GET("api/mobile/reviews/{movieId}")
    Call<List<Review>> getReviewsByMovieId(@Path("movieId") int movieId);

    @HTTP(method = "DELETE", path = "api/mobile/reviews/delete/{reviewId}", hasBody = true)
    Call<ResponseBody> deleteReview(@Path("reviewId") String reviewId, @Body JsonObject userId);

    @PUT("api/mobile/reviews/update/{reviewId}")
    Call<ReviewResponse> updateReview(@Path("reviewId") String reviewId, @Body ReviewRequest reviewRequest);

    @GET("api/mobile/movies/{movieId}/details")
    Call<MovieResponse> getMovieDetails(@Path("movieId") int movieId);

    @GET("api/mobile/movies/{movieId}/credits")
    Call<CreditsResponse> fetchMovieCredits(@Path("movieId") int movieId);

    @GET("api/mobile/movies/{movieId}/similar")
    Call<MoviesResponse> getSimilarMovies(@Path("movieId") int movieId);

    @GET("api/mobile/movies/type/{type}")
    Call<MoviesResponse> getMoviesByType(@Path("type") String type, @Query("page") int page);

    @GET("api/mobile/movies/search")
    Call<MoviesResponse> searchMovies(@Query("query") String query);

    @GET("api/mobile/movies/{movieId}/videos")
    Call<TrailerResponse> fetchMovieTrailers(@Path("movieId") int movieId);

    @POST("api/mobile/auth/logout")
    Call<Void> logoutUser();

    @GET("api/auth/user/email/{email}")
    Call<UserResponse> getUserByEmail(@Path("email") String email);

    @POST("api/mobile/movies/credits/batch")
    Call<Map<String, CreditsResponse>> fetchMovieCreditsInBatch(@Body List<Integer> movieIds);

    @GET("api/mobile/movies/details")
    Call<List<MovieDetailsResponse>> fetchMoviesDetails(@Query("ids") String movieIds);

}
