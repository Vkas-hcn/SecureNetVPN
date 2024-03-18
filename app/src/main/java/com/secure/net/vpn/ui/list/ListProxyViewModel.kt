package com.secure.net.vpn.ui.list

import android.app.Activity
import com.secure.net.vpn.base.BaseViewModel
import com.secure.net.vpn.ui.home.HomeFragment

class ListProxyViewModel : BaseViewModel() {

    fun showSwitching(activity: Activity,okNextFun:()->Unit) {
        val dialogVpn: androidx.appcompat.app.AlertDialog =
            androidx.appcompat.app.AlertDialog.Builder(activity)
                .setTitle("Tips")
                .setMessage("Whether to disconnect the current connection?")
                .setCancelable(false)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    okNextFun()
                }.setNegativeButton("CANCEL") { dialog, _ ->
                    dialog.dismiss()
                }.create()
        dialogVpn.setCancelable(false)
        dialogVpn.show()
    }
}