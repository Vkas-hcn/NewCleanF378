package com.des.show.bee.yy.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.des.show.bee.FileItem
import com.des.show.bee.R

class FileCleanAdapter(
    private val files: MutableList<FileItem>,
    private val onSelectionChanged: () -> Unit
) : RecyclerView.Adapter<FileCleanAdapter.FileViewHolder>() {

    class FileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivIcon: ImageView = view.findViewById(R.id.img_file_icon)
        val tvName: TextView = view.findViewById(R.id.tv_app_name)
        val tvSize: TextView = view.findViewById(R.id.tv_file_size)
        val ivSelect: ImageView = view.findViewById(R.id.tv_uninstall)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_file_clean, parent, false)
        return FileViewHolder(v)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val item = files[position]
        holder.tvName.text = item.name
        holder.tvSize.text = formatSize(item.size)
        holder.ivSelect.setImageResource(
            if (item.isSelected) R.drawable.icon_check_file else R.drawable.icon_discheck_file
        )

        holder.itemView.setOnClickListener {
            item.isSelected = !item.isSelected
            notifyItemChanged(position)
            onSelectionChanged()
        }
    }

    override fun getItemCount(): Int = files.size

    private fun formatSize(size: Long): String {
        return when {
            size >= 1024L * 1024L * 1024L -> String.format("%.2f GB", size / (1024.0 * 1024.0 * 1024.0))
            size >= 1024L * 1024L -> String.format("%.2f MB", size / (1024.0 * 1024.0))
            size >= 1024L -> String.format("%.2f KB", size / 1024.0)
            else -> "$size B"
        }
    }
}
