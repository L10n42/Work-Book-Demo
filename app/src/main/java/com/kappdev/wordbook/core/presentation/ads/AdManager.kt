package com.kappdev.wordbook.core.presentation.ads

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class AdManager(
    private val context: Context
) {

    private var interstitialAd: InterstitialAd? = null

    var isAdLoading by mutableStateOf(false)
        private set

    fun loadAndShowAd(adUnitId: AdUnitId, onResult: () -> Unit = {}) = loadAndShowAd(adUnitId, onResult, onResult)

    fun loadAndShowAd(adUnitId: AdUnitId, onAdDismissed: () -> Unit, onFailed: () -> Unit) {
        loadInterstitialAd(
            adUnitId = adUnitId,
            onFailed = onFailed,
            onLoaded = {
                showInterstitialAd(
                    onAdDismissed = onAdDismissed,
                    onAdFailed = onFailed
                )
            }
        )
    }

    fun loadInterstitialAd(
        adUnitId: AdUnitId,
        onLoaded: () -> Unit = {},
        onFailed: () -> Unit = {}
    ) {
        isAdLoading = true
        InterstitialAd.load(context, adUnitId.id, AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                    isAdLoading = false
                    onFailed()
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isAdLoading = false
                    onLoaded()
                }
            }
        )
    }

    fun showInterstitialAd(onAdDismissed: () -> Unit, onAdFailed: () -> Unit) {
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(e: AdError) {
                interstitialAd = null
                onAdFailed()
            }

            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                onAdDismissed()
            }
        }
        interstitialAd?.show(context as Activity)
    }
}