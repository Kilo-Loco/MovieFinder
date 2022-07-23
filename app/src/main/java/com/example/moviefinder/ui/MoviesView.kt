package com.example.moviefinder.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import androidx.compose.foundation.lazy.items

fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("no activity")
}

@ExperimentalFoundationApi
@Composable
fun MoviesView(navController: NavController, vm: MovieViewModel) {
    LaunchedEffect(Unit, block = {
        vm.getTopRatedMovies()
    })

    val uiState by vm.uiState.collectAsState()

    val currentActivity = LocalContext.current.findActivity()
    val authText: String
    val authAction: () -> Unit
    if (uiState.isSignedIn) {
        authText = "Sign Out"
        authAction = { vm.signOut() }
    } else {
        authText = "Sign In"
        authAction = { vm.login(currentActivity) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Top Rated Movies") },
                actions = {
                    Button(onClick = authAction) {
                        Text(authText)
                    }
                }
            )
        }
    ) {
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
                                vm.selectMovie(movie)
                                navController.navigate("movieDetails")
                            })
                    )
                }
            }
        )
    }
}