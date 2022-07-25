package com.example.moviefinder

import android.app.Application
import com.example.moviefinder.di.AppContainer
import com.example.moviefinder.di.AppContainerImpl

class MovieFinderApplication : Application() {

    // to obtain dependencies
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}
