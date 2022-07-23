package com.example.moviefinder.di

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.example.moviefinder.ApiService
import com.example.moviefinder.MovieFinderPurchasingListener
import com.example.moviefinder.data.MoviesRepository


interface AppContainer {
    val apiService: ApiService
    val moviesRepository: MoviesRepository
    val movieFinderPurchasingListener: MovieFinderPurchasingListener
}


class AppContainerImpl(private val applicationContext: Context) : AppContainer {

    private fun getApiKey(): String {
        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        return ai.metaData["tmdbApiKey"].toString()
    }
    override val apiService: ApiService by lazy {
        ApiService.create(getApiKey())
    }

    override val moviesRepository: MoviesRepository by lazy {
        MoviesRepository(apiService)
    }

    override val movieFinderPurchasingListener: MovieFinderPurchasingListener by lazy {
        MovieFinderPurchasingListener()
    }

}
