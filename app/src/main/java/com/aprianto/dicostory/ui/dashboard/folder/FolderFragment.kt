package com.aprianto.dicostory.ui.dashboard.folder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.aprianto.dicostory.data.viewmodel.FolderViewModel
import com.aprianto.dicostory.databinding.FragmentDownloadedBinding


class FolderFragment : Fragment() {
    private lateinit var binding: FragmentDownloadedBinding
    val viewModel: FolderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDownloadedBinding.inflate(
            inflater,
            container,
            false
        )

        viewModel.let { vm ->
            vm.loadingStory.observe(viewLifecycleOwner) {
                binding.loadingStory.isVisible = it
            }
            vm.loadingDownload.observe(viewLifecycleOwner) {
                binding.loadingDownload.isVisible = it
            }
            vm.assetImageStory.observe(viewLifecycleOwner) { data ->
                binding.rvStory.let {
                    binding.nullStory.isVisible = data.isEmpty()
                    it.setHasFixedSize(true)
                    it.layoutManager = GridLayoutManager(context, 2)
                    it.isNestedScrollingEnabled = false
                    it.adapter = FolderAdapter(data)
                }
            }
            vm.assetImageDownload.observe(viewLifecycleOwner) { data ->
                binding.rvDownload.let {
                    binding.nullDownload.isVisible = data.isEmpty()
                    it.setHasFixedSize(true)
                    it.layoutManager = GridLayoutManager(context, 2)
                    it.isNestedScrollingEnabled = false
                    it.adapter = FolderAdapter(data)
                }
            }
        }
        return binding.root
    }
}