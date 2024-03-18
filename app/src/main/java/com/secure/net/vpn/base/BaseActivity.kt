package com.secure.net.vpn.base

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider

abstract class BaseActivity<DB : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity() {

    lateinit var binding: DB
    lateinit var viewModel: VM

    abstract val layoutId: Int
    abstract val viewModelClass: Class<VM>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        viewModel = ViewModelProvider(this).get(viewModelClass)
        binding.lifecycleOwner = this
        initViews()
        observeViewModel()
    }

    abstract fun initViews()

    abstract fun observeViewModel()

    protected fun navigateTo(clazz: Class<*>) {
        val intent = Intent(this, clazz)
        startActivity(intent)
    }

    protected fun navigateToWithArgs(clazz: Class<*>, args: Bundle) {
        val intent = Intent(this, clazz)
        intent.putExtras(args)
        startActivity(intent)
    }
    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun log(message: String) {
    }
}
