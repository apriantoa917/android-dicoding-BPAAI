package com.aprianto.dicostory.ui.dashboard.profile

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.aprianto.dicostory.databinding.ActivityWebViewBinding
import com.aprianto.dicostory.utils.Constanta
import java.util.*
import kotlin.concurrent.schedule


@Suppress("DEPRECATION", "DEPRECATED_IDENTITY_EQUALS")
class WebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebViewBinding

    companion object {
        const val EXTRA_WEBVIEW = "EXTRA_WEBVIEW"
    }

    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.webView.let {
            it.loadUrl(intent.getStringExtra(EXTRA_WEBVIEW) ?: Constanta.URLPortfolio)
            it.settings.javaScriptEnabled = true
            it.webViewClient = object : WebViewClient() {
                @Deprecated("Deprecated in Java")
                override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                    view.loadUrl(url.toString())
                    return false
                }
            }
            it.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    binding.toolbar.title = view.title
                }
            }
        }
        /* toolbar */
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.swipeRefresh.setOnRefreshListener {
            binding.webView.reload()
            Timer().schedule(2000) {
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event!!.action === KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    if (binding.webView.canGoBack()) {
                        binding.webView.goBack()
                    } else {
                        finish()
                    }
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}