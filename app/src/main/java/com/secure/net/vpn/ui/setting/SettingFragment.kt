package com.secure.net.vpn.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.secure.net.vpn.R
import com.secure.net.vpn.base.BaseFragment
import com.secure.net.vpn.databinding.FragmentHomeBinding
import com.secure.net.vpn.databinding.FragmentSettingBinding
import com.secure.net.vpn.ui.home.HomeViewModel
import com.secure.net.vpn.ui.web.ProActivity

class SettingFragment  : BaseFragment<FragmentSettingBinding, HomeViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_setting
    override val viewModelClass: Class<HomeViewModel>
        get() = HomeViewModel::class.java

    override fun initViews() {
        binding.llPp.setOnClickListener {
            navigateTo(ProActivity::class.java)
        }
    }

    override fun observeViewModel() {
    }
}
