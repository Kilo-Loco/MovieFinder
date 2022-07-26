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

    private val appContainer by lazy { (application as MovieFinderApplication).container }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PurchasingService.registerListener(this, appContainer.movieFinderPurchasingListener)

        appContainer.amplifyHandlerService.configureAmplify()
        appContainer.amplifyHandlerService.checkAuthStatus()

        setContent {
            MovieFinderApp(appContainer)
        }
    }

    override fun onResume() {
        super.onResume()
        appContainer.iapHandler.getAppStoreData()
    }
}