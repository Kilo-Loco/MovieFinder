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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.moviefinder.Movie

@ExperimentalFoundationApi
@Composable
fun MoviesView(
    navController: NavController,
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
                                        navController.navigate("movieDetails")
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