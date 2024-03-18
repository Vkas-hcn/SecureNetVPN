package com.secure.net.vpn.ui.proxy

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
import com.secure.net.vpn.data.AppInfo

class ProxyAppAdapter(private val dataList: MutableList<AppInfo>) :
    RecyclerView.Adapter<ProxyAppAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_app_name)
        var aivFlag: ImageView = itemView.findViewById(R.id.aiv_app_icon)
        var imgCheck: ImageView = itemView.findViewById(R.id.img_cho)

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
        holder.tvName.text = item.name
        holder.aivFlag.setImageDrawable(item.icon)
        if (item.isCheck) {
            holder.imgCheck.setImageResource(R.drawable.ic_app_2)
        } else {
            holder.imgCheck.setImageResource(R.drawable.ic_app_1)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context: Context = parent.context
        val inflater = LayoutInflater.from(context)
        val itemView: View = inflater.inflate(R.layout.layout_app, parent, false)
        return ViewHolder(itemView)
    }

    fun setSerAllData(datas: MutableList<AppInfo>) {
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