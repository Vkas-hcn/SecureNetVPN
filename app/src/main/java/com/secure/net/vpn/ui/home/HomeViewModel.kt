package com.secure.net.vpn.ui.home

import androidx.lifecycle.MutableLiveData
import com.github.shadowsocks.database.Profile
import com.secure.net.vpn.app.SecureApp
import com.secure.net.vpn.base.BaseViewModel
import com.secure.net.vpn.data.SecureVpnBean
import com.secure.net.vpn.data.ServerDetailBean
import com.secure.net.vpn.ui.home.HomeFragment

class HomeViewModel : BaseViewModel() {
    var jumpToEnd = MutableLiveData(false)
    fun isConnectFun(): Boolean {
        return SecureApp.isVpnState == 2
    }

    fun setSkServerData(profile: Profile, bestData: ServerDetailBean): Profile {
        profile.name = bestData.OTLFDAAE + "-" + bestData.Byv
        profile.host = bestData.XVQxEerTo
        profile.password = bestData.zrrXO
        profile.method = bestData.TUsVxJPE
        profile.remotePort = bestData.nRz
        return profile
    }
}