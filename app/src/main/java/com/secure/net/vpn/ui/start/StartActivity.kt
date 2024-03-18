package com.secure.net.vpn.ui.start

import com.secure.net.vpn.R
import com.secure.net.vpn.base.BaseActivity
import com.secure.net.vpn.data.SecureUtils
import com.secure.net.vpn.databinding.ActivityStartBinding
import com.secure.net.vpn.ui.main.MainActivity

class StartActivity : BaseActivity<ActivityStartBinding, StartViewModel>() {
    override val layoutId: Int
        get() = R.layout.activity_start
    override val viewModelClass: Class<StartViewModel>
        get() = StartViewModel::class.java


    override fun initViews() {
        SecureUtils.getVpnNetData()
        viewModel.startCountdown({
            binding.proStart.progress = it
        }, {
            binding.proStart.progress = 100
            viewModel.jumpToMain.postValue(true)
        })
    }

    override fun observeViewModel() {
        viewModel.jumpToMain.observe(this) {
            if(it){
                navigateTo(MainActivity::class.java)
                finish()
            }
        }
    }

}
