package com.example.moviefinder

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazon.device.iap.PurchasingService
import com.amplifyframework.core.Amplify
import kotlinx.coroutines.launch
import java.lang.Exception

class MovieViewModel : ViewModel() {
    private val purchasingListener = MovieFinderPurchasingListener()

    private val _isSignedIn = mutableStateOf(false)
    val isSignedIn: Boolean
        get() = _isSignedIn.value

    private val _moviesList = mutableStateListOf<Movie>()
    val moviesList: List<Movie>
        get() = _moviesList

    private val _selectedMovie = mutableStateOf<Movie?>(null)
    val selectedMovie: Movie?
        get() = _selectedMovie.value

    private val _streamProviders = mutableStateListOf<VideoStreamPlatform>()
    val streamProviders: List<VideoStreamPlatform>
        get() = _streamProviders

    private val _hasPurchasedStreamingInfo = mutableStateOf(false)
    val hasPurchasedStreamingInfo: Boolean
        get() = _hasPurchasedStreamingInfo.value

    fun login(activity: Activity) {
        Amplify.Auth.signInWithWebUI(activity,
            {
                Log.i("KILO", "Signin OK = $it")
                _isSignedIn.value = true
            },
            { Log.e("KILO", "Signin failed", it) }
        )
    }

    fun signOut() {
        Amplify.Auth.signOut(
            {
                Log.i("KILO", "Sign out successful")
                _isSignedIn.value = false
            },
            { Log.e("KILO", "Sign out error", it) }
        )
    }

    fun checkAuthStatus() {
        Amplify.Auth.fetchAuthSession(
            {
                Log.i("KILO", "Auth session ${it.isSignedIn}")
                _isSignedIn.value = it.isSignedIn
            },
            { Log.e("KILO", "Failed to get auth session", it) }
        )
    }

    fun getTopRatedMovies() {
        viewModelScope.launch {
            val apiService = ApiService.getInstance()
            try {
                _moviesList.clear()
                _moviesList.addAll(apiService.topRatedMovies().movies)
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

                val streamingPlatforms = apiService.streamingPlatforms(selectedMovie!!.id)
                    .results.us?.streamingPlatforms

                if (streamingPlatforms != null) {
                    _streamProviders.addAll(streamingPlatforms)
                }

            } catch (e: Exception) {
                Log.e("kilo", "Error", e)
            }
        }
    }

    fun selectMovie(movie: Movie) {
        _selectedMovie.value = movie
    }

    fun allowStreamingInfoAccess() {
        _hasPurchasedStreamingInfo.value = true
    }

    fun removeStreamingInfoAccess() {
        _hasPurchasedStreamingInfo.value = false
    }

    fun registerPurchasingListener(activity: Activity) {
        PurchasingService.registerListener(activity, purchasingListener)
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

        PurchasingService.purchase(parentSKU)
    }
}