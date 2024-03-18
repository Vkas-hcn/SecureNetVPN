package com.secure.net.vpn.data

import android.graphics.drawable.Drawable

data class SecureVpnBean(
    val code: Int,
    val `data`: Data,
    val msg: String
)

data class Data(
    val zpXcuZ: List<ServerDetailBean>,
    val xqXlJgMTB: List<ServerDetailBean>
)

data class ServerDetailBean(
    var Byv: String,
    var OTLFDAAE: String,
    var XVQxEerTo: String,
    var dBH: String,
    var nRz: Int,
    var DuHLr: String,
    var zrrXO: String,
    var TUsVxJPE: String,
    var smartService: Int,
    var isShow: Boolean = false,
)

data class AppInfo(
    var name: String? = null,
    var icon: Drawable? = null,
    var packName: String? = null,
    var isShow: Boolean = false,
    var isCheck: Boolean = false
)

