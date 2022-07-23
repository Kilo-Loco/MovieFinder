package com.example.moviefinder

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify

@ExperimentalFoundationApi
class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MovieViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiKey = getApiKey()
        viewModel = MovieViewModel(apiKey)

        configureAmplify()
        viewModel.checkAuthStatus()
        viewModel.registerPurchasingListener(this)

        setContent {
            MaterialTheme {
                AppNavigator()
            }
        }
    }

    private fun getApiKey(): String {
        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        return ai.metaData["tmdbApiKey"].toString()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAppStoreData()
    }

    private fun configureAmplify() {
        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.configure(applicationContext)
            Log.i("KILO", "Configured amplify")
        } catch (e: Exception) {
            Log.e("KILO", "Amplify configuration failed", e)
        }
    }

    @Composable
    private fun AppNavigator() {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "topMovies") {
            composable("topMovies") {
                MoviesView(navController = navController, vm = viewModel)
            }
            composable("movieDetails") { MovieDetails(vm = viewModel) }
        }
    }
}