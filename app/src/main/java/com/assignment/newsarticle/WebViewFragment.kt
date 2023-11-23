package com.assignment.newsarticle

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.assignment.newsarticle.databinding.FragmentWebViewBinding

class WebViewFragment: Fragment() {
    private val TAG: String = "WebView Fragment"
    lateinit var binding: FragmentWebViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWebViewBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loading.visibility = View.VISIBLE
        val url: String? = arguments?.getString("url")
        val techcrunch: Boolean? = arguments?.getBoolean("techcrunch")
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webViewClient = object : WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if(techcrunch == true){
                    binding.loading.text = "This page is having some problem, please consider opening in browser"
                }
                else{
                    binding.loading.visibility = View.GONE
                }
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                binding.loading.visibility = View.VISIBLE
                binding.loading.text = "Something went wrong"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                Toast.makeText(requireContext(), "Something went wrong, opening in browser", Toast.LENGTH_SHORT).show()
                startActivity(intent)
            }
        }
        if(url != null){
            binding.webView.loadUrl(url)
        }

        binding.back.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.browser.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }
}