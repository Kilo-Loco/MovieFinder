package com.example.moviefinder.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.moviefinder.data.Movie
import com.example.moviefinder.ui.theme.MovieFinderTheme

@ExperimentalFoundationApi
@Composable
fun MoviesView(
    onLogin: () -> Unit,
    onLogout: () -> Unit,
    onSelectMovie: (Movie) -> Unit,
    uiState: MovieViewModelState
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Top Rated Movies") },
                actions = {
                    LoginButton(uiState.isSignedIn, onLogin, onLogout)
                }
            )
        }
    ) {
        Column() {
            if (uiState.isSignedIn) {
                LazyVerticalGrid(
                    cells = GridCells.Adaptive(150.dp),
                    content = {
                        items(uiState.moviesList) { movie ->
                            Image(
                                painter = rememberImagePainter(movie.fullPostPath),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .aspectRatio(0.75f)
                                    .clickable(onClick = {
                                        onSelectMovie(movie)
                                    })
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun LoginButton(signedIn: Boolean, onLogin: () -> Unit, onLogout: () -> Unit) {
    if (signedIn) {
        Button(onClick = { onLogout() }) { Text("Sign Out") }
    } else {
        Button(onClick = { onLogin() }) { Text("Sign In") }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Preview("Movies list screen", fontScale = 1.5f, device = Devices.PIXEL_4_XL)
@Composable
fun PreviewMoviesView() {
    MovieFinderTheme {

        val uiState = MovieViewModelState(
            isSignedIn = true,
            moviesList = listOf(
                Movie(
                    id = "movie1",
                    posterPath = "https://image.tmdb.org/t/p/w500/q6y0Go1tsGEsmtFryDOJo3dEmqu.jpg",
                    title = "A cool movie",
                    overview = "Overview of the cool movie"
                ), Movie(
                    id = "movie2",
                    title = "A cool movie 2",
                    posterPath = "https://image.tmdb.org/t/p/w500/2CAL2433ZeIihfX1Hb2139CX0pW.jpg",
                    overview = "Overview of the cool movie 2"
                )
            )
        )
        MoviesView({}, {}, {}, uiState)
    }
}