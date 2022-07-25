package com.example.moviefinder.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.amazon.device.iap.PurchasingService
import com.example.moviefinder.Movie
import com.example.moviefinder.VideoStreamPlatform
import com.example.moviefinder.data.MoviesRepository
import com.example.moviefinder.iap.parentSKU
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MovieViewModelState(
    val isSignedIn: Boolean = false,
    val moviesList: List<Movie> = emptyList(),
    val selectedMovie: Movie? = null,
    val streamProviders: List<VideoStreamPlatform> = emptyList(),
    val hasPurchasedStreamingInfo: Boolean = false
)

class MovieViewModel(
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    private val viewModelState = MutableStateFlow(MovieViewModelState())

    init {

        getTopRatedMovies()
        //getAppStoreData()
    }

    val uiState = combine(
        moviesRepository.isLoggedIn,
        moviesRepository.isPurchasedStreamingInfo,
        viewModelState
    ) { isLoggedIn, isPurchasedStreamingInfo, viewModelState ->
        MovieViewModelState(
            isSignedIn = isLoggedIn,
            moviesList = viewModelState.moviesList,
            selectedMovie = viewModelState.selectedMovie,
            streamProviders = viewModelState.streamProviders,
            hasPurchasedStreamingInfo = isPurchasedStreamingInfo
        )
    }.stateIn(
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

    fun removeStreamingInfoAccess() {
        moviesRepository.setPurchaseStreamingInfo(false)
    }

    fun getAppStoreData() {
        //getUserData() will query the Appstore for the Users information
        PurchasingService.getUserData()
        //getPurchaseUpdates() will query the Appstore for any previous purchase
        PurchasingService.getPurchaseUpdates(true)
        //getProductData will validate the SKUs with Amazon Appstore
        val productSkus: MutableSet<String> = HashSet()
        productSkus.add(parentSKU)
        PurchasingService.getProductData(productSkus)
        Log.i("KILO", "Validating SKUs with Amazon")
    }

    fun purchaseStreamingInfo() {
        PurchasingService.purchase(parentSKU)
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