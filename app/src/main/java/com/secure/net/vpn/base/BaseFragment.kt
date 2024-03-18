package com.secure.net.vpn.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

abstract class BaseFragment<DB : ViewDataBinding, VM : BaseViewModel> : Fragment() {

    lateinit var binding: DB
    lateinit var viewModel: VM

    abstract val layoutId: Int
    abstract val viewModelClass: Class<VM>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        viewModel = ViewModelProvider(this).get(viewModelClass)
        binding.lifecycleOwner = viewLifecycleOwner
        initViews()
        observeViewModel()
        return binding.root
    }

    abstract fun initViews()

    abstract fun observeViewModel()
    protected fun navigateTo(clazz: Class<*>) {
        val intent = Intent(activity, clazz)
        startActivity(intent)
    }

    protected fun navigateToWithArgs(clazz: Class<*>, args: Bundle) {
        val intent = Intent(activity, clazz)
        intent.putExtras(args)
        startActivity(intent)
    }
    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun log(message: String) {
    }
}
