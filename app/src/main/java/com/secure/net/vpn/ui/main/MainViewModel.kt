package com.secure.net.vpn.ui.main

import android.app.Activity
import android.content.Intent
import com.secure.net.vpn.base.BaseViewModel
import com.secure.net.vpn.data.DataUtils
import com.secure.net.vpn.ui.end.EndActivity
import com.secure.net.vpn.ui.home.HomeFragment
import java.util.Locale

class MainViewModel:BaseViewModel() {
    fun isIllegalIp(): Boolean {
        if (DataUtils.ip_one.isEmpty()) {
            return isIllegalIp2()
        }
        return DataUtils.ip_one == "IR" || DataUtils.ip_one == "CN" ||
                DataUtils.ip_one == "HK" || DataUtils.ip_one == "MO"
    }

    private fun isIllegalIp2(): Boolean {
        val ipData =  DataUtils.ip_two
        val locale = Locale.getDefault()
        val language = locale.language
        if (ipData.isEmpty()) {
            return language == "zh" || language == "fa"
        }
        return ipData == "IR" || ipData == "CN" ||
                ipData == "HK" || ipData == "MO"
    }
     fun showSwitching(activity: Activity) {
        val dialogVpn: androidx.appcompat.app.AlertDialog =
            androidx.appcompat.app.AlertDialog.Builder(activity)
                .setTitle("Tips")
                .setMessage("This software is no longer available for use in your region or country")
                .setCancelable(false)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    activity.finish()
                    android.os.Process.killProcess(android.os.Process.myPid())
                }.create()
        dialogVpn.setCancelable(false)
        dialogVpn.show()
    }


}