package com.secure.net.vpn.ui.web

import com.secure.net.vpn.R
import com.secure.net.vpn.base.BaseActivity
import com.secure.net.vpn.data.DataUtils
import com.secure.net.vpn.databinding.ActivityWebProBinding
import com.secure.net.vpn.ui.start.StartViewModel

class ProActivity : BaseActivity<ActivityWebProBinding, ProViewModel>() {
    override val layoutId: Int
        get() = R.layout.activity_web_pro
    override val viewModelClass: Class<ProViewModel>
        get() = ProViewModel::class.java


    override fun initViews() {
        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.wvState.loadUrl(DataUtils.agreementUrl)
        binding.wvState.webViewClient = object : android.webkit.WebViewClient() {
            override fun shouldOverrideUrlLoading(view: android.webkit.WebView?, url: String?): Boolean {
                view?.loadUrl(url!!)
                return true
            }
        }
    }

    override fun observeViewModel() {

    }
}