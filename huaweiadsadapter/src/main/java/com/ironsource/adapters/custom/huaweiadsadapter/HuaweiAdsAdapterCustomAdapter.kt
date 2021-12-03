package com.ironsource.adapters.custom.huaweiadsadapter

import android.content.Context
import android.util.Log
import com.huawei.hms.ads.HwAds
import com.ironsource.mediationsdk.adunit.adapter.BaseAdapter
import com.ironsource.mediationsdk.adunit.adapter.listener.NetworkInitializationListener
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors

class HuaweiAdsAdapterCustomAdapter : BaseAdapter() {
    override fun init(adData: AdData, context: Context, listener: NetworkInitializationListener?) {
        try {
            HwAds.init(context.applicationContext)
            listener?.onInitSuccess()
        } catch (e: Exception) {
            Log.e(TAG, "Init failed. $e")
            listener?.onInitFailed(AdapterErrors.ADAPTER_ERROR_INTERNAL, e.toString())
        }
    }

    override fun getNetworkSDKVersion(): String {
        Log.e(TAG, "getNetworkSDKVersion ${HwAds.getSDKVersion()}")
        return HwAds.getSDKVersion()
    }

    override fun getAdapterVersion(): String {
        Log.e(TAG, "getAdapterVersion ${getVersion()}")
        return getVersion()
    }

    override fun setAdapterDebug(isDebug: Boolean) {
        super.setAdapterDebug(isDebug)
    }

    companion object {
        fun getVersion() = ADAPTER_VERSION
        private const val ADAPTER_VERSION = "1.0"
        private const val TAG = "CustomAdapter"
    }
}