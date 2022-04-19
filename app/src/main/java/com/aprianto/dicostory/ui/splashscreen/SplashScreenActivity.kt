package com.aprianto.dicostory.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.aprianto.dicostory.ui.dashboard.MainActivity
import com.aprianto.dicostory.data.viewmodel.SettingViewModel
import com.aprianto.dicostory.data.viewmodel.ViewModelSettingFactory
import com.aprianto.dicostory.databinding.ActivitySplashScreenBinding
import com.aprianto.dicostory.ui.auth.AuthActivity
import com.aprianto.dicostory.utils.Constanta
import com.aprianto.dicostory.utils.SettingPreferences
import com.aprianto.dicostory.utils.dataStore
import java.util.*
import kotlin.concurrent.schedule


class  SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val pref = SettingPreferences.getInstance(dataStore)
        val settingViewModel =
            ViewModelProvider(this, ViewModelSettingFactory(pref))[SettingViewModel::class.java]

        settingViewModel.getUserPreferences(Constanta.UserPreferences.UserToken.name)
            .observe(this) { token ->
                if (token == Constanta.preferenceDefaultValue) Timer().schedule(2000) {
                    startActivity(Intent(this@SplashScreenActivity, AuthActivity::class.java))
                    finish()
                } else Timer().schedule(2000) {
                    startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                    finish()
                }
            }
    }
}