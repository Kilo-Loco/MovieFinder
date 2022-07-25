package com.example.moviefinder.iap

import android.util.Log
import com.amazon.device.iap.PurchasingService

const val parentSKU = "com.kiloloco.moviefinder.iap.consumable.streaminfo"

interface IAPService {
    fun getAppStoreData()
    fun purchaseStreamingInfo()
}

class IAPHandler() : IAPService {

    override fun getAppStoreData() {
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

    override fun purchaseStreamingInfo() {
        PurchasingService.purchase(parentSKU)
    }
}