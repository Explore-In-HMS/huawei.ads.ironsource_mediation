package com.ironsource.adapters.custom.huaweiadsadapter

import android.content.Context
import android.util.Log
import com.huawei.hms.ads.HwAds
import com.ironsource.mediationsdk.adunit.adapter.BaseAdapter
import com.ironsource.mediationsdk.adunit.adapter.listener.NetworkInitializationListener
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors

class HuaweiAdsAdapterCustomAdapter : BaseAdapter() {

    private fun getVersion() = ADAPTER_VERSION
    private var ADAPTER_VERSION = "1.1.1"
    private val TAG = HuaweiAdsAdapterCustomAdapter::class.simpleName


    override fun init(adData: AdData, context: Context, listener: NetworkInitializationListener?) {
        Log.d(TAG, "Adapter - init()")
        try {
            HwAds.init(context.applicationContext)
            listener?.onInitSuccess()
        } catch (e: Exception) {
            Log.d(TAG, "Adapter - init() - Init failed. $e")

            listener?.onInitFailed(AdapterErrors.ADAPTER_ERROR_INTERNAL, e.toString())
        }
    }

    override fun getNetworkSDKVersion(): String {
        Log.d(TAG, "Adapter - getNetworkSDKVersion ${HwAds.getSDKVersion()}")
        return HwAds.getSDKVersion()
    }

    override fun getAdapterVersion(): String {
        Log.d(TAG, "Adapter - getAdapterVersion ${getVersion()}")
        return getVersion()
    }

    override fun setAdapterDebug(isDebug: Boolean) {
        super.setAdapterDebug(isDebug)
    }
}