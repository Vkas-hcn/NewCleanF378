package com.desolation.spreads.reach.qlwj

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.desolation.spreads.reach.qlwj.FileScanAdapter
import com.desolation.spreads.reach.R
import com.desolation.spreads.reach.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val categories: List<TrashCategory>,
    private val onSelectionChanged: () -> Unit
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
            tvTitle.text = category.name
            tvSize.text = formatFileSize(category.totalSize)



            updateCategorySelection(category)

            imgSelect.setImageResource(
                if (category.isSelected) R.drawable.icon_check else R.drawable.icon_discheck
            )

            if (category.isExpanded) {
                rvItemFile.visibility = View.VISIBLE
                val fileAdapter = FileScanAdapter(category.files) {
                    updateCategorySelection(category)
                    notifyItemChanged(position)
                    onSelectionChanged()
                }
                rvItemFile.apply {
                    layoutManager = LinearLayoutManager(holder.itemView.context)
                    adapter = fileAdapter
                }
            } else {
                rvItemFile.visibility = View.GONE
            }

            llCategory.setOnClickListener {
                category.isExpanded = !category.isExpanded
                notifyItemChanged(position)
            }

            imgSelect.setOnClickListener {
                category.isSelected = !category.isSelected
                category.files.forEach { it.isSelected = category.isSelected }
                notifyItemChanged(position)
                onSelectionChanged()
            }
        }
    }

    override fun getItemCount() = categories.size

    private fun updateCategorySelection(category: TrashCategory) {
        category.isSelected = category.files.isNotEmpty() && category.files.all { it.isSelected }
    }

    private fun formatFileSize(size: Long): String {
        return when {
            size >= 1024 * 1024 * 1024 -> String.format("%.2fGB", size / (1024.0 * 1024.0 * 1024.0))
            size >= 1024 * 1024 -> String.format("%.2fMB", size / (1024.0 * 1024.0))
            else -> String.format("%.2fKB", size / 1024.0)
        }
    }
}