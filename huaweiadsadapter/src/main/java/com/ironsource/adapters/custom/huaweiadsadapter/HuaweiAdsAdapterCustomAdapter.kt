package com.ironsource.adapters.custom.huaweiadsadapter

import android.content.Context
import android.util.Log
import com.huawei.hms.ads.HwAds
import com.ironsource.mediationsdk.adunit.adapter.BaseAdapter
import com.ironsource.mediationsdk.adunit.adapter.listener.NetworkInitializationListener
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors
import java.io.PrintWriter
import java.io.StringWriter

class HuaweiAdsAdapterCustomAdapter : BaseAdapter() {

    private val TAG = HuaweiAdsAdapterCustomAdapter::class.simpleName

    override fun init(adData: AdData, context: Context, listener: NetworkInitializationListener?) {
        try {
            HwAds.init(context.applicationContext)
            listener?.onInitSuccess()
        } catch (e: Exception) {
            val stacktrace =
                    StringWriter().also { e.printStackTrace(PrintWriter(it)) }.toString().trim()
            Log.e(TAG, "Init Failed: $stacktrace")
            listener?.onInitFailed(AdapterErrors.ADAPTER_ERROR_INTERNAL, e.toString())
        }
    }

    override fun getNetworkSDKVersion(): String {
        Log.d(TAG, "getNetworkSDKVersion() = ${HwAds.getSDKVersion()}")
        return HwAds.getSDKVersion()
    }

    override fun getAdapterVersion(): String {
        Log.d(TAG, "getAdapterVersion() = ${getVersion()}")
        return getVersion()
    }

    override fun setAdapterDebug(isDebug: Boolean) {
        Log.d(TAG, "setAdapterDebug() = $isDebug")
        super.setAdapterDebug(isDebug)
    }

    companion object {
        fun getVersion() = ADAPTER_VERSION
        private const val ADAPTER_VERSION = "1.0"
    }
}