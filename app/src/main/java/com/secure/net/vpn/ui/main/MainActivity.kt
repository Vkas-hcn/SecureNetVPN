package com.secure.net.vpn.ui.main

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.shadowsocks.aidl.ShadowsocksConnection
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.preference.DataStore
import com.github.shadowsocks.utils.StartService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.secure.net.vpn.R
import com.secure.net.vpn.app.SecureApp
import com.secure.net.vpn.base.BaseActivity
import com.secure.net.vpn.data.DataUtils
import com.secure.net.vpn.data.SecureUtils
import com.secure.net.vpn.databinding.ActivityMainBinding
import com.secure.net.vpn.ui.end.EndActivity
import com.secure.net.vpn.ui.home.HomeFragment
import com.secure.net.vpn.ui.list.ListProxyFragment
import com.secure.net.vpn.ui.proxy.ProxyFragment
import com.secure.net.vpn.ui.setting.SettingFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    override val layoutId: Int
        get() = R.layout.activity_main
    override val viewModelClass: Class<MainViewModel>
        get() = MainViewModel::class.java
    val homeFragment = HomeFragment()
    val listFragment = ListProxyFragment()
    val proxyFragment = ProxyFragment()
    val settingFragment = SettingFragment()
    val connection = ShadowsocksConnection(true)
    var jumpType = false

    companion object {
        var stateListener: ((BaseService.State) -> Unit)? = null
    }

    override fun initViews() {
        stateListener?.invoke(BaseService.State.Idle)
        connection.connect(this, homeFragment)
        DataStore.publicStore.registerChangeListener(homeFragment)

        if(viewModel.isIllegalIp()){
            viewModel.showSwitching(this)
        }
        onBackPressedDispatcher.addCallback(this) {
            if(homeFragment.isHidden){
                resetMenuIcons()
                binding.icHome.setImageResource(R.drawable.ic_home_1)
                switchFragment(homeFragment)
                return@addCallback
            }
            if (homeFragment.binding.showGuide == true) {
                homeFragment.binding.showGuide = false
               binding.showGuide = false
                return@addCallback
            }
            if (homeFragment.isConnect()) {
                Toast.makeText(
                    this@MainActivity,
                    "Unable to operate during connection process!",
                    Toast.LENGTH_SHORT
                ).show()
                return@addCallback
            }
            if (homeFragment.isDisConnect()) {
                homeFragment.stopDisConnect()
                return@addCallback
            }
            finish()
        }
    }

    val connect = registerForActivityResult(StartService()) {
        if (it) {
            Toast.makeText(this, "No permission", Toast.LENGTH_SHORT).show()
        } else {
            if (SecureUtils.isHaveNetWork(SecureApp.instance)) {
                homeFragment.connectVpnFun()
            } else {
                Toast.makeText(this, "No network", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun observeViewModel() {
        binding.viewGuideMain.setOnClickListener {  }
        binding.icHome.setOnClickListener {
            checkConnectFun {
                resetMenuIcons()
                binding.icHome.setImageResource(R.drawable.ic_home_1)
                switchFragment(homeFragment)
            }
        }
        binding.icList.setOnClickListener {
            checkConnectFun {
                resetMenuIcons()
                binding.icList.setImageResource(R.drawable.ic_sq_1)
                switchFragment(listFragment)
            }
        }
        binding.icProxy.setOnClickListener {
            checkConnectFun {
                resetMenuIcons()
                binding.icProxy.setImageResource(R.drawable.ic_pro_1)
                switchFragment(proxyFragment)
            }
        }
        binding.icSetting.setOnClickListener {
            checkConnectFun {
                resetMenuIcons()
                binding.icSetting.setImageResource(R.drawable.ic_set_1)
                switchFragment(settingFragment)
            }
        }
        switchFragment(homeFragment)
    }

    private fun checkConnectFun(nextFun: () -> Unit) {
        if (homeFragment.isConnect()) {
            Toast.makeText(
                this,
                "Unable to operate during connection process!",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            nextFun()
        }
    }

    fun switchFragment(showFragment: Fragment) {
        if (homeFragment.isDisConnect()) {
            homeFragment.stopDisConnect()
        }
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        for (fragment in fragmentManager.fragments) {
            fragmentTransaction.hide(fragment)
        }
        if (!showFragment.isAdded) {
            fragmentTransaction.add(R.id.fragment_container, showFragment)
        }
        fragmentTransaction.show(showFragment)
        fragmentTransaction.commitNow()
        if (showFragment is ListProxyFragment) {
            lifecycleScope.launch {
                delay(300)
                listFragment.onResumeEndFun()
            }
        }
    }

    private fun resetMenuIcons() {
        binding.icHome.setImageResource(R.drawable.ic_home_2)
        binding.icList.setImageResource(R.drawable.ic_sq_2)
        binding.icProxy.setImageResource(R.drawable.ic_pro_2)
        binding.icSetting.setImageResource(R.drawable.ic_set_2)
    }

    fun selectIdFun(fragment: Fragment) {
        resetMenuIcons()
        when (fragment) {
            is HomeFragment -> {
                binding.icHome.setImageResource(R.drawable.ic_home_1)
            }

            is ListProxyFragment -> {
                binding.icHome.setImageResource(R.drawable.ic_sq_1)
            }

            is ProxyFragment -> {
                binding.icHome.setImageResource(R.drawable.ic_pro_1)

            }

            is SettingFragment -> {
                binding.icHome.setImageResource(R.drawable.ic_set_1)
            }

            else -> {
                binding.icHome.setImageResource(R.drawable.ic_home_1)
            }
        }

    }

    override fun onStop() {
        super.onStop()
        connection.bandwidthTimeout = 0
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        DataStore.publicStore.unregisterChangeListener(homeFragment)
        connection.disconnect(this)
        jumpType = false
    }

    fun jumTOEnd() {
        if (!jumpType) {
            val intent = Intent(this, EndActivity::class.java)
            startActivityForResult(intent, 0x789)
            jumpType = true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0x789) {
            jumpType = false
        }
        if (requestCode == 0x789 && DataUtils.clickServer != "" && SecureApp.isVpnState != 2) {
            DataUtils.currentlyServer = DataUtils.clickServer
            homeFragment.setServiceUi(SecureUtils.getNowVpnBean())
            DataUtils.clickServer = ""
        }
    }

}
