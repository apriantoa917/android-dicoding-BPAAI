package com.aprianto.dicostory.ui.dashboard.folder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aprianto.dicostory.data.model.Folder
import com.aprianto.dicostory.databinding.RvFolderBinding
import com.aprianto.dicostory.utils.Helper


class FolderAdapter(
    private val data: ArrayList<Folder>
) :
    RecyclerView.Adapter<FolderAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            RvFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val folder = data[position]
        holder.bind(folder)
    }

    inner class ViewHolder(private val binding: RvFolderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(folder: Folder) {
            binding.image.setImageBitmap(folder.asset)
            binding.image.setOnClickListener {
                Helper.loadImageFromStorage(folder.path)?.let {
                    Helper.showDialogPreviewImage(binding.root.context, it, folder.path)
                }

            }
        }
    }
}