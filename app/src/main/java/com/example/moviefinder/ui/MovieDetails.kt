package com.example.moviefinder.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
        Column() {
            if (selectedMovie != null) {
                Text(text = selectedMovie.overview, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(20.dp))

                if (uiState.hasPurchasedStreamingInfo) {
                    if (uiState.streamProviders.isNotEmpty()) {
                        Text(text = "Streaming on:", fontWeight = FontWeight.Bold)
                        uiState.streamProviders.forEach {
                            Text(it.name)
                        }
                    } else {
                        Text(text = "Not available on streaming", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(onClick = { onPurchase() }) {
                        Text(text = "Purchase Streaming Info")
                    }
                }
            }
        }
    }
}