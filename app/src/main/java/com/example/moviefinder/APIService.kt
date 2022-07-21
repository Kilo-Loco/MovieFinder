package com.example.moviefinder

import android.app.Activity
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amplifyframework.core.Amplify
import com.example.moviefinder.ApiService.Companion.apiService
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
import java.lang.Exception

interface ApiService {
    @Headers("Content-Type: application/json;charset=utf-8")
    @GET("movie/top_rated?api_key=$tmdbApiKey")
    suspend fun topRatedMovies(): TopRatedMoviesResponse

    @Headers("Content-Type: application/json;charset=utf-8")
    @GET("movie/{movieId}/watch/providers?api_key=$tmdbApiKey")
    suspend fun streamingPlatforms(
        @Path("movieId")
        movieId: String
    ): VideoProviderResponse

    companion object {
        var apiService: ApiService? = null
        fun getInstance(): ApiService {
            if (apiService == null) {
                apiService = Retrofit.Builder()
                    .baseUrl("https://api.themoviedb.org/3/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService::class.java)
            }
            return apiService!!
        }
    }
}

