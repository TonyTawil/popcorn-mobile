package com.example.popcorn.Networking;

import android.content.Context;
import java.io.File;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit = null;

    // Use this for production
    private static final String PRODUCTION_URL = "https://popcorn.antoinetawil.com/";
    // Use this for local development
    private static final String LOCAL_URL = "http://10.0.2.2:3000/";

    // Change this flag to switch between production and local development
    private static final boolean USE_PRODUCTION = true;

    public static Retrofit getRetrofitInstance(Context context) {
        if (retrofit == null) {
            File httpCacheDirectory = new File(context.getCacheDir(), "responses");
            int cacheSize = 10 * 1024 * 1024;
            Cache cache = new Cache(httpCacheDirectory, cacheSize);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.cache(cache);

            retrofit = new Retrofit.Builder()
                    .baseUrl(USE_PRODUCTION ? PRODUCTION_URL : LOCAL_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }
}
