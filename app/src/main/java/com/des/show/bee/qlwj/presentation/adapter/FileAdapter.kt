package com.des.show.bee.qlwj.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.des.show.bee.R
import com.des.show.bee.qlwj.data.model.TrashFile
import com.des.show.bee.qlwj.util.FileSizeFormatter
import com.des.show.bee.databinding.ItemFileBinding


class FileAdapter(
    private val files: List<TrashFile>,
    private val onFileSelectionChanged: (Int) -> Unit
) : RecyclerView.Adapter<FileAdapter.FileViewHolder>() {

    class FileViewHolder(val binding: ItemFileBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val binding = ItemFileBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = files[position]

        with(holder.binding) {
            // 设置文件信息
            tvFileName.text = file.name
            tvFileSize.text = FileSizeFormatter.formatFileSize(file.size)

            // 设置选中状态图标
            imgFileSelect.setImageResource(
                if (file.isSelected) R.drawable.icon_check else R.drawable.icon_discheck
            )

            // 设置文件点击事件
            root.setOnClickListener {
                onFileSelectionChanged(position)
            }

            // 设置选择框点击事件
            imgFileSelect.setOnClickListener {
                onFileSelectionChanged(position)
            }
        }
    }

    override fun getItemCount() = files.size
}