package com.example.moviefinder.ui

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.amazon.device.iap.PurchasingService
import com.amplifyframework.core.Amplify
import com.example.moviefinder.Movie
import com.example.moviefinder.MovieFinderPurchasingListener
import com.example.moviefinder.VideoStreamPlatform
import com.example.moviefinder.data.MoviesRepository
import com.example.moviefinder.parentSKU
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MovieViewModelState(
    val isSignedIn: Boolean = false,
    val moviesList: List<Movie> = emptyList(),
    val selectedMovie: Movie? = null,
    val streamProviders: List<VideoStreamPlatform> = emptyList(),
    val hasPurchasedStreamingInfo: Boolean = false
)

class MovieViewModel(
    private val moviesRepository: MoviesRepository,
    private val purchasingListener: MovieFinderPurchasingListener
) : ViewModel() {

    private val viewModelState = MutableStateFlow(MovieViewModelState())
    val uiState = viewModelState.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        viewModelState.value
    )

    fun login(activity: Activity) {
        Amplify.Auth.signInWithWebUI(activity,
            {
                Log.i("KILO", "Signin OK = $it")
                viewModelState.update { it.copy(isSignedIn = true) }
            },
            { Log.e("KILO", "Signin failed", it) }
        )
    }

    fun signOut() {
        Amplify.Auth.signOut(
            {
                Log.i("KILO", "Sign out successful")
                viewModelState.update { it.copy(isSignedIn = false) }
            },
            { Log.e("KILO", "Sign out error", it) }
        )
    }

    fun checkAuthStatus() {
        Amplify.Auth.fetchAuthSession(
            {
                Log.i("KILO", "Auth session ${it.isSignedIn}")
                viewModelState.update { it.copy(isSignedIn = true) }
            },
            { Log.e("KILO", "Failed to get auth session", it) }
        )
    }

    fun getTopRatedMovies() {
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

    fun allowStreamingInfoAccess() {
        viewModelScope.launch {
            viewModelState.update { it.copy(hasPurchasedStreamingInfo = true) }
        }
    }

    fun removeStreamingInfoAccess() {
        viewModelScope.launch {
            viewModelState.update { it.copy(hasPurchasedStreamingInfo = false) }
        }
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
        purchasingListener.onPurchase = {
            allowStreamingInfoAccess()
        }

        //TODO: remove after test IAP
        allowStreamingInfoAccess()

        PurchasingService.purchase(parentSKU)
    }

    companion object {
        fun provideFactory(
            moviesRepository: MoviesRepository,
            purchasingListener: MovieFinderPurchasingListener
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MovieViewModel(moviesRepository, purchasingListener) as T
            }
        }
    }
}