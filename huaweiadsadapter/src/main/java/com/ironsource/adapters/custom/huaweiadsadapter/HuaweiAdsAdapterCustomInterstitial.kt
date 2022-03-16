package com.ironsource.adapters.custom.huaweiadsadapter

import android.app.Activity
import android.util.Log
import com.huawei.hms.ads.*
import com.ironsource.environment.ContextProvider
import com.ironsource.mediationsdk.adunit.adapter.BaseInterstitial
import com.ironsource.mediationsdk.adunit.adapter.listener.InterstitialAdListener
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors
import com.ironsource.mediationsdk.model.NetworkSettings

class HuaweiAdsAdapterCustomInterstitial(networkSettings: NetworkSettings) :
    BaseInterstitial<HuaweiAdsAdapterCustomAdapter>(networkSettings) {
    private var mInterstitialAd: InterstitialAd? = null
    private var mHuaweiAdsAdapterConfiguration = HuaweiAdsAdapterCustomAdapter()
    var TAG = HuaweiAdsAdapterCustomInterstitial::class.simpleName

    private var AD_UNIT_ID = "adunitid"
    private var TAG_FOR_CHILD_PROTECTION = "tagforchildprotection"
    private var TAG_UNDER_AGE_OF_PROMISE = "tagunderageofpromise"

    override fun loadAd(adData: AdData, activity: Activity, listener: InterstitialAdListener) {
        val instanceId = adData.getString(AD_UNIT_ID)
        Log.d(TAG, "Interstitial - loadAd() - for instanceId: $instanceId")

        if (instanceId == null) {
            Log.w(TAG, "Interstitial - loadAd() - instanceId is null")
            listener.onAdLoadFailed(
                AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL,
                AdapterErrors.ADAPTER_ERROR_MISSING_PARAMS,
                "InstanceId is missing"
            )
            return
        }

        val requestConfigurationBuilder = HwAds.getRequestOptions().toBuilder()

        val childDirected = adData.getString(TAG_FOR_CHILD_PROTECTION)
        if (childDirected != null) {
            if (java.lang.Boolean.parseBoolean(childDirected)) {
                requestConfigurationBuilder.setTagForChildProtection(TagForChild.TAG_FOR_CHILD_PROTECTION_TRUE)
            } else {
                requestConfigurationBuilder.setTagForChildProtection(TagForChild.TAG_FOR_CHILD_PROTECTION_FALSE)
            }
        } else {
            requestConfigurationBuilder.setTagForChildProtection(TagForChild.TAG_FOR_CHILD_PROTECTION_UNSPECIFIED)
        }

        // Publishers may want to mark their requests to receive treatment for users in the
        // European Economic Area (EEA) under the age of consent.
        val underAgeOfConsent = adData.getString(TAG_UNDER_AGE_OF_PROMISE)
        if (underAgeOfConsent != null) {
            if (java.lang.Boolean.parseBoolean(underAgeOfConsent)) {
                requestConfigurationBuilder.setTagForUnderAgeOfPromise(UnderAge.PROMISE_TRUE)
            } else {
                requestConfigurationBuilder.setTagForUnderAgeOfPromise(UnderAge.PROMISE_FALSE)
            }
        } else {
            requestConfigurationBuilder.setTagForUnderAgeOfPromise(UnderAge.PROMISE_UNSPECIFIED)
        }
        val requestConfiguration = requestConfigurationBuilder.build()
        HwAds.setRequestOptions(requestConfiguration)


        mHuaweiAdsAdapterConfiguration.setAdapterDebug(true)

        mInterstitialAd = InterstitialAd(activity.applicationContext)

        // "testb4znbuh3n2" is a dedicated test ad unit ID. Before releasing your app, replace the test ad unit ID with the formal one.
        mInterstitialAd!!.adId = instanceId

        val adRequest = AdParam.Builder().setRequestOrigin("IronSource").build()
        mInterstitialAd!!.loadAd(adRequest)


        val adListener: AdListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.d(TAG, "Interstitial - AdListener - onAdLoaded() - Ad loaded successfully")
                // Called when an ad is loaded successfully.
                listener.onAdLoadSuccess()
            }

            override fun onAdFailed(errorCode: Int) {
                Log.e(
                    TAG,
                    "Interstitial - AdListener - onAdFailed() - Failed to load Huawei interstitial with code: ${errorCode}."
                )
                // Called when an ad fails to be loaded.
                listener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL, errorCode,"Ad failed to load")
            }
        }
        mInterstitialAd!!.adListener = adListener

    }

    override fun isAdAvailable(adData: AdData): Boolean {
        Log.d(TAG, "Interstitial - AdListener - isAdAvailable()")
        return mInterstitialAd?.isLoaded ?: false
    }

    override fun showAd(adData: AdData, listener: InterstitialAdListener) {
        Log.d(TAG, "Interstitial - AdListener - showAd() - ShowAd for instanceId: ${adData.getString(AD_UNIT_ID)}")

        try {
            if (mInterstitialAd!!.isLoaded) {
                mInterstitialAd!!.adListener = object : AdListener() {
                    override fun onAdClosed() {
                        Log.d(TAG, "Interstitial - AdListener - onAdClosed()")
                        listener.onAdClosed()
                        super.onAdClosed()
                    }

                    override fun onAdFailed(p0: Int) {
                        Log.e(TAG, "\"Interstitial - AdListener - onAdFailed() -Ad failed to show. ${p0.toString()}")
                        mInterstitialAd = null
                        listener.onAdShowFailed(
                            AdapterErrors.ADAPTER_ERROR_INTERNAL,
                            p0.toString()
                        )
                        super.onAdFailed(p0)
                    }

                    override fun onAdLeave() {
                        Log.d(TAG, "Interstitial - AdListener - onAdLeave()")
                        listener.onAdClosed()
                        super.onAdLeave()
                    }

                    override fun onAdOpened() {
                        Log.d(TAG, "Interstitial - AdListener - onAdOpened()")
                        mInterstitialAd = null
                        listener.onAdOpened()
                        listener.onAdShowSuccess()
                        super.onAdOpened()
                    }

                    override fun onAdLoaded() {
                        Log.d(TAG, "Interstitial - AdListener - onAdLoaded()")
                        listener.onAdLoadSuccess()
                        super.onAdLoaded()
                    }

                    override fun onAdClicked() {
                        Log.d(TAG, "Interstitial - AdListener - onAdClicked()")
                        listener.onAdClicked()
                        super.onAdClicked()
                    }
                }
                mInterstitialAd!!.show(ContextProvider.getInstance().currentActiveActivity)
            }else{
                Log.e(TAG, "Interstitial - show() - InterstitialAd isLoaded false")
                listener.onAdShowFailed(AdapterErrors.ADAPTER_ERROR_INTERNAL, "Interstitial ad is not loaded yet!")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Interstitial - show() - Ad failed to show. $e")
            mInterstitialAd = null
            listener.onAdShowFailed(AdapterErrors.ADAPTER_ERROR_INTERNAL, e.toString())
        }
    }

    
}