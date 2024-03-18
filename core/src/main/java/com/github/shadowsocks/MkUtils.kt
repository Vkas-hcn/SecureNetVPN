package com.github.shadowsocks

import android.content.Context
import android.util.Log
import android.net.VpnService
import android.text.format.Formatter
import com.github.shadowsocks.aidl.TrafficStats
import com.github.shadowsocks.bg.BaseService
import com.google.gson.Gson
import com.tencent.mmkv.MMKV

object MkUtils {
    private val mmkv by lazy {
        MMKV.mmkvWithID("secureNet", MMKV.MULTI_PROCESS_MODE)
    }

    private fun getFlowData(): List<String>? {
        val data = mmkv.decodeString("app_pack_name", "")
        if (data == null) {
            return null
        }
        if (data.isEmpty()) {
            return null
        }
        return data.split(",").toMutableList()
    }

    fun brand(builder: VpnService.Builder) {
        val dataList = getFlowData()
        Log.e("TAG", "getAroundFlowJsonData-ss: ${Gson().toJson(dataList)}")

        (dataList)?.iterator()?.forEachRemaining {
            runCatching { builder.addDisallowedApplication(it) }
        }
    }

    private fun listGmsPackages(): List<String> {
        return listOf(
            "com.google.android.gms",
            "com.google.android.ext.services",
            "com.google.process.gservices",
            "com.android.vending",
            "com.google.android.gms.persistent",
            "com.google.android.cellbroadcastservice",
            "com.google.android.packageinstaller",
            "com.google.android.gms.location.history",
        )
    }


    fun getSpeedData(service: BaseService.Interface, stats: TrafficStats){
        val data = (service as Context).getString(
            com.github.shadowsocks.core.R.string.traffic,
            (service as Context).getString(
                com.github.shadowsocks.core.R.string.speed,
                Formatter.formatFileSize((service as Context), stats.txRate)
            ),
            (service as Context).getString(
                com.github.shadowsocks.core.R.string.speed,
                Formatter.formatFileSize((service as Context), stats.rxRate)
            )
        )
        val pattern = """([\d.]+)\s*([^\s]+)\s*([↑↓])\s*([\d.]+)\s*([^\s]+)\s*([↑↓])""".toRegex()
        val matches = pattern.find(data)
        if (matches != null) {
            val (value1, unit1, arrow1, value2, unit2, arrow2) = matches.destructured
            mmkv.encode("secure_dow", "$value1 $unit1")
            mmkv.encode("secure_up", "$value2 $unit2")
        }
    }
}