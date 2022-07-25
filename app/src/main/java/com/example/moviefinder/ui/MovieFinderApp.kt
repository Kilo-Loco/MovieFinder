package com.example.moviefinder.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moviefinder.di.AppContainer
import com.example.moviefinder.ui.theme.MovieFinderTheme

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AppNavigator(appContainer: AppContainer) {
    val navController = rememberNavController()

    val viewModel: MovieViewModel = viewModel(
        factory = MovieViewModel.provideFactory(
            appContainer.moviesRepository
        )
    )
    NavHost(navController = navController, startDestination = "topMovies") {
        composable("topMovies") {
            val currentActivity = LocalContext.current.findActivity()
            if (currentActivity != null) {
                MoviesView(
                    navController = navController,
                    vm = viewModel,
                    onLogin = { appContainer.amplifyHandlerService.login(currentActivity) },
                    onLogout = { appContainer.amplifyHandlerService.logout() }
                )
            }
        }
        composable("movieDetails") {
            MovieDetails(vm = viewModel)
        }
    }
}

@Composable
fun MovieFinderApp(appContainer: AppContainer) {
    MovieFinderTheme {
        AppNavigator(appContainer)
    }
}