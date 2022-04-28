package com.aprianto.dicostory.ui.dashboard.profile

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aprianto.dicostory.R
import com.aprianto.dicostory.data.viewmodel.SettingViewModel
import com.aprianto.dicostory.data.viewmodel.ViewModelSettingFactory
import com.aprianto.dicostory.databinding.FragmentProfileBinding
import com.aprianto.dicostory.ui.dashboard.MainActivity
import com.aprianto.dicostory.utils.Constanta
import com.aprianto.dicostory.utils.Helper
import com.aprianto.dicostory.utils.SettingPreferences
import com.aprianto.dicostory.utils.dataStore


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pref = SettingPreferences.getInstance((activity as MainActivity).dataStore)
        val settingViewModel =
            ViewModelProvider(this, ViewModelSettingFactory(pref))[SettingViewModel::class.java]
        settingViewModel.getUserPreferences(Constanta.UserPreferences.UserName.name)
            .observe(viewLifecycleOwner) {
                binding.textName.text = it
            }
        settingViewModel.getUserPreferences(Constanta.UserPreferences.UserUID.name)
            .observe(viewLifecycleOwner) {
                binding.textUid.text = it
            }
        settingViewModel.getUserPreferences(Constanta.UserPreferences.UserEmail.name)
            .observe(viewLifecycleOwner) {
                binding.textEmail.text = it
            }
        settingViewModel.getUserPreferences(Constanta.UserPreferences.UserLastLogin.name)
            .observe(viewLifecycleOwner) {
                binding.textLastLogin.text =
                    StringBuilder(getString(R.string.const_text_login_on))
                        .append(" ")
                        .append(Helper.getSimpleDateString(it))
            }
        settingViewModel.getUserPreferences(Constanta.UserPreferences.UserToken.name)
            .observe(viewLifecycleOwner) {
                if (it == Constanta.preferenceDefaultValue) {
                    (activity as MainActivity).routeToAuth()
                }
            }
        binding.btnLogout.setOnClickListener {
            settingViewModel.clearUserPreferences()
        }
        binding.btnInfo.setOnClickListener {
            Helper.showDialogInfo(
                (activity as MainActivity),
                (activity as MainActivity).getString(R.string.UI_info_profile), Gravity.START
            )
        }
        binding.btnSetPermission.setOnClickListener {
            Helper.openSettingPermission(requireContext())
        }
        binding.btnSetLanguage.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
        binding.btnPandoraBox.setOnClickListener {
            (activity as MainActivity).startActivity(
                Intent(
                    (activity as MainActivity),
                    WebViewActivity::class.java
                )
            )
        }
    }
}