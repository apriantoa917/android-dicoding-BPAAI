package com.aprianto.dicostory.ui.dashboard.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.aprianto.dicostory.R
import com.aprianto.dicostory.databinding.FragmentHomeBinding
import com.aprianto.dicostory.ui.dashboard.MainActivity
import com.aprianto.dicostory.utils.Helper
import java.util.*
import kotlin.concurrent.schedule


class HomeFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private val rvAdapter = StoryAdapter()
    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* activate options menu in fragments */
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val mainViewModel = (activity as MainActivity).getStoryViewModel()
        mainViewModel.story.observe(viewLifecycleOwner) {
            rvAdapter.submitData(
                lifecycle,
                it
            )
            Helper.updateWidgetData(requireContext())
        }
        /* toolbar */
        (activity as MainActivity).setSupportActionBar(binding.toolbar)

        binding.swipeRefresh.setOnRefreshListener {
            onRefresh()
        }
        binding.rvStory.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter =
                rvAdapter.withLoadStateFooter(footer = StoryLoadingStateAdapter { rvAdapter.retry() })
        }
        return binding.root
    }


    /* handling onSwipeRefresh action */
    override fun onRefresh() {
        binding.swipeRefresh.isRefreshing = true
        rvAdapter.refresh()
        Timer().schedule(2000) {
            binding.swipeRefresh.isRefreshing = false
            binding.rvStory.smoothScrollToPosition(0)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.activity_main_toolbar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.swipeRefresh -> {
                onRefresh()
            }
            R.id.infoDialog -> {
                Helper.showDialogInfo(
                    (activity as MainActivity),
                    (activity as MainActivity).getString(R.string.UI_info_homescreen),
                    Gravity.START
                )
            }
        }
        return true
    }

}