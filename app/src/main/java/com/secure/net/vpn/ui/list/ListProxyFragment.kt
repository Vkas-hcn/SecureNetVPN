package com.secure.net.vpn.ui.list


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.secure.net.vpn.R
import com.secure.net.vpn.app.SecureApp
import com.secure.net.vpn.base.BaseFragment
import com.secure.net.vpn.data.SecureUtils
import com.secure.net.vpn.data.SecureUtils.getAllVpnListData
import com.secure.net.vpn.data.SecureUtils.getCountryImage
import com.secure.net.vpn.data.ServerDetailBean
import com.secure.net.vpn.databinding.FragmentListBinding
import com.secure.net.vpn.ui.main.MainActivity
import java.util.Locale

class ListProxyFragment : BaseFragment<FragmentListBinding, ListProxyViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_list
    override val viewModelClass: Class<ListProxyViewModel>
        get() = ListProxyViewModel::class.java
    private lateinit var allService: MutableList<ServerDetailBean>
    private lateinit var adapter: ListProxyAdapter
    override fun initViews() {
        initAllAdapter()
        editFun()
        if (SecureUtils.isHaveServeData()) {
            binding.tvNoData.visibility = View.GONE
            binding.rvList.visibility = View.VISIBLE
        } else {
            binding.tvNoData.visibility = View.VISIBLE
            binding.rvList.visibility = View.GONE
        }
    }

    override fun observeViewModel() {
    }

    private fun sendDataBack(data: String) {
        val result = Bundle().apply {
            putString("resultKey", data)
        }
        parentFragmentManager.setFragmentResult("list_back", result)
    }

    private fun setEndUi() {
        val bean = SecureUtils.getSelectVpnServiceData()
        binding.imgServiceTop.setImageResource(bean.OTLFDAAE.getCountryImage())
        val name = if (bean.OTLFDAAE.isEmpty()) {
            "Faster Server"
        } else {
            bean.OTLFDAAE
        }
        binding.tvEndName.text = name
        if (SecureApp.isVpnState == 2) {
            binding.imgType.visibility = View.VISIBLE
        } else {
            binding.imgType.visibility = View.GONE
        }
    }

    private fun initAllAdapter() {
        allService = getAllVpnListData()
        adapter = ListProxyAdapter(allService)
        binding.rvList.adapter = adapter
        binding.rvList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        adapter.setOnItemClickListener(object : ListProxyAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                clickToHome(allService[position])
            }
        })
    }

    fun clickToHome(bean: ServerDetailBean) {
        val nowBean = SecureUtils.getNowVpnBean()
        if (SecureApp.isVpnState != 2) {
            SecureUtils.setSelectVpnServiceData(bean)
            sendDataBack("connect")
            (activity as MainActivity).switchFragment((activity as MainActivity).homeFragment)
            (activity as MainActivity).selectIdFun((activity as MainActivity).homeFragment)
        }
        if (SecureApp.isVpnState == 2 && bean.XVQxEerTo != nowBean.XVQxEerTo) {
            viewModel.showSwitching(activity as MainActivity) {
                SecureUtils.setSelectVpnServiceData(bean)
                sendDataBack("disconnect")
                (activity as MainActivity).switchFragment((activity as MainActivity).homeFragment)
                (activity as MainActivity).selectIdFun((activity as MainActivity).homeFragment)
            }
        }
        if (SecureApp.isVpnState == 2 && (bean.XVQxEerTo == nowBean.XVQxEerTo && bean.smartService != nowBean.smartService)) {
            viewModel.showSwitching(activity as MainActivity) {
                SecureUtils.setSelectVpnServiceData(bean)
                sendDataBack("disconnect")
                (activity as MainActivity).switchFragment((activity as MainActivity).homeFragment)
                (activity as MainActivity).selectIdFun((activity as MainActivity).homeFragment)
            }
        }
    }

    fun onResumeEndFun() {
        if (SecureUtils.isHaveServeData()) {
            allService = getAllVpnListData()
            adapter.setAllData(allService)
            setEndUi()
            binding.tvNoData.visibility = View.GONE
            binding.rvList.visibility = View.VISIBLE
        } else {
            binding.tvNoData.visibility = View.VISIBLE
            binding.rvList.visibility = View.GONE
        }
    }

    private fun editFun() {
        binding.etSer.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                allService.forEach { all ->
                    all.isShow = !(all.OTLFDAAE + all.Byv).lowercase(Locale.getDefault()).contains(
                        s.toString()
                            .lowercase(Locale.getDefault())
                    )
                }
                adapter.setSerAllData(allService)
                showNoData()
            }
        })
    }

    fun showNoData() {
        var type = false
        allService.forEach {
            if (!it.isShow) {
                type = true
            }
        }
        if (type) {
            binding.tvNoData.visibility = View.GONE
            binding.rvList.visibility = View.VISIBLE
        } else {
            binding.tvNoData.visibility = View.VISIBLE
            binding.rvList.visibility = View.GONE
        }
    }
}
