package com.ironsource.adapters.custom.huaweiadsadapter

import android.app.Activity
import android.content.res.Configuration
import android.content.res.Resources
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.doOnPreDraw
import com.huawei.hms.ads.*
import com.huawei.hms.ads.banner.BannerView
import com.ironsource.mediationsdk.ISBannerSize
import com.ironsource.mediationsdk.adunit.adapter.BaseBanner
import com.ironsource.mediationsdk.adunit.adapter.listener.BannerAdListener
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors
import com.ironsource.mediationsdk.model.NetworkSettings

class HuaweiAdsAdapterCustomBanner(networkSettings: NetworkSettings) :
    BaseBanner<HuaweiAdsAdapterCustomAdapter>(networkSettings) {

    private lateinit var adView: View
    private var bannerView: BannerView? = null
    private var mHuaweiAdsAdapterConfiguration = HuaweiAdsAdapterCustomAdapter()
    private val TAG = HuaweiAdsAdapterCustomBanner::class.simpleName

    private val TAG_FOR_CHILD_PROTECTION = "tagforchildprotection"
    private val TAG_UNDER_AGE_OF_PROMISE = "tagunderageofpromise"
    private val AD_UNIT_ID = "adunitid"

    override fun loadAd(adData: AdData, activity: Activity, isBannerSize: ISBannerSize, listener: BannerAdListener) {

        val isFlutterApp: Boolean = try {
            Class.forName("io.flutter.app.FlutterActivity")
            true
        } catch (e: ClassNotFoundException) {
            Log.e(TAG,e.message.toString())
            false
        }

        val isUnityApp: Boolean = try {
            Class.forName("com.unity3d.player.UnityPlayerActivity")
            true
        } catch (e: ClassNotFoundException) {
            Log.e(TAG,e.message.toString())
            false
        }

        val instanceId = adData.getString(AD_UNIT_ID)
        Log.d(TAG, "BannerAdapter - loadAd() - for adUnitID: $instanceId")

        if (instanceId == null) {
            Log.e(TAG, "BannerAdapter - loadAd() - instanceId is null")
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

        val adRequest = AdParam.Builder().setRequestOrigin("IronSource").build()


        mHuaweiAdsAdapterConfiguration.setAdapterDebug(true)

        bannerView = BannerView(activity)
        bannerView!!.adId = instanceId
        bannerView!!.bannerAdSize = getAdSize(isBannerSize)
        crossPlatformControl(activity,isFlutterApp,isUnityApp)
        bannerView!!.adListener = createAdListener(listener)
        Log.d(TAG, "BannerAdapter - Huawei Banner Ad initialized")
        bannerView!!.loadAd(adRequest)



    }

    private fun crossPlatformControl(activity: Activity, isFlutterApp: Boolean, isUnityApp:Boolean){

        if (isFlutterApp || isUnityApp) {
            activity.runOnUiThread {
                bannerView!!.visibility = View.INVISIBLE
            }
            bannerView!!.doOnPreDraw {
                Log.d(
                    TAG,
                    "BannerEventLoader - loadAd() - doOnPreDraw - Optimizing banner ad rendering for cross platform"
                )
                activity.runOnUiThread {
                    if (activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {

                        val displayMetrics = Resources.getSystem().displayMetrics
                        val bannerWidth = displayMetrics.heightPixels
                        val bannerHeight = displayMetrics.widthPixels

                        val calculatedCoeff = if (bannerHeight >= bannerWidth) {
                            bannerHeight.toFloat() / bannerWidth
                        } else {
                            bannerWidth.toFloat() / bannerHeight
                        }

                        val params: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
                            (it.width / calculatedCoeff).toInt(),
                            (it.height / calculatedCoeff).toInt()
                        )
                        params.gravity = Gravity.CENTER

                        bannerView!!.layoutParams = params
                        Log.d(
                            TAG,
                            "BannerEventLoader - loadAd() - doOnPreDraw() - Calculated Coefficient:$calculatedCoeff, New width ${(it.width / calculatedCoeff).toInt()}, New Height: ${(it.height / calculatedCoeff).toInt()}"
                        )

                    } else {
                        val params: FrameLayout.LayoutParams = FrameLayout.LayoutParams(it.width, it.height)
                        params.gravity = Gravity.CENTER
                        bannerView!!.layoutParams = params
                    }
                    bannerView!!.visibility = View.VISIBLE
                }
            }
        }

    }

    private fun getAdSize(size: ISBannerSize): BannerAdSize? {
        val description = size.description
        var adSize: BannerAdSize? = null
        when (description) {
            "RECTANGLE" ->  {
                adSize = BannerAdSize.BANNER_SIZE_300_250

            }
            "LARGE" ->  {
                adSize = BannerAdSize.BANNER_SIZE_320_100
            }
            "SMART" ->  {
                adSize = BannerAdSize.BANNER_SIZE_SMART

            }
            "BANNER" -> {
                adSize = BannerAdSize.BANNER_SIZE_320_50

            }
            "CUSTOM" ->  {
                adSize = BannerAdSize(size.height, size.width)

            }
        }
        Log.d(TAG, "Return Banner Ad Size --->>> $adSize")
        return adSize
    }


    private fun createAdListener(listener: BannerAdListener): AdListener{
        return object : AdListener(){

            override fun onAdClosed() {
                super.onAdClosed()
                Log.d(TAG, "BannerAdapter - AdListener() - AdClosed")
                listener.onAdScreenDismissed()
            }

            override fun onAdFailed(p0: Int) {
                super.onAdFailed(p0)
                Log.d(TAG, "BannerAdapter - AdListener() - AdFailed - Error Code: $p0")
                listener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL,p0, "Ad load failed")
            }

            override fun onAdLeave() {
                super.onAdLeave()
                Log.d(TAG, "BannerAdapter - AdListener() - AdLeave")
                listener.onAdLeftApplication()
            }

            override fun onAdOpened() {
                super.onAdOpened()
                Log.d(TAG, "BannerAdapter - AdListener() - AdOpened")
                listener.onAdOpened()
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.d(TAG, "BannerAdapter - AdListener() - AdLoaded!!!")
                listener.onAdLoadSuccess(bannerView as View,FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT))
            }

            override fun onAdClicked() {
                super.onAdClicked()
                Log.d(TAG, "BannerAdapter - AdListener() - AdClicked")
                listener.onAdClicked()
            }
        }
    }

    override fun destroyAd(adData: AdData) {
        Log.d(TAG, "Huawei Ads Banner onDestroy")
        if (adView is BannerView) {
            (adView as BannerView).destroy()
        }
    }
}