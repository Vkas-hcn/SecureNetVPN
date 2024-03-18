package com.secure.net.vpn.ui.list

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.secure.net.vpn.R
import com.secure.net.vpn.app.SecureApp
import com.secure.net.vpn.data.SecureUtils
import com.secure.net.vpn.data.SecureUtils.getCountryImage
import com.secure.net.vpn.data.ServerDetailBean

class ListProxyAdapter(private val dataList: MutableList<ServerDetailBean>) :
    RecyclerView.Adapter<ListProxyAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_country)
        var aivFlag: ImageView = itemView.findViewById(R.id.aiv_flag)
        var imgSmart: ImageView = itemView.findViewById(R.id.img_smart)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(position)
                }
            }
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    private fun onItemClick(position: Int) {
        onItemClickListener?.onItemClick(position)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var onItemClickListener: OnItemClickListener? = null


    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = dataList[position]
        setVisibility(item.isShow, holder.itemView)
        val nowService = SecureUtils.getNowVpnBean()
        if (item.smartService == 1) {
            holder.tvName.text = "Faster Server"
            holder.aivFlag.setImageResource(R.drawable.ic_fast)
        } else {
            holder.tvName.text = String.format(item.OTLFDAAE + "," + item.Byv)
            holder.aivFlag.setImageResource(item.OTLFDAAE.getCountryImage())
        }
        if (item.XVQxEerTo == nowService.XVQxEerTo && item.smartService == nowService.smartService && SecureApp.isVpnState == 2) {
            holder.imgSmart.setImageResource(R.drawable.ic_ch_2)
        }else{
            holder.imgSmart.setImageResource(R.drawable.ic_ch_1)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context: Context = parent.context
        val inflater = LayoutInflater.from(context)
        val itemView: View = inflater.inflate(R.layout.layout_service, parent, false)
        return ViewHolder(itemView)
    }

    fun setAllData(datas: MutableList<ServerDetailBean>) {
        if (dataList.size != datas.size) {
            dataList.removeAll(dataList)
            dataList.addAll(datas)
        }
        notifyDataSetChanged()
    }

    fun setSerAllData(datas: MutableList<ServerDetailBean>) {
        dataList.removeAll(dataList)
        dataList.addAll(datas)
        notifyDataSetChanged()
    }

    private fun setVisibility(isVisible: Boolean, itemView: View) {
        val param = itemView.layoutParams as RecyclerView.LayoutParams
        if (!isVisible) {
            param.height = LinearLayout.LayoutParams.WRAP_CONTENT
            param.width = LinearLayout.LayoutParams.MATCH_PARENT
            itemView.visibility = View.VISIBLE
        } else {
            itemView.visibility = View.GONE
            param.height = 0
            param.width = 0
        }
        itemView.layoutParams = param
    }
}