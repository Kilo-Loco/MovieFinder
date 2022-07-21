package com.example.moviefinder

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amazon.device.iap.PurchasingService

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

            if (vm.hasPurchasedStreamingInfo) {
                if (vm.streamProviders.isNotEmpty()) {
                    Text(text = "Streaming on:", fontWeight = FontWeight.Bold)
                    vm.streamProviders.forEach {
                        Text(it.name)
                    }
                } else {
                    Text(text = "Not available on streaming", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(onClick = { PurchasingService.purchase(parentSKU) }) {
                    Text(text = "Purchase Streaming Info")
                }
            }
        }
    }
}