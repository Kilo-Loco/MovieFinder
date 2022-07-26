package com.example.moviefinder.di

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.example.moviefinder.amplify.AmplifyHandler
import com.example.moviefinder.amplify.AmplifyService
import com.example.moviefinder.data.MoviesRepository
import com.example.moviefinder.data.api.ApiService
import com.example.moviefinder.iap.IAPHandler
import com.example.moviefinder.iap.IAPService
import com.example.moviefinder.iap.MovieFinderPurchasingListener


interface AppContainer {
    val apiService: ApiService
    val moviesRepository: MoviesRepository
    val movieFinderPurchasingListener: MovieFinderPurchasingListener
    val amplifyHandlerService: AmplifyService
    val iapHandler: IAPService
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
        MovieFinderPurchasingListener(moviesRepository)
    }

    override val amplifyHandlerService: AmplifyService by lazy {
        AmplifyHandler(applicationContext, moviesRepository)
    }

    override val iapHandler: IAPService by lazy {
        IAPHandler()
    }

}
