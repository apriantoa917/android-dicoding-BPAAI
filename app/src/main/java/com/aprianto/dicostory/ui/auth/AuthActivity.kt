package com.aprianto.dicostory.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.aprianto.dicostory.R
import com.aprianto.dicostory.databinding.ActivityAuthBinding
import com.aprianto.dicostory.ui.dashboard.MainActivity

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        @Suppress("DEPRECATION")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.container, LoginFragment.newInstance())
                .commit()
        }
    }

    fun routeToMainActivity() {
        startActivity(Intent(this@AuthActivity, MainActivity::class.java))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }


}