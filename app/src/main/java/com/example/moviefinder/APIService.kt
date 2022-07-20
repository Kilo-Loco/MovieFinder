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

data class Movie(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("poster_path")
    val posterPath: String,
    @SerializedName("overview")
    val overview: String
) {
    val fullPostPath: String
        get() = "https://image.tmdb.org/t/p/w500$posterPath"
}

data class TopRatedMoviesResponse(
    @SerializedName("results")
    val movies: List<Movie>
)

data class VideoStreamPlatform(
    @SerializedName("provider_name")
    val name: String
)

data class VideoProviderRegion(
    @SerializedName("flatrate")
    val streamingPlatforms: List<VideoStreamPlatform>
)

data class  VideoProviderResponseResults(
    @SerializedName("US")
    val us: VideoProviderRegion?
)

data class VideoProviderResponse(
    @SerializedName("results")
    val results: VideoProviderResponseResults
)

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

class MovieViewModel : ViewModel() {
    private val _isSignedIn = mutableStateOf(false)
    val isSignedIn: Boolean
        get() = _isSignedIn.value

    private val _moviesList = mutableStateListOf<Movie>()
    val moviesList: List<Movie>
        get() = _moviesList

    var selectedMovie: Movie? = null

    private val _streamProviders = mutableStateListOf<VideoStreamPlatform>()
    val streamProviders: List<VideoStreamPlatform>
        get() = _streamProviders

    private val _hasPurchasedStreamingInfo = mutableStateOf(false)
    val hasPurchasedStreamingInfo: Boolean
        get() = _hasPurchasedStreamingInfo.value

    fun login(activity: Activity) {
        Amplify.Auth.signInWithWebUI(activity,
            { Log.i("AuthQuickStart", "Signin OK = $it") },
            { Log.e("AuthQuickStart", "Signin failed", it) }
        )
    }

    fun checkAuthStatus() {
        Amplify.Auth.fetchAuthSession(
            { Log.i("KILO", "Auth session ${it.isSignedIn}") },
            { Log.e("KILO", "Failed to get auth session", it) }
        )
    }

    fun getTopRatedMovies() {
        viewModelScope.launch {
            val apiService = ApiService.getInstance()
            try {
                _moviesList.clear()
                _moviesList.addAll(apiService.topRatedMovies().movies)
                _moviesList.forEach { Log.i("KILO", it.toString()) }
            } catch (e: Exception) {
                Log.e("kilo", "Error", e)
            }
        }
    }

    fun getStreamingProviders() {
        viewModelScope.launch {
            val apiService = ApiService.getInstance()
            try {
                _streamProviders.clear()

                val streamingPlatforms = apiService
                    .streamingPlatforms(selectedMovie!!.id)
                    .results.us?.streamingPlatforms

                if (streamingPlatforms != null) {
                    _streamProviders.addAll(streamingPlatforms)
                }
                _streamProviders.forEach { Log.i("KILO", it.toString()) }
            } catch (e: Exception) {
                Log.e("kilo", "Error", e)
            }
        }
    }

    fun selectMovie(movie: Movie) {
        selectedMovie = movie
    }
}