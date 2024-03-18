package com.secure.net.vpn.ui.end

import com.secure.net.vpn.R
import com.secure.net.vpn.app.SecureApp
import com.secure.net.vpn.base.BaseActivity
import com.secure.net.vpn.databinding.ActivityEndBinding

class EndActivity : BaseActivity<ActivityEndBinding, EndViewModel>() {
    override val layoutId: Int
        get() = R.layout.activity_end
    override val viewModelClass: Class<EndViewModel>
        get() = EndViewModel::class.java

    override fun initViews() {
        if (SecureApp.isVpnState == 2) {
            binding.tvState.text = "Connection succeed"
            binding.tvTip.text = "You are very safe right now!"
            binding.imgConnect.setImageResource(R.drawable.ic_connect_end)
        } else {
            binding.tvState.text = "Disconnection succeed"
            binding.tvTip.text = "You have exposed in danger!"
            binding.imgConnect.setImageResource(R.drawable.ic_disconnect_end)
        }
    }

    override fun observeViewModel() {
        binding.imgBack.setOnClickListener {
            finish()
        }
    }
}