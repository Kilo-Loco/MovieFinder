package com.example.moviefinder.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moviefinder.data.Movie
import com.example.moviefinder.data.VideoStreamPlatform
import com.example.moviefinder.data.MoviesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MovieViewModelState(
    val moviesList: List<Movie> = emptyList(),
    val selectedMovie: Movie? = null,
    val streamProviders: List<VideoStreamPlatform> = emptyList(),
)

class MovieViewModel(
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    private val viewModelState = MutableStateFlow(MovieViewModelState())

    init {
        getTopRatedMovies()
    }

    // placeholder
    val isLoggedIn = MutableStateFlow(false)

    val uiState = viewModelState.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        viewModelState.value
    )

    private fun getTopRatedMovies() {
        viewModelScope.launch {
            viewModelState.update { it.copy(moviesList = moviesRepository.getTopRatedMovies().movies) }
        }
    }

    fun getStreamingProviders() {
        viewModelScope.launch {
            viewModelState.value.selectedMovie?.let { it ->
                val streamingPlatforms = moviesRepository.getStreamingPlatforms(it.id)
                if (streamingPlatforms != null) {
                    viewModelState.update { it.copy(streamProviders = streamingPlatforms) }
                }
            }
        }
    }

    fun selectMovie(movie: Movie) {
        viewModelScope.launch {
            viewModelState.update { it.copy(selectedMovie = movie) }
        }
    }

    companion object {
        fun provideFactory(
            moviesRepository: MoviesRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MovieViewModel(moviesRepository) as T
            }
        }
    }
}