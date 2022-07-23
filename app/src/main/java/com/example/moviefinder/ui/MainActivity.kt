package com.example.moviefinder.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import com.amazon.device.iap.PurchasingService
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.example.moviefinder.MovieFinderApplication

@ExperimentalFoundationApi
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (application as MovieFinderApplication).container

        PurchasingService.registerListener(this, appContainer.movieFinderPurchasingListener)

        configureAmplify()
        // viewModel.checkAuthStatus()

        setContent {
            MovieFinderApp(appContainer)
        }
    }

    override fun onResume() {
        super.onResume()
        //  viewModel.getAppStoreData()
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
}