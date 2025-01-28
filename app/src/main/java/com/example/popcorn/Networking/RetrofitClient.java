package com.example.popcorn.Networking;

import android.content.Context;
import android.util.Log;
import java.io.File;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.UUID;

public class RetrofitClient {

    private static Retrofit retrofit = null;

    // Use this for production
    private static final String PRODUCTION_URL = "https://popcorn.antoinetawil.com/";
    // Use this for local development
    private static final String LOCAL_URL = "http://10.0.2.2:3000/";

    // Change this flag to switch between production and local development
    private static final boolean USE_PRODUCTION = true;

    // Increased cache size to 50MB
    private static final int CACHE_SIZE = 50 * 1024 * 1024;

    public static Retrofit getRetrofitInstance(Context context) {
        if (retrofit == null) {
            // Create cache
            File httpCacheDirectory = new File(context.getCacheDir(), "responses");
            Cache cache = new Cache(httpCacheDirectory, CACHE_SIZE);

            // Add logging interceptor
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

            // Configure OkHttpClient
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)  // Increased for batch requests
                .writeTimeout(15, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(chain -> {
                    long startTime = System.currentTimeMillis();
                    
                    // Add request ID for tracking
                    String requestId = UUID.randomUUID().toString().substring(0, 8);
                    Log.d("NetworkTiming", requestId + " Starting request to: " + chain.request().url());
                    
                    okhttp3.Response response = chain.proceed(chain.request().newBuilder()
                        .header("Cache-Control", "public, max-age=300")  // Cache for 5 minutes
                        .build());
                    
                    long endTime = System.currentTimeMillis();
                    Log.d("NetworkTiming", requestId + " Request completed in " + (endTime - startTime) + "ms");
                    Log.d("NetworkTiming", requestId + " Response code: " + response.code());
                    
                    return response;
                })
                .addInterceptor(logging);

            retrofit = new Retrofit.Builder()
                .baseUrl(USE_PRODUCTION ? PRODUCTION_URL : LOCAL_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        }
        return retrofit;
    }
}
