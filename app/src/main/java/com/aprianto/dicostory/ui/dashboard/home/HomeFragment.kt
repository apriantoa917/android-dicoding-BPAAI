package com.aprianto.dicostory.ui.dashboard.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.aprianto.dicostory.R
import com.aprianto.dicostory.data.repository.remote.ApiConfig
import com.aprianto.dicostory.data.viewmodel.SettingViewModel
import com.aprianto.dicostory.data.viewmodel.StoryPagerViewModel
import com.aprianto.dicostory.data.viewmodel.ViewModelSettingFactory
import com.aprianto.dicostory.data.viewmodel.ViewModelStoryFactory
import com.aprianto.dicostory.databinding.FragmentHomeBinding
import com.aprianto.dicostory.ui.dashboard.MainActivity
import com.aprianto.dicostory.utils.Helper
import com.aprianto.dicostory.utils.SettingPreferences
import com.aprianto.dicostory.utils.dataStore
import java.util.*
import kotlin.concurrent.schedule

class HomeFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

//    private var viewModel: StoryViewModel? = null


    private var mainViewModel: StoryPagerViewModel? = null
    val rvAdapter = StoryAdapter()
    private lateinit var binding: FragmentHomeBinding

    //    private val rvAdapter = HomeAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

//        viewModel = ViewModelProvider(
//            this,
//            ViewModelGeneralFactory((activity as MainActivity))
//        )[StoryViewModel::class.java]

        val apiService = ApiConfig.getApiService()
        mainViewModel = ViewModelProvider(
            this,
            ViewModelStoryFactory(apiService)
        )[StoryPagerViewModel::class.java]

        val pref = SettingPreferences.getInstance((activity as MainActivity).dataStore)
        val settingViewModel =
            ViewModelProvider(this, ViewModelSettingFactory(pref))[SettingViewModel::class.java]


        /* toolbar */
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

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
        mainViewModel!!.story.observe(viewLifecycleOwner) {
            rvAdapter.submitData(
                lifecycle,
                it
            )
        }

        return binding.root
    }


    fun getData() {
        val adapters = StoryAdapter()
        binding.rvStory.adapter = adapters.withLoadStateFooter(
            footer = StoryLoadingStateAdapter {
                adapters.retry()
            }
        )
//        lifecycleScope.launch {
//
//        }


    }

    override fun onRefresh() {
        binding.swipeRefresh.isRefreshing = true
        rvAdapter.refresh()
        Timer().schedule(2000) {
            binding.swipeRefresh.isRefreshing = false
        }
        binding.rvStory.scrollToPosition(0)
//        binding.nestedScrollView.smoothScrollTo(0, 0)
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