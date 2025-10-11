package com.desolation.spreads.reach.cf.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.desolation.spreads.reach.databinding.ItemDuplicateCategoryBinding
import com.desolation.spreads.reach.cf.model.DuplicateCategory
import com.desolation.spreads.reach.cf.model.DuplicateFile

class DuplicateCategoryAdapter(
    private val onItemClickListener: (DuplicateFile) -> Unit,
    private val onCategoryClickListener: (DuplicateCategory) -> Unit
) : RecyclerView.Adapter<DuplicateCategoryAdapter.CategoryViewHolder>() {

    private var categories: List<DuplicateCategory> = emptyList()

    fun updateCategories(newCategories: List<DuplicateCategory>) {
        categories = newCategories
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemDuplicateCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

    inner class CategoryViewHolder(private val binding: ItemDuplicateCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: DuplicateCategory) {
            // Set category title
            val title = if (category.isImageCategory) {
                "Similar Images (${category.dateGroup})"
            } else {
                "Duplicate Files (${category.files.size} files)"
            }
            binding.tvCategoryTitle.text = title
            
            val adapter = DuplicateItemAdapter { file ->
                onItemClickListener(file)
            }
            
            binding.rvCategories.layoutManager = LinearLayoutManager(binding.root.context)
            binding.rvCategories.adapter = adapter
            adapter.updateFiles(category.files)
            
            // Set click listener for category header if needed
            binding.root.setOnClickListener {
                onCategoryClickListener(category)
            }
        }
    }
}