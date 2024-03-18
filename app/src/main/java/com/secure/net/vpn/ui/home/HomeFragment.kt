package com.secure.net.vpn.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope

import androidx.preference.PreferenceDataStore
import com.github.shadowsocks.Core
import com.github.shadowsocks.aidl.IShadowsocksService
import com.github.shadowsocks.aidl.ShadowsocksConnection
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.database.Profile
import com.github.shadowsocks.database.ProfileManager
import com.github.shadowsocks.preference.OnPreferenceDataStoreChangeListener
import com.github.shadowsocks.utils.Key
import com.secure.net.vpn.R
import com.secure.net.vpn.app.SecureApp
import com.secure.net.vpn.base.BaseFragment
import com.secure.net.vpn.databinding.FragmentHomeBinding
import com.github.shadowsocks.preference.DataStore
import com.github.shadowsocks.utils.StartService
import com.google.gson.Gson
import com.secure.net.vpn.data.DataUtils
import com.secure.net.vpn.data.SecureUtils
import com.secure.net.vpn.data.SecureUtils.getCountryImage
import com.secure.net.vpn.data.ServerDetailBean
import com.secure.net.vpn.ui.end.EndActivity
import com.secure.net.vpn.ui.main.MainActivity
import com.secure.net.vpn.ui.main.MainActivity.Companion.stateListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(),
    ShadowsocksConnection.Callback,
    OnPreferenceDataStoreChangeListener {
    override val layoutId: Int
        get() = R.layout.fragment_home
    override val viewModelClass: Class<HomeViewModel>
        get() = HomeViewModel::class.java

    private var speedJob: Job? = null
    private lateinit var mainActivity :MainActivity
    override fun initViews() {
        mainActivity = activity as MainActivity
        initializeServerData()
        parentFragmentManager.setFragmentResultListener("list_back", this) { _, bundle ->
            val result = bundle.getString("resultKey")
            if (result?.isNotEmpty() == true) {
                if (DataUtils.clickServer != "" && SecureApp.isVpnState != 2) {
                    DataUtils.currentlyServer = DataUtils.clickServer
                    updateSkServer()
                    setServiceUi(SecureUtils.getNowVpnBean())
                    DataUtils.clickServer = ""
                    clickVpnButton()
                }
                if (DataUtils.clickServer != "" && SecureApp.isVpnState == 2) {
                    updateSkServer()
                    clickVpnButton()
                }
            }
        }
    }

    private fun initializeServerData() {
        val bestData = SecureUtils.getNowVpnBean()
        ProfileManager.getProfile(DataStore.profileId).let {
            if (it != null) {
                ProfileManager.updateProfile(viewModel.setSkServerData(it, bestData))
            } else {
                val profile = Profile()
                ProfileManager.createProfile(viewModel.setSkServerData(profile, bestData))
            }
        }
        bestData.smartService = 1
        DataStore.profileId = 1L
        SecureUtils.setNowVpnBean(bestData)
        setServiceUi(bestData)
    }

    @SuppressLint("SetTextI18n")
    fun setServiceUi(bean: ServerDetailBean) {
        val name = if (bean.OTLFDAAE.isEmpty()) {
            "Faster Server"
        } else {
            bean.OTLFDAAE + "," + bean.Byv
        }
        binding.tvServiceName.text = name
        binding.tvServiceIp.text = "IP:" + bean.XVQxEerTo
        binding.icFlag.setImageResource(bean.OTLFDAAE.getCountryImage())
    }

    private fun updateSkServer() {
        val nowBean = SecureUtils.getSelectVpnServiceData()
        ProfileManager.getProfile(DataStore.profileId).let {
            if (it != null) {
                ProfileManager.updateProfile(viewModel.setSkServerData(it, nowBean))
            } else {
                ProfileManager.createProfile(Profile())
            }
        }
        DataStore.profileId = 1L
    }

    override fun observeViewModel() {
        binding.showGuide = !viewModel.isConnectFun()
        mainActivity.binding.showGuide = !viewModel.isConnectFun()
        binding.viewGuide.setOnClickListener {

        }
        binding.lavGuide.setOnClickListener {
            binding.showGuide = false
            mainActivity.binding.showGuide = false
            clickVpnButton()
        }
        binding.aivVpn.setOnClickListener {
            clickVpnButton()
        }
        viewModel.jumpToEnd.observe(this) {
            if (it) {
                mainActivity.jumTOEnd()
            }
        }
    }

    private fun clickVpnButton() {
        binding.showGuide = false
        mainActivity.binding.showGuide = false
        lifecycleScope.launch(Dispatchers.Main) {
            val state = SecureUtils.isHaveServeData()
            binding.proBarLoad.visibility = View.VISIBLE
            if (state) {
                setServiceUi(SecureUtils.getNowVpnBean())
                binding.proBarLoad.visibility = View.GONE
                mainActivity.connect.launch(null)
            } else {
                delay(2000)
                binding.proBarLoad.visibility = View.GONE
                initializeServerData()
            }
        }
    }

    private fun connectSuccessFun() {
        if (SecureApp.isVpnState != -1) {
            viewModel.jumpToEnd.postValue(true)
        }
        SecureApp.isVpnState = 2
        speedNumFun()
    }

    private fun connectingFun() {
        binding.showConnect = true
        SecureApp.isVpnState = 1
        vpnStateUi()
    }

    private fun disConnectSuccessFun() {
        if (SecureApp.isVpnState != -1) {
            viewModel.jumpToEnd.postValue(true)
        }
        SecureApp.isVpnState = 0
    }

    fun connectFail() {
        if (SecureApp.isVpnState != 2) {
            lifecycleScope.launch(Dispatchers.Main) {
                Toast.makeText(activity, "Connection failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun connectVpnFun() {
        SecureApp.clickState = SecureApp.isVpnState
        lifecycleScope.launch {
            connectingFun()
            delay(2000)
            Log.e("TAG", "connectVpnFun: ${SecureApp.clickState}")
            if (SecureApp.clickState != -1) {
                if (SecureApp.clickState == 2) {
                    Core.stopService()
                } else {
                    SecureUtils.setNowVpnBean(SecureUtils.getSelectVpnServiceData())
                    Core.startService()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (SecureApp.clickState != -1) {
            binding.showGuide = false
            mainActivity.binding.showGuide = false

        }
    }

    fun stopDisConnect() {
        SecureApp.clickState = -1
        SecureApp.isVpnState = 2
        vpnStateUi()
    }

    override fun onStop() {
        super.onStop()
        if (isDisConnect()) {
            stopDisConnect()
        }
        if (isConnect()) {
            SecureApp.clickState = -1
            SecureApp.isVpnState = 0
            vpnStateUi()
        }
    }

    fun isDisConnect(): Boolean {
        return SecureApp.isVpnState == 1 && SecureApp.clickState == 2
    }

    fun isConnect(): Boolean {
        return SecureApp.isVpnState == 1 && (SecureApp.clickState == 0 || SecureApp.clickState == -1)
    }

    override fun stateChanged(state: BaseService.State, profileName: String?, msg: String?) {
        SecureApp.isVpnState = if (state.canStop) {
            2
        } else {
            0
        }
        if (viewModel.isConnectFun()) {
            connectSuccessFun()
        } else {
            disConnectSuccessFun()
        }
        vpnStateUi()
    }

    override fun onServiceConnected(service: IShadowsocksService) {
        val state = BaseService.State.values()[service.state]
        SecureApp.isVpnState = if (state.canStop) {
            2
        } else {
            0
        }
        vpnStateUi()
    }

    override fun onPreferenceDataStoreChanged(store: PreferenceDataStore, key: String) {
        when (key) {
            Key.serviceMode -> {
                mainActivity.connection.disconnect(mainActivity)
                mainActivity.connection.connect(activity as MainActivity, this)
            }
        }
    }

    private fun vpnStateUi() {
        when (SecureApp.isVpnState) {
            -1, 0 -> {
                binding.tvState.text = "Disconnect"
                binding.aivVpn.setImageResource(R.drawable.ic_vpn_connect)
                binding.showConnect = false
            }

            1 -> {
                if (SecureApp.clickState == 2) {
                    binding.tvState.text = "Disconnecting"
                } else {
                    binding.tvState.text = "Connecting"
                }
                binding.showConnect = true

            }

            2 -> {
                binding.tvState.text = "Connected"
                binding.aivVpn.setImageResource(R.drawable.ic_vpn_connect_end)
                binding.showConnect = false
            }
        }
    }

    fun speedNumFun() {
        speedJob?.cancel()
        speedJob = null
        speedJob = lifecycleScope.launch {
            while (isActive) {
                binding.tvUp.text = SecureApp.saveUtils.decodeString("secure_up", "0.00b/s")
                binding.tvDow.text = SecureApp.saveUtils.decodeString("secure_dow", "0.00b/s")
                delay(500)
            }
        }
    }

}
