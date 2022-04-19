package com.aprianto.dicostory.ui.dashboard.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.aprianto.dicostory.R
import com.aprianto.dicostory.data.viewmodel.SettingViewModel
import com.aprianto.dicostory.data.viewmodel.StoryViewModel
import com.aprianto.dicostory.data.viewmodel.ViewModelGeneralFactory
import com.aprianto.dicostory.data.viewmodel.ViewModelSettingFactory
import com.aprianto.dicostory.databinding.FragmentHomeBinding
import com.aprianto.dicostory.ui.dashboard.MainActivity
import com.aprianto.dicostory.utils.Constanta
import com.aprianto.dicostory.utils.Helper
import com.aprianto.dicostory.utils.SettingPreferences
import com.aprianto.dicostory.utils.dataStore
import java.util.*
import kotlin.concurrent.schedule

class HomeFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private var viewModel: StoryViewModel? = null
    private lateinit var binding: FragmentHomeBinding
    private val rvAdapter = HomeAdapter()
    private var tempToken = ""

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
        viewModel = ViewModelProvider(
            this,
            ViewModelGeneralFactory((activity as MainActivity))
        )[StoryViewModel::class.java]
        val pref = SettingPreferences.getInstance((activity as MainActivity).dataStore)
        val settingViewModel =
            ViewModelProvider(this, ViewModelSettingFactory(pref))[SettingViewModel::class.java]
        settingViewModel.getUserPreferences(Constanta.UserPreferences.UserToken.name)
            .observe(viewLifecycleOwner) { token ->
                tempToken = StringBuilder("Bearer ").append(token).toString()
                viewModel?.loadStoryData(tempToken)
            }
        /* toolbar */
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        binding.btnJumpUp.visibility = View.GONE
        binding.swipeRefresh.setOnRefreshListener {
            onRefresh()
        }
        binding.rvStory.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter = rvAdapter
        }
        viewModel?.apply {
            loading.observe(viewLifecycleOwner) { binding.loading.root.visibility = it }
            error.observe(
                viewLifecycleOwner
            ) { if (it.isNotEmpty()) Helper.showDialogInfo(requireContext(), it) }
            storyList.observe(viewLifecycleOwner) {
                rvAdapter.apply {
                    initData(it)
                    notifyDataSetChanged()
                }
                binding.btnJumpUp.visibility = View.VISIBLE
            }
        }
        binding.btnJumpUp.setOnClickListener {
            binding.nestedScrollView.smoothScrollTo(0, 0)
        }
        return binding.root
    }

    override fun onRefresh() {
        binding.swipeRefresh.isRefreshing = true
        viewModel?.loadStoryData(tempToken)
        Timer().schedule(2000) {
            binding.swipeRefresh.isRefreshing = false
        }
        binding.nestedScrollView.smoothScrollTo(0, 0)
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