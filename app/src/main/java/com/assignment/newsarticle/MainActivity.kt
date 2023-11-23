package com.assignment.newsarticle

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.window.SplashScreen
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.assignment.newsarticle.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    var delay = 2000L;
    var keepSplashOn = true;
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition{keepSplashOn};
        Handler(Looper.getMainLooper()).postDelayed({keepSplashOn=false}, delay);
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, HomeFragment());
        fragmentTransaction.commit()
    }
}








