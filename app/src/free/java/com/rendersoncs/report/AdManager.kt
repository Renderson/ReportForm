package com.rendersoncs.report

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError

class AdManager(private val context: Activity, private val adUnitId: String) {
    private var mInterstitialAd: InterstitialAd? = null
    private var isAdLoading: Boolean = false

    fun loadAdMob() {
        if (isAdLoading || mInterstitialAd != null) return

        isAdLoading = true
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                    isAdLoading = false
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    isAdLoading = false

                    mInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                mInterstitialAd = null
                                loadAdMob()
                            }

                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                mInterstitialAd = null
                                loadAdMob()
                            }
                        }
                }
            }
        )
    }

    fun showAdMob(onAdDismissed: () -> Unit) {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    onAdDismissed()
                    loadAdMob()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    onAdDismissed()
                    loadAdMob()
                }
            }
            mInterstitialAd?.show(context)
        } else {
            loadAdMob()
            onAdDismissed()
        }
    }
}

