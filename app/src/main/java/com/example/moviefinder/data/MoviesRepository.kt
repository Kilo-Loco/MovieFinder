package com.example.moviefinder.data

import com.example.moviefinder.*

class MoviesRepository(private val apiService: ApiService) {

    suspend fun getTopRatedMovies(): TopRatedMoviesResponse {
        return apiService.topRatedMovies()
    }

    suspend fun getStreamingPlatforms(movieId: String): List<VideoStreamPlatform>? {
        return apiService.streamingPlatforms(movieId).results.us?.streamingPlatforms
    }
}