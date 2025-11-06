package com.des.show.bee.qlwj.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.des.show.bee.R
import com.des.show.bee.qlwj.data.model.TrashCategory
import com.des.show.bee.qlwj.util.FileSizeFormatter
import com.des.show.bee.databinding.ItemCategoryBinding


class CategoryAdapter(
    private var categories: List<TrashCategory>,
    private val onCategoryExpansionChanged: (Int) -> Unit,
    private val onCategorySelectionChanged: (Int) -> Unit,
    private val onFileSelectionChanged: (Int, Int) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]

        with(holder.binding) {
            // 设置分类信息
            tvTitle.text = category.name
            tvSize.text = FileSizeFormatter.formatFileSize(category.totalSize)

            // 设置选中状态图标
            imgSelect.setImageResource(
                if (category.isSelected) R.drawable.icon_check else R.drawable.icon_discheck
            )

            // 处理文件列表的展开/收起
            if (category.isExpanded && category.files.isNotEmpty()) {
                rvItemFile.visibility = View.VISIBLE
                setupFileRecyclerView(holder, category, position)
            } else {
                rvItemFile.visibility = View.GONE
            }

            // 设置分类点击事件（展开/收起）
            llCategory.setOnClickListener {
                onCategoryExpansionChanged(position)
            }

            // 设置分类选择事件
            imgSelect.setOnClickListener {
                onCategorySelectionChanged(position)
            }
        }
    }


    private fun setupFileRecyclerView(holder: CategoryViewHolder, category: TrashCategory, categoryPosition: Int) {
        val fileAdapter = FileAdapter(category.files) {
            filePosition ->
            onFileSelectionChanged(categoryPosition, filePosition)
        }
        
        with(holder.binding.rvItemFile) {
            // 避免重复设置LayoutManager
            if (layoutManager == null) {
                layoutManager = LinearLayoutManager(holder.itemView.context)
            }
            adapter = fileAdapter
        }
    }

    override fun getItemCount() = categories.size


    fun updateCategories(newCategories: List<TrashCategory>) {
        categories = newCategories
        notifyDataSetChanged()
    }
}