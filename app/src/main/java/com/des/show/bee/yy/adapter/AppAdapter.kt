package com.des.show.bee.yy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.des.show.bee.databinding.ItemAppItemBinding
import com.des.show.bee.yy.model.AppInfo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppAdapter(
    private val appList: List<AppInfo>,
    private val onUninstallClick: (AppInfo) -> Unit
) : RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemAppItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AppViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(appList[position])
    }

    override fun getItemCount(): Int = appList.size

    inner class AppViewHolder(private val binding: ItemAppItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(appInfo: AppInfo) {
            binding.apply {
                imgAppIcon.setImageDrawable(appInfo.icon)
                tvAppName.text = appInfo.appName
                
                // 格式化安装时间
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val installDate = Date(appInfo.installTime)
                tvAppLauTime.text = dateFormat.format(installDate)
                
                // 设置卸载按钮点击事件
                tvUninstall.setOnClickListener {
                    onUninstallClick(appInfo)
                }
            }
        }
    }
}