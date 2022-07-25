package com.example.moviefinder.data.api

import com.example.moviefinder.data.TopRatedMoviesResponse
import com.example.moviefinder.data.VideoProviderResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("movie/top_rated")
    suspend fun topRatedMovies(): TopRatedMoviesResponse

    @GET("movie/{movieId}/watch/providers")
    suspend fun streamingPlatforms(@Path("movieId") movieId: String): VideoProviderResponse

    companion object {
        private const val BASE_URL = "https://api.themoviedb.org/3/"

        fun create(API_KEY: String): ApiService {

            val builder = OkHttpClient.Builder().addInterceptor { chain ->
                val request = chain.request().newBuilder()
                val originalHttpUrl = chain.request().url()
                val url = originalHttpUrl.newBuilder()
                    .addQueryParameter("api_key", API_KEY).build()
                request.header("Content-Type", "application/json;charset=utf-8");
                request.url(url)
                chain.proceed(request.build())
            }
            val client = builder.build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(ApiService::class.java)

        }
    }
}

