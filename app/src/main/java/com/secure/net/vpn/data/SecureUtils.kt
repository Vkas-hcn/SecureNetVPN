package com.secure.net.vpn.data

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.google.gson.Gson
import com.secure.net.vpn.R
import com.secure.net.vpn.data.DataUtils.getServiceData
import org.json.JSONObject
import java.util.Base64
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.secure.net.vpn.app.SecureApp
import java.util.Locale

object SecureUtils {
    fun getVpnNetData() {
        getTbaList("requ")
        val date = System.currentTimeMillis()

        getServiceData(
            DataUtils.vpn_service_url,
            onSuccess = {
                val result = decodeModifiedBase64(it)
                Log.e("TAG", "getVpnNetData-onSuccess:$result")
                DataUtils.service_string = result ?: ""
                getTbaList("seer")
                val time = (System.currentTimeMillis()-date)/1000
                getTbaList("timee", "time",time,111)
            },
            onError = {
                Log.e("TAG", "getVpnNetData -onError: $it")
            }
        )
    }

    private fun decodeModifiedBase64(input: String): String? {
        if (input.length <= 16) {
            return null
        }
        val modifiedString = input.dropLast(5)
        val swappedCaseString = modifiedString.map { char ->
            when {
                char.isLowerCase() -> char.toUpperCase()
                char.isUpperCase() -> char.toLowerCase()
                else -> char
            }
        }.joinToString("")
        return try {
            String(Base64.getDecoder().decode(swappedCaseString))
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    fun getAllVpnListData(): MutableList<ServerDetailBean> {
        val list: MutableList<ServerDetailBean> = getVpnServiceData()
        list.add(0, getVpnSmartData())
        return list
    }

    fun isHaveServeData(): Boolean {
        val vpnBean = Gson().fromJson(DataUtils.service_string, SecureVpnBean::class.java)
        if (vpnBean == null) {
            getVpnNetData()
            return false
        }
        if (vpnBean.data.zpXcuZ.isEmpty()) {
            getVpnNetData()
            return false
        }
        return true
    }

    private fun getVpnServiceData(): MutableList<ServerDetailBean> {
        val vpnBean = Gson().fromJson(DataUtils.service_string, SecureVpnBean::class.java)
            ?: return mutableListOf()
        return vpnBean.data.zpXcuZ as MutableList<ServerDetailBean>
    }

    private fun getVpnSmartData(): ServerDetailBean {
        val vpnBean = Gson().fromJson(DataUtils.service_string, SecureVpnBean::class.java)
        if (vpnBean == null) {
            getVpnNetData()
            return ServerDetailBean("", "", "", "", 0, "", "", "", 0)
        }
        val bestBean = vpnBean.data.xqXlJgMTB.random()
        bestBean.smartService = 1
        bestBean.OTLFDAAE = "Fast Server"
        return bestBean
    }

    fun isHaveNetWork(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        if (networkCapabilities != null) {
            return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        }
        return false
    }

    fun getNowVpnBean(): ServerDetailBean {
        if (DataUtils.currentlyServer.isEmpty()) {
            return getVpnSmartData()
        }
        val bean = Gson().fromJson(DataUtils.currentlyServer, ServerDetailBean::class.java)
        return if (bean.XVQxEerTo.isEmpty()) {
            getAllVpnListData()[0]
        } else {
            bean
        }
    }

    fun setNowVpnBean(serverVpn: ServerDetailBean) {
        DataUtils.currentlyServer = Gson().toJson(serverVpn)
    }

    fun getSelectVpnServiceData(): ServerDetailBean {
        if (DataUtils.clickServer.isEmpty()) {
            DataUtils.clickServer = DataUtils.currentlyServer
        }
        return Gson().fromJson(DataUtils.clickServer, ServerDetailBean::class.java)
    }

    fun setSelectVpnServiceData(serverVpn: ServerDetailBean) {
        DataUtils.clickServer = Gson().toJson(serverVpn)
    }

    fun String.getCountryImage(): Int {
        return when (this) {
            "China" -> R.drawable.ic_cn
            "United States" -> R.drawable.ic_us
            "Japan" -> R.drawable.ic_jp
            "Singapore" -> R.drawable.ic_sg
            "United Kingdom" -> R.drawable.ic_gb
            "Germany" -> R.drawable.ic_de
            "France" -> R.drawable.ic_fr
            "Canada" -> R.drawable.ic_ca
            "Australia" -> R.drawable.ic_au
            "India" -> R.drawable.ic_in
            "South Korea" -> R.drawable.ic_kr
            "Brazil" -> R.drawable.ic_br
            "Spain" -> R.drawable.ic_es
            "Switzerland" -> R.drawable.ic_ch
            "Denmark" -> R.drawable.ic_dk
            "Ireland" -> R.drawable.ic_ie
            "Belgium" -> R.drawable.ic_be
            "United Arab Emirates" -> R.drawable.ic_ae
            else -> R.drawable.ic_fast
        }
    }
    private fun getTbaList(
        eventName: String,
        parameterName: String = "",
        tbaValue: Any = 0,
        wTime: Int = 0,
    ) {

        val json = if (wTime == 0) {
            DataUtils.getTbaDataJson(SecureApp.instance, eventName)
        } else {
            DataUtils.getTbaTimeDataJson(SecureApp.instance, tbaValue, eventName, parameterName)
        }
        Log.e("TAG", "mai-dian-----$eventName-----: parameter=${json}", )
        try {
            DataUtils.post(DataUtils.tab_url, json, object : Callback {
                override fun onSuccess(response: String) {
                    Log.e("TAG", "mai-dian-----$eventName-----onSuccess::${response}")
                }

                override fun onFailure(error: String) {
                    Log.e("TAG", "mai-dian-----$eventName-----onFailure:${error}")

                }
            })
        } catch (e: Exception) {
            Log.e("TAG", "mai-dian------$eventName-----Exception:${e}")
        }
    }
    fun getIfConfig() {
        getServiceData(
            "https://api.myip.com/",
            onSuccess = {
                Log.e("TAG", "getIfConfig-onSuccess: $it")
                val jsonObject = JSONObject(it)
                DataUtils.ip_one = jsonObject.optString("country", "Unknown")
            },
            onError = {
                Log.e("TAG", "getIfConfig-onError: $it")
                getIf2Config { data ->
                    DataUtils.ip_two = data
                }
            }
        )
    }

    private fun getIf2Config(getResultFun: (String) -> Unit) {
        getServiceData(
            "https://api.infoip.io/",
            onSuccess = {
                Log.e("TAG", "getIfConfig2-onSuccess: $it")

                val jsonObject = JSONObject(it)
                getResultFun(jsonObject.optString("country_short", "Unknown"))
            },
            onError = {
                Log.e("TAG", "getIfConfig-onError: $it")
            }
        )
    }


    var appList: MutableList<AppInfo> = ArrayList()

    fun getAllLauncherIconPackages(context: Context) {
        val launcherIconPackageList: MutableList<AppInfo> = ArrayList()
        val intent = Intent()
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfos =
            context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
        for (info in resolveInfos) {
            val data = context.packageManager.getApplicationInfo(info.activityInfo.packageName, 0)
            if (info.activityInfo.packageName != context.packageName) {
                val appInfo = AppInfo()
                appInfo.isShow = false
                appInfo.name = context.packageManager.getApplicationLabel(data).toString()
                appInfo.packName = info.activityInfo.packageName
                appInfo.icon = context.packageManager.getApplicationIcon(data)
                launcherIconPackageList.add(appInfo)
            }
        }
        appList = launcherIconPackageList
            .asSequence()
            .sortedBy { it.name?.uppercase(Locale.getDefault()) }
            .toMutableList()
    }


    fun getAppListData(): MutableList<AppInfo> {
        return appList
    }

    fun getSavePackName(): MutableList<String>? {
        if (DataUtils.app_pack_name.isEmpty()) {
            return null
        }
        return DataUtils.app_pack_name.split(",").toMutableList()
    }

    fun setSavePackName(appInfo: AppInfo) {
        val bean = getSavePackName()
        var flag = false
        bean?.forEach {
            if (it == appInfo.packName) {
                flag = true
            }
        }
        if (appInfo.isCheck) {
            if (!flag ) {
                if(DataUtils.app_pack_name.isEmpty()){
                    DataUtils.app_pack_name = "${appInfo.packName}"
                }else{
                    DataUtils.app_pack_name = "${DataUtils.app_pack_name},${appInfo.packName}"
                }
            }

        } else {
            bean?.remove(appInfo.packName)
            DataUtils.app_pack_name = bean?.joinToString(separator = ",").toString()
        }
    }
}