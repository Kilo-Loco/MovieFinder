package com.example.moviefinder.data

import com.example.moviefinder.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MoviesRepository(private val apiService: ApiService) {

    private val _isPurchasedStreamingInfo = MutableStateFlow(false) //An initial value is required
    val isPurchasedStreamingInfo: StateFlow<Boolean>
        get() = _isPurchasedStreamingInfo

    suspend fun getTopRatedMovies(): TopRatedMoviesResponse {
        return apiService.topRatedMovies()
    }

    suspend fun getStreamingPlatforms(movieId: String): List<VideoStreamPlatform>? {
        return apiService.streamingPlatforms(movieId).results.us?.streamingPlatforms
    }

    fun setPurchaseStreamingInfo(isPurchased: Boolean) {
        _isPurchasedStreamingInfo.value = isPurchased
    }
}