package com.example.lab6

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.download.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.URL

private const val url = "https://pp.userapi.com/c628718/v628718010/15fe/7GxzcHAsxdA.jpg"

@Suppress("BlockingMethodInNonBlockingContext")
class CoroutinesDownload : AppCompatActivity() {
    private var icon: Bitmap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.download)

        button_download.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                withContext(Dispatchers.IO) {
                    try {
                        val input: InputStream = URL(url).openStream()
                        icon = BitmapFactory.decodeStream(input)
                    } catch (e: Exception) {
                        Log.e("Error", e.message.toString())
                        e.printStackTrace()
                    }
                }
                withContext(Dispatchers.Main) {
                    imageView_download.setImageBitmap(icon)
                }
            }
        }
    }
}