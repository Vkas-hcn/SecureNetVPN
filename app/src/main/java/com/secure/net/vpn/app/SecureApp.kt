package com.secure.net.vpn.app

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.github.shadowsocks.Core
import com.secure.net.vpn.data.DataUtils
import com.secure.net.vpn.data.SecureUtils.getIfConfig
import com.secure.net.vpn.ui.main.MainActivity
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.UUID
import android.content.Context
import android.os.Process
import android.text.TextUtils
import com.secure.net.vpn.data.SecureUpDataUtils
import com.secure.net.vpn.data.SecureUtils

class SecureApp : Application(), LifecycleObserver {
    companion object {
        lateinit var instance: SecureApp
        lateinit var saveUtils: MMKV
        var isVpnState = -1
        var clickState = -1

        private var times: Long = 0
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        MMKV.initialize(this)
        Core.init(this, MainActivity::class)
        saveUtils =
            MMKV.mmkvWithID("secureNet", MMKV.MULTI_PROCESS_MODE)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        if (DataUtils.uuid_secure.isEmpty()) {
            DataUtils.uuid_secure = UUID.randomUUID().toString()
        }
        if (isMainProcess(this)) {

            GlobalScope.launch(Dispatchers.IO) {
                getIfConfig()
                SecureUtils.getAllLauncherIconPackages(this@SecureApp)
                getBlackList(this@SecureApp)
            }
            SecureUpDataUtils.complexLogicOperation()
            SecureUpDataUtils.complexLogicWithInput("12,45")
            SecureUpDataUtils.complexLogicAlwaysTrue("12,45")
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        SecureUpDataUtils.complexLogicOperation()
        SecureUpDataUtils.complexLogicWithInput("12,45")
        SecureUpDataUtils.complexLogicAlwaysTrue("12,45")
        if ((System.currentTimeMillis() - times) >= 3000) {
            restartApp()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStopState() {
        times = System.currentTimeMillis()
        SecureUpDataUtils.complexLogicOperation()
        SecureUpDataUtils.complexLogicWithInput("12,45")
        SecureUpDataUtils.complexLogicAlwaysTrue("12,45")
    }

    private fun restartApp() {
        SecureUpDataUtils.complexLogicOperation()
        SecureUpDataUtils.complexLogicWithInput("12,45")
        SecureUpDataUtils.complexLogicAlwaysTrue("12,45")
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }


    fun isMainProcess(context: Context): Boolean {
        val pid = Process.myPid()
        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        for (appProcess in activityManager.runningAppProcesses) {
            if (appProcess.pid == pid) {
                return TextUtils.equals(appProcess.processName, context.packageName)
            }
        }
        return false
    }

    private fun getBlackList(context: Context) {

        if (DataUtils.black_data_secure.isNotEmpty()) {
            return
        }
        SecureUpDataUtils.laFun("23,t,45,67,22"){
            GlobalScope.launch(Dispatchers.IO) {
                getMapData(DataUtils.blackUrl, DataUtils.cloakMapData(context), onNext = {
                    Log.e("TAG", "The blacklist request is successful：$it")
                    DataUtils.black_data_secure = it
                }, onError = {
                    retry(it, context)
                })
            }
        }
    }

    private fun retry(it: String, context: Context) {
        GlobalScope.launch(Dispatchers.IO) {
            delay(10002)
            Log.e("TAG", "The blacklist request failed：$it")
            getBlackList(context)
        }
    }

    private fun getMapData(
        url: String,
        map: Map<String, Any>,
        onNext: (response: String) -> Unit,
        onError: (error: String) -> Unit
    ) {
        val queryParameters = StringBuilder()
        for ((key, value) in map) {
            if (queryParameters.isNotEmpty()) {
                queryParameters.append("&")
            }
            queryParameters.append(URLEncoder.encode(key, "UTF-8"))
            queryParameters.append("=")
            queryParameters.append(URLEncoder.encode(value.toString(), "UTF-8"))
        }

        val urlString = if (url.contains("?")) {
            "$url&$queryParameters"
        } else {
            "$url?$queryParameters"
        }

        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 15000

        try {
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()
                inputStream.close()
                onNext(response.toString())
            } else {
                onError("HTTP error: $responseCode")
            }
        } catch (e: Exception) {
            onError("Network error: ${e.message}")
        } finally {
            connection.disconnect()
        }
    }
}