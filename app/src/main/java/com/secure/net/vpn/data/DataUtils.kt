package com.secure.net.vpn.data

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Build
import android.provider.Settings
import android.webkit.WebSettings
import com.secure.net.vpn.app.SecureApp
import com.secure.net.vpn.app.SecureApp.Companion.saveUtils
import com.secure.net.vpn.data.DataUtils.getAppVersion
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Locale
import java.util.UUID
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

interface Callback {
    fun onSuccess(result: String)
    fun onFailure(error: String)
}
object DataUtils {
    const val tab_url = "https://test-roth.secureconnecteasy.com/ys/berkeley"
    const val agreementUrl = "https://www.baidu.com"
    const val blackUrl = "https://haines.secureconnecteasy.com/gotham/surmise"
    const val vpn_service_url = "https://test.secureconnecteasy.com/RRTdGGy/VgIaxWTy/"
    var currentlyServer = ""
        set(value) {
            saveUtils.encode("CurrentlyServer", value)
            field = value
        }
        get() = saveUtils.decodeString("CurrentlyServer", "") ?: ""
    var clickServer = ""
        set(value) {
            saveUtils.encode("clickServer", value)
            field = value
        }
        get() = saveUtils.decodeString("clickServer", "") ?: ""
    var service_string = ""
        set(value) {
            saveUtils.encode("service_string", value)
            field = value
        }
        get() = saveUtils.decodeString("service_string", "") ?: ""

    var uuid_secure = ""
        set(value) {
            saveUtils.encode("uuid_secure", value)
            field = value
        }
        get() = saveUtils.decodeString("uuid_secure", "") ?: ""

    var black_data_secure = ""
        set(value) {
            saveUtils.encode("black_data_secure", value)
            field = value
        }
        get() = saveUtils.decodeString("black_data_secure", "") ?: ""
    var ip_one = ""
        set(value) {
            saveUtils.encode("ip_one", value)
            field = value
        }
        get() = saveUtils.decodeString("ip_one", "") ?: ""
    var ip_two = ""
        set(value) {
            saveUtils.encode("ip_two", value)
            field = value
        }
        get() = saveUtils.decodeString("ip_two", "") ?: ""

    var app_pack_name= ""
        set(value) {
            saveUtils.encode("app_pack_name", value)
            field = value
        }
        get() = saveUtils.decodeString("app_pack_name", "") ?: ""
    fun cloakMapData(context: Context): Map<String, Any> {
        return mapOf<String, Any>(
            //distinct_id
            "lester" to (uuid_secure),
            //client_ts
            "leisure" to (System.currentTimeMillis()),
            //device_model
            "clamber" to Build.MODEL,
            //bundle_id
            "although" to ("com.secure.connect.easy.privacy.surf"),
            //os_version
            "loosen" to Build.VERSION.RELEASE,
            //gaid
            "forbore" to "",
            //android_id
            "multics" to context.getAppId(),
            //os
            "casual" to "panacea",
            //app_version
            "twigging" to context.getAppVersion(),
        )
    }

    private fun Context.getAppVersion(): String {
        try {
            val packageInfo = this.packageManager.getPackageInfo(this.packageName, 0)

            return packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return ""
    }

    private fun Context.getAppId(): String {
        return Settings.Secure.getString(
            this.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }
    fun getServiceData(url: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        Thread {
            try {

                val urlObj = URL(url)
                val conn = urlObj.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                val customHeaders = mapOf(
                    "QKEJ" to "ZZ",
                    "WXTP" to SecureApp.instance.packageName,
                )
                for ((key, value) in customHeaders) {
                    conn.setRequestProperty(key, value)
                }
                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = conn.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val response = reader.readText()
                    reader.close()
                    onSuccess(response)
                } else {
                    onError("Error from server: $responseCode")
                }
            } catch (e: Exception) {
                onError("Network error or other exception: ${e.message}")
            }
        }.start()
    }


    fun post(url: String, body: Any, callback: Callback) {
        Thread {
            var connection: HttpURLConnection? = null
            try {
                val urlConnection = URL(url).openConnection() as HttpURLConnection
                connection = urlConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                connection.setRequestProperty("Accept", "application/json")
                connection.doOutput = true
                connection.doInput = true

                // Write body to the request
                BufferedWriter(OutputStreamWriter(connection.outputStream, "UTF-8")).use { writer ->
                    writer.write(body.toString())
                    writer.flush()
                }

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val responseBody = connection.inputStream.bufferedReader().use { it.readText() }
                    callback.onSuccess(responseBody)
                } else {
                    val errorBody = connection.errorStream.bufferedReader().use { it.readText() }
                    callback.onFailure(errorBody)
                }
            } catch (e: IOException) {
                callback.onFailure("Network error: ${e.message}")
            } finally {
                connection?.disconnect()
            }
        }.start()
    }


    /**
     * 顶层json
     */
    private fun getTopLevelJsonData(context: Context): JSONObject {
        val jsonData = JSONObject()

        jsonData.apply {
            //bundle_id
            put("although", "com.secure.connect.easy.privacy.surf")
            //os_version
            put("loosen", Build.VERSION.RELEASE)
            //app_version
            put("twigging", context.getAppVersion())
            //os
            put("casual", "panacea")
            //android_id
            put("multics", context.getAppId())
            //system_language
            put(
                "tate",
                "${Locale.getDefault().language}_${Locale.getDefault().country}"
            )
            //client_ts
            put("leisure", System.currentTimeMillis())
            //distinct_id
            put("lester", uuid_secure)
            //device_model
            put("clamber", Build.MODEL)
            //manufacturer
            put("acrimony", Build.MANUFACTURER.toLowerCase())
            //operator
            put(
                "robotics",
                ""
            )
            //log_id
            put("anaphora", UUID.randomUUID().toString())
        }
        return jsonData
    }
    fun getTbaDataJson(context: Context, name: String): String {
        return getTopLevelJsonData(context).apply {
            put("inflater", name)
        }.toString()
    }

    fun getTbaTimeDataJson(
        context: Context,
        time: Any,
        name: String,
        parameterName: String
    ): String {
        val data = JSONObject()
        data.put(parameterName, time)
        return getTopLevelJsonData(context).apply {
            put("inflater", name)
            put("tolstoy^$parameterName",time)
        }.toString()
    }
}