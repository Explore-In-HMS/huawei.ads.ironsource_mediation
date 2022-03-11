package com.ironsource.adapters.custom.huaweiadsadapter

import android.app.Activity
import android.util.Log
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.HwAds
import com.huawei.hms.ads.TagForChild
import com.huawei.hms.ads.UnderAge
import com.huawei.hms.ads.reward.Reward
import com.huawei.hms.ads.reward.RewardAd
import com.huawei.hms.ads.reward.RewardAdLoadListener
import com.huawei.hms.ads.reward.RewardAdStatusListener
import com.ironsource.environment.ContextProvider
import com.ironsource.mediationsdk.adunit.adapter.BaseRewardedVideo
import com.ironsource.mediationsdk.adunit.adapter.listener.RewardedVideoAdListener
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors
import com.ironsource.mediationsdk.model.NetworkSettings


class HuaweiAdsAdapterCustomRewardedVideo(networkSettings: NetworkSettings) :
    BaseRewardedVideo<HuaweiAdsAdapterCustomAdapter>(networkSettings) {

    private var mRewardAd: RewardAd? = null
    private var mHuaweiAdsAdapterConfiguration = HuaweiAdsAdapterCustomAdapter()

    companion object {
        const val TAG = "CustomAdapter"
    }

    override fun loadAd(adData: AdData, activity: Activity, listener: RewardedVideoAdListener) {
        val instanceId = adData.getString("adunitid")

        Log.d(TAG, "loadAd for InstanceId: $instanceId")

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

        // "testx9dtjwj8hp" is a dedicated test ad unit ID
//        mRewardAd = RewardAd(activity.applicationContext, "testx9dtjwj8hp")
        mRewardAd = RewardAd(activity.applicationContext, instanceId)

        val builder = AdParam.Builder()
        builder.setRequestOrigin("IronSource")

        val adRequest = builder.build()

        val rewardAdLoadListener: RewardAdLoadListener = object : RewardAdLoadListener() {
            override fun onRewardedLoaded() {
                Log.d(TAG, "onRewardedLoaded")
                listener.onAdLoadSuccess()
            }

            override fun onRewardAdFailedToLoad(errorCode: Int) {
                Log.e(TAG, "onRewardAdFailedToLoad")
                listener.onAdLoadFailed(
                    AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL,
                    AdapterErrors.ADAPTER_ERROR_INTERNAL,
                    errorCode.toString()
                )
            }
        }

        mRewardAd!!.loadAd(adRequest, rewardAdLoadListener)
    }

    override fun showAd(adData: AdData, listener: RewardedVideoAdListener) {
        val instanceId = adData.getString("adunitid")

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
            if (mRewardAd!!.isLoaded) {

                val statusListener = object : RewardAdStatusListener() {

                    override fun onRewardAdClosed() {
                        Log.d(TAG, "onRewardAdClosed")
                        listener.onAdClosed()
                        super.onRewardAdClosed()
                    }

                    override fun onRewardAdFailedToShow(p0: Int) {
                        Log.e(TAG, "onRewardAdFailedToShow : $p0")
                        listener.onAdLoadFailed(
                            AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL,
                            AdapterErrors.ADAPTER_ERROR_INTERNAL,
                            p0.toString()
                        )
                        super.onRewardAdFailedToShow(p0)
                    }

                    override fun onRewardAdOpened() {
                        Log.e(TAG, "onRewardAdOpened")
                        listener.onAdOpened()
                        super.onRewardAdOpened()
                    }

                    override fun onRewarded(p0: Reward?) {
                        Log.e(TAG, "onRewarded")
                        listener.onAdRewarded()
                        super.onRewarded(p0)
                    }
                }

                mRewardAd!!.show(ContextProvider.getInstance().currentActiveActivity,statusListener)
            }

        } catch (e: Exception) {

            Log.e(TAG, "Ad failed to show. $e")
            mRewardAd = null
            listener.onAdShowFailed(AdapterErrors.ADAPTER_ERROR_INTERNAL, e.toString())
        }
    }

    override fun isAdAvailable(adData: AdData): Boolean {
        Log.e(TAG, "isAdAvailable. ${mRewardAd != null}")
        return mRewardAd != null
    }
}