package com.example.moviefinder.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import com.example.moviefinder.MovieFinderApplication

@ExperimentalFoundationApi
class MainActivity : ComponentActivity() {

    private val appContainer by lazy { (application as MovieFinderApplication).container }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MovieFinderApp(appContainer)
        }
    }
}