package com.secure.net.vpn.ui.proxy


import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.secure.net.vpn.R
import com.secure.net.vpn.app.SecureApp
import com.secure.net.vpn.base.BaseFragment
import com.secure.net.vpn.data.AppInfo
import com.secure.net.vpn.data.SecureUtils
import com.secure.net.vpn.data.ServerDetailBean
import com.secure.net.vpn.databinding.FragmentProxyBinding
import com.secure.net.vpn.ui.list.ListProxyAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class ProxyFragment  : BaseFragment<FragmentProxyBinding, ProxyViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_proxy
    override val viewModelClass: Class<ProxyViewModel>
        get() = ProxyViewModel::class.java
    private lateinit var allApp: MutableList<AppInfo>
    private lateinit var adapter: ProxyAppAdapter

    override fun initViews() {
        editFun()
        getAppListDataFun {
            initAllAdapter()
        }
    }

    override fun observeViewModel() {
        binding.tvAllOpen.setOnClickListener {
            allApp.forEach { all ->
                all.isCheck = true
                SecureUtils.setSavePackName(all)
            }
            adapter.notifyDataSetChanged()
        }
    }
    private fun initAllAdapter() {
        adapter = ProxyAppAdapter(allApp)
        binding.rvListApp.adapter = adapter
        binding.rvListApp.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        adapter.setOnItemClickListener(object : ProxyAppAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                setSwichApp(allApp[position])
            }
        })
    }
    private fun getAppListDataFun(nextFun:()->Unit){
        lifecycleScope.launch(Dispatchers.IO) {
            allApp = SecureUtils.getAppListData()
            val saveDataList = SecureUtils.getSavePackName()
            saveDataList?.forEach {
                allApp.forEach {pack->
                    if(pack.packName == it){
                        pack.isCheck =true
                    }
                }
            }
            nextFun()
        }
    }
    fun setSwichApp(appInfo: AppInfo){
        appInfo.isCheck = !appInfo.isCheck
        SecureUtils.setSavePackName(appInfo)
        adapter.notifyDataSetChanged()
    }

    private fun editFun(){
        binding.etSer.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                allApp.forEach { all ->
                    all.isShow = !all.name?.lowercase(Locale.getDefault())?.contains(s.toString()
                        .lowercase(Locale.getDefault()))!!
                }
                adapter.notifyDataSetChanged()
                showNoData()
            }
        })
    }
    fun showNoData() {
        var type = false
        allApp.forEach {
            if (!it.isShow) {
                type = true
            }
        }
        if (type) {
            binding.tvNoData.visibility = View.GONE
            binding.rvListApp.visibility = View.VISIBLE
        } else {
            binding.tvNoData.visibility = View.VISIBLE
            binding.rvListApp.visibility = View.GONE
        }
    }
}
