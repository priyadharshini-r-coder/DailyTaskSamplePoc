package com.example.dailytasksamplepoc.kotlinomnicure.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.example.dailytasksamplepoc.R
import com.example.dailytasksamplepoc.databinding.ActivityTrainingMaterialBinding
import com.example.kotlinomnicure.utils.Constants
import com.example.kotlinomnicure.utils.PrefUtility

class TrainingMaterialsActivity : BaseActivity(){

    //Declare the variables
    private val TAG: String = TrainingMaterialsActivity::class.java.getSimpleName()
    private var binding: ActivityTrainingMaterialBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_training_material)
        initToolbar()
        setView()
    }

    //init the toolbar
    private fun initToolbar() {
        setSupportActionBar(binding!!.toolbar)
        addBackButton()
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }
        //        binding.toolbar.setTitle(getString(R.string.system_alert));
        binding!!.toolbar.setNavigationIcon(R.drawable.ic_back)
    }

    //set the view
    @SuppressLint("SetJavaScriptEnabled")
    private fun setView() {
        val url: String? =
            PrefUtility().getStringInPref(this, Constants.SharedPrefConstants.TUTORIAL_URL, "")


        binding!!.webView.webViewClient = MyBrowser()
        binding!!.webView.settings.loadsImagesAutomatically = true
        binding!!.webView.settings.javaScriptEnabled = true
        binding!!.webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        if (url != null) {
            binding!!.webView.loadUrl(url)
        }
    }

    //link for the browser
    private class MyBrowser : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }
    }
}