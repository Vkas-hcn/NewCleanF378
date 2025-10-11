package com.desolation.spreads.reach.cf.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desolation.spreads.reach.R
import com.desolation.spreads.reach.databinding.ItemDuplicateItemBinding
import com.desolation.spreads.reach.cf.model.DuplicateFile
import com.desolation.spreads.reach.cf.utils.FileUtils

class DuplicateItemAdapter(
    private val onItemClickListener: (DuplicateFile) -> Unit
) : RecyclerView.Adapter<DuplicateItemAdapter.ItemViewHolder>() {

    private var files: List<DuplicateFile> = emptyList()

    fun updateFiles(newFiles: List<DuplicateFile>) {
        files = newFiles
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemDuplicateItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(files[position])
    }

    override fun getItemCount(): Int = files.size

    inner class ItemViewHolder(private val binding: ItemDuplicateItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(file: DuplicateFile) {
            binding.tvFileName.text = file.name
            binding.tvFileSize.text = FileUtils.formatFileSize(file.size)
            
            // Set icon based on selection state
            binding.imgCheck.setImageResource(
                if (file.isSelected) R.drawable.icon_check_file else R.drawable.icon_discheck_dup
            )
            
            binding.root.setOnClickListener {
                file.isSelected = !file.isSelected
                notifyItemChanged(adapterPosition)
                onItemClickListener(file)
            }
        }
    }
}
