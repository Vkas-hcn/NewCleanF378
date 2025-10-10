package com.desolation.spreads.reach

import com.desolation.spreads.reach.databinding.ItemFileBinding


class FileScanAdapter(
    private val files: List<TrashFile>,
    private val onSelectionChanged: () -> Unit
) : androidx.recyclerview.widget.RecyclerView.Adapter<FileScanAdapter.FileViewHolder>() {

    class FileViewHolder(val binding: ItemFileBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): FileViewHolder {
        val binding = ItemFileBinding.inflate(
            android.view.LayoutInflater.from(parent.context), parent, false
        )
        return FileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = files[position]

        with(holder.binding) {
            tvFileName.text = file.name
            tvFileSize.text = formatFileSize(file.size)

            imgFileSelect.setImageResource(
                if (file.isSelected) R.drawable.icon_check else R.drawable.icon_discheck
            )

            root.setOnClickListener {
                toggleFileSelection(file, position)
            }

            imgFileSelect.setOnClickListener {
                toggleFileSelection(file, position)
            }
        }
    }

    override fun getItemCount() = files.size

    private fun toggleFileSelection(file: TrashFile, position: Int) {
        file.isSelected = !file.isSelected
        notifyItemChanged(position)
        onSelectionChanged()
    }

    private fun formatFileSize(size: Long): String {
        return when {
            size >= 1024 * 1024 -> String.format("%.2fMB", size / (1024.0 * 1024.0))
            else -> String.format("%.2fKB", size / 1024.0)
        }
    }
}