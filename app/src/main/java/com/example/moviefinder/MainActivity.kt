package com.example.moviefinder

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.moviefinder.ui.theme.MovieFinderTheme

@ExperimentalFoundationApi
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                AppNavigator()
            }
        }
    }

    @Composable
    fun AppNavigator() {
        val navController = rememberNavController()
        val vm = MovieViewModel()
        
        NavHost(navController = navController, startDestination = "topMovies") {
            composable("topMovies") { MoviesView(navController = navController, vm = vm) }
            composable("movieDetails") { MovieDetails(vm = vm) }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun MoviesView(navController: NavController, vm: MovieViewModel) {
    LaunchedEffect(Unit, block = {
        vm.getTopRatedMovies()
    })

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Top Rated Movies") },
                actions = {
                    Button(onClick = { /*TODO*/ }) {
                        Text("Login")
                    }
                }
            )
        }
    ) {
        LazyVerticalGrid(
            cells = GridCells.Adaptive(150.dp),
            content = {
                items(vm.moviesList) { movie ->
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

@Composable
fun MovieDetails(vm: MovieViewModel) {

    LaunchedEffect(Unit, block = {
        vm.getStreamingProviders()
    })
    val selectedMovie = vm.selectedMovie!!
    
    Scaffold(
        topBar = {
            TopAppBar(title = { Row {
                Text(selectedMovie.title)
            } })
        }
    ) {
        Column() {
            Text(text = selectedMovie.overview, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(20.dp))

            if (vm.streamProviders.isNotEmpty()) {
                Text(text = "Streaming on:", fontWeight = FontWeight.Bold)
                vm.streamProviders.forEach {
                    Text(it.name)
                }
            } else {
                Text(text = "Not available on streaming", fontWeight = FontWeight.Bold)
            }
        }
    }
}