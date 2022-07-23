package com.example.moviefinder.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moviefinder.di.AppContainer
import com.example.moviefinder.ui.theme.MovieFinderTheme


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AppNavigator(appContainer: AppContainer) {
    val navController = rememberNavController()

    val viewModel: MovieViewModel = viewModel(
        factory = MovieViewModel.provideFactory(
            appContainer.moviesRepository,
            appContainer.movieFinderPurchasingListener
        )
    )
    NavHost(navController = navController, startDestination = "topMovies") {

        composable("topMovies") {
            MoviesView(navController = navController, vm = viewModel)
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