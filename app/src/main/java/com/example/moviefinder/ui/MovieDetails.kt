package com.example.moviefinder.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moviefinder.data.Movie
import com.example.moviefinder.data.VideoStreamPlatform
import com.example.moviefinder.ui.theme.MovieFinderTheme

@Composable
fun MovieDetails(uiState: MovieViewModelState, onPurchase: () -> Unit) {

    val selectedMovie = uiState.selectedMovie

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Row {
                    Text(selectedMovie?.title ?: "Weird we have a null movie!")
                }
            })
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (selectedMovie != null) {
                Text(text = selectedMovie.overview, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(20.dp))

                if (uiState.streamProviders.isNotEmpty()) {
                    Text(text = "Now you can watch the movie on:", fontWeight = FontWeight.Bold)
                    uiState.streamProviders.forEach {
                        Text(it.name)
                    }
                } else {
                    Text(text = "Not available on streaming", fontWeight = FontWeight.Bold)
                }

            }
        }
    }
}

@Preview("Movies details screen streaming enabled", fontScale = 1.5f, device = Devices.PIXEL_4_XL)
@Composable
fun PreviewMovieDetailsNoPay() {
    MovieFinderTheme {
        val uiState = MovieViewModelState(
            isSignedIn = true,
            selectedMovie = Movie(
                id = "movie1",
                title = "A cool movie",
                posterPath = "",
                overview = "Overview of the cool movie"
            ),
            streamProviders = listOf(
                VideoStreamPlatform("Netflix"),
                VideoStreamPlatform("Amazing video")
            ),
        )
        MovieDetails(uiState, {})
    }
}

@Preview("Movies details screen streaming disabled", fontScale = 1.5f, device = Devices.PIXEL_4_XL)
@Composable
fun PreviewMovieDetails() {
    MovieFinderTheme {

        val uiState = MovieViewModelState(
            isSignedIn = true,
            selectedMovie = Movie(
                id = "movie1",
                title = "A cool movie",
                posterPath = "",
                overview = "Overview of the cool movie"
            ),
            streamProviders = listOf(
                VideoStreamPlatform("Netflox"),
                VideoStreamPlatform("Amazing video")
            ),
        )
        MovieDetails(uiState, {})
    }
}