package com.example.moviefinder

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.ui.platform.LocalContext
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
import com.amazon.device.iap.PurchasingListener
import com.amazon.device.iap.PurchasingService
import com.amazon.device.iap.model.*
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.example.moviefinder.ui.theme.MovieFinderTheme
import java.lang.Exception
import java.util.HashSet

const val parentSKU = "com.kiloloco.moviefinder.iap.consumable.streaminfo"

@ExperimentalFoundationApi
class MainActivity : ComponentActivity() {

    private var currentUserId: String? = null
    private var currentMarketplace: String? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.i("KILO", "Permission granted")
        } else {
            Log.i("KILO", "Permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureAmplify()
        PurchasingService.registerListener(this, purchasingListener)

        setContent {
            MaterialTheme {
                AppNavigator()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //getUserData() will query the Appstore for the Users information
        PurchasingService.getUserData()
        //getPurchaseUpdates() will query the Appstore for any previous purchase
        PurchasingService.getPurchaseUpdates(true)
        //getProductData will validate the SKUs with Amazon Appstore
        val productSkus: MutableSet<String> = HashSet()
        productSkus.add(parentSKU)
        PurchasingService.getProductData(productSkus)
        Log.i("KILO", "Validating SKUs with Amazon")
    }

    private var purchasingListener: PurchasingListener = object : PurchasingListener {
        override fun onUserDataResponse(response: UserDataResponse) {
            val status = response.requestStatus
            when (status) {
                UserDataResponse.RequestStatus.SUCCESSFUL -> {
                    currentUserId = response.userData.userId
                    currentMarketplace = response.userData.marketplace
                }
                UserDataResponse.RequestStatus.FAILED, UserDataResponse.RequestStatus.NOT_SUPPORTED -> {
                }
            }
        }

        override fun onProductDataResponse(productDataResponse: ProductDataResponse) {
            when (productDataResponse.requestStatus) {
                ProductDataResponse.RequestStatus.SUCCESSFUL -> {
                    val products = productDataResponse.productData
                    for (key in products.keys) {
                        val product = products[key]
                        Log.v(
                            "Product:",
                            "Product: ${product!!.title} \n Type: ${product.productType}\n SKU: ${product.sku}\n Price: ${product.price}\n Description: ${product.description}\n"
                        )
                    }
                    for (s in productDataResponse.unavailableSkus) {
                        Log.v("Unavailable SKU:$s", "Unavailable SKU:$s")
                    }
                }
                ProductDataResponse.RequestStatus.FAILED -> Log.v("FAILED", "FAILED")

                else -> {
                    Log.e("Product", "Not supported")
                }
            }
        }

        override fun onPurchaseResponse(purchaseResponse: PurchaseResponse) {
            when (purchaseResponse.requestStatus) {
                PurchaseResponse.RequestStatus.SUCCESSFUL -> PurchasingService.notifyFulfillment(
                    purchaseResponse.receipt.receiptId,
                    FulfillmentResult.FULFILLED
                )
                PurchaseResponse.RequestStatus.FAILED -> {
                }
                else -> {
                    Log.e("Product", "Not supported")
                }
            }
        }

        override fun onPurchaseUpdatesResponse(response: PurchaseUpdatesResponse) {
            when (response.requestStatus) {
                PurchaseUpdatesResponse.RequestStatus.SUCCESSFUL -> {
                    for (receipt in response.receipts) {
                        if (!receipt.isCanceled) {
//                            binding.textView.apply {
//                                text = "SUBSCRIBED"
//                                setTextColor(android.graphics.Color.RED)
//                            }
                        }
                    }
                    if (response.hasMore()) {
                        PurchasingService.getPurchaseUpdates(true)
                    }
                }
                PurchaseUpdatesResponse.RequestStatus.FAILED -> Log.d("FAILED", "FAILED")
                else -> {
                    Log.e("Product", "Not supported")
                }
            }
        }
    }

    private fun configureAmplify() {
        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.configure(applicationContext)
            Log.i("KILO", "Configured amplify")
        } catch (e: Exception) {
            Log.e("KILO", "Amplify configuration failed", e)
        }
    }

    @Composable
    private fun AppNavigator() {
        val navController = rememberNavController()
        val vm = MovieViewModel()
        vm.checkAuthStatus()
        
        NavHost(navController = navController, startDestination = "topMovies") {
            composable("topMovies") {
                MoviesView(
                    navController = navController,
                    vm = vm,
                    onToggleAuth = { toggleAuth(vm) }
                )
            }
            composable("movieDetails") { MovieDetails(vm = vm) }
        }
    }

    private fun toggleAuth(vm: MovieViewModel) {
        if (vm.isSignedIn) {
            Amplify.Auth.signOut(
                { Log.i("KILO", "Signed out") },
                { Log.e("KILO", "Failed log out", it) }
            )
        } else {
            Amplify.Auth.signInWithWebUI(this,
                { Log.i("AuthQuickStart", "Signin OK = $it") },
                { Log.e("AuthQuickStart", "Signin failed", it) }
            )
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun MoviesView(navController: NavController, vm: MovieViewModel, onToggleAuth: () -> Unit) {
    LaunchedEffect(Unit, block = {
        vm.getTopRatedMovies()
    })

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Top Rated Movies") },
                actions = {
                    Button(onClick = onToggleAuth) {
                        if (vm.isSignedIn) {
                            Text("Login")
                        } else {
                            Text(text = "Sign Out")
                        }
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