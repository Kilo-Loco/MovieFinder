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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MovieDetails(vm: MovieViewModel) {

    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(Unit, block = {
        vm.getStreamingProviders()
    })
    val selectedMovie = uiState.selectedMovie!!

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
                Button(onClick = { vm.purchaseStreamingInfo() }) {
                    Text(text = "Purchase Streaming Info")
                }
            }
        }
    }
}