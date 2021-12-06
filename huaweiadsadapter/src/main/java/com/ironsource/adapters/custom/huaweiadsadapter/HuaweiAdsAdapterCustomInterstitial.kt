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

    companion object {
        const val TAG = "CustomAdapter"
    }

    override fun loadAd(adData: AdData, activity: Activity, listener: InterstitialAdListener) {
        val instanceId = adData.getString("adunitid")
        Log.e(TAG, "loadAd for instanceId: $instanceId")

        if (instanceId == null) {
            Log.e(TAG, "ad failed to load. instanceId is null")
            listener.onAdLoadFailed(
                AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL,
                AdapterErrors.ADAPTER_ERROR_MISSING_PARAMS,
                "instanceId is missing"
            )
            return
        }

        val requestConfigurationBuilder = HwAds.getRequestOptions().toBuilder()

        val childDirected = adData.getString("tagforchildprotection")
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
        val underAgeOfConsent = adData.getString("tagunderageofpromise")
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

        val builder = AdParam.Builder()
        builder.setRequestOrigin("IronSource")
        val adRequest = builder.build()
        mInterstitialAd!!.loadAd(adRequest)


        val adListener: AdListener = object : AdListener() {
            override fun onAdLoaded() {
                // Called when an ad is loaded successfully.
                listener.onAdLoadSuccess()
            }

            override fun onAdFailed(errorCode: Int) {
                // Called when an ad fails to be loaded.
                Log.e(TAG, "ad failed to load. instanceId is null")
            }
        }
        mInterstitialAd!!.adListener = adListener

    }

    override fun showAd(adData: AdData, listener: InterstitialAdListener) {
        val instanceId = adData.getString("adunitid")
        Log.e(TAG, "showAd for instanceId: $instanceId")

        if (instanceId == null) {
            Log.e(TAG, "ad failed to show. instanceId is null")
            listener.onAdLoadFailed(
                AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL,
                AdapterErrors.ADAPTER_ERROR_MISSING_PARAMS,
                "instanceId is missing"
            )
            return
        }
        try {
            mInterstitialAd!!.adListener = object : AdListener() {
                override fun onAdClosed() {
                    Log.e(TAG, "ad closed")
                    listener.onAdClosed()
                    super.onAdClosed()
                }

                override fun onAdFailed(p0: Int) {
                    Log.e(TAG, "ad failed to show. ${p0.toString()}")
                    mInterstitialAd = null
                    listener.onAdShowFailed(
                        AdapterErrors.ADAPTER_ERROR_INTERNAL,
                        p0.toString()
                    )
                    super.onAdFailed(p0)
                }

                override fun onAdLeave() {
                    listener.onAdClosed()
                    super.onAdLeave()
                }

                override fun onAdOpened() {
                    mInterstitialAd = null
                    listener.onAdOpened()
                    listener.onAdShowSuccess()
                    super.onAdOpened()
                }

                override fun onAdLoaded() {
                    listener.onAdLoadSuccess()
                    super.onAdLoaded()
                }

                override fun onAdClicked() {
                    listener.onAdClicked()
                    super.onAdClicked()
                }

            }
            mInterstitialAd!!.show(ContextProvider.getInstance().currentActiveActivity)

        } catch (e: Exception) {

            Log.e(TAG, "ad failed to show. $e")
            mInterstitialAd = null
            listener.onAdShowFailed(AdapterErrors.ADAPTER_ERROR_INTERNAL, e.toString())
        }


    }

    override fun isAdAvailable(adData: AdData): Boolean {
        Log.e(TAG, "isAdAvailable. ${mInterstitialAd != null}")
        return mInterstitialAd != null
    }
}