package com.example.moviefinder.iap

import android.util.Log
import com.amazon.device.iap.PurchasingListener
import com.amazon.device.iap.PurchasingService
import com.amazon.device.iap.model.*
import com.example.moviefinder.data.MoviesRepository

class MovieFinderPurchasingListener(private val moviesRepository: MoviesRepository): PurchasingListener {
    private var currentUserId: String? = null
    private var currentMarketplace: String? = null

    override fun onUserDataResponse(response: UserDataResponse) {
        when (response.requestStatus) {
            UserDataResponse.RequestStatus.SUCCESSFUL -> {
                currentUserId = response.userData.userId
                currentMarketplace = response.userData.marketplace
            }
            UserDataResponse.RequestStatus.FAILED, UserDataResponse.RequestStatus.NOT_SUPPORTED, null -> {
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
            PurchaseResponse.RequestStatus.SUCCESSFUL -> {
                PurchasingService.notifyFulfillment(
                    purchaseResponse.receipt.receiptId,
                    FulfillmentResult.FULFILLED
                )
                moviesRepository.setPurchaseStreamingInfo(true)
            }
            PurchaseResponse.RequestStatus.FAILED -> {}
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
                        moviesRepository.setPurchaseStreamingInfo(false)
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