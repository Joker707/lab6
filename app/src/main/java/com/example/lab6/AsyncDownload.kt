package com.example.lab6

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.download.*
import java.io.InputStream
import java.net.URL

private const val url = "https://pp.userapi.com/c628718/v628718010/15fe/7GxzcHAsxdA.jpg"

@Suppress("DEPRECATION")
class AsyncDownload : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.download)

        button_download.setOnClickListener {
            DownloadImageTask().execute(url)
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class DownloadImageTask : AsyncTask<String?, Void?, Bitmap?>() {

        override fun doInBackground(vararg urls: String?): Bitmap? {
            val urldisplay: String? = urls[0]
            var mIcon11: Bitmap? = null
            try {
                val input: InputStream = URL(urldisplay).openStream()
                mIcon11 = BitmapFactory.decodeStream(input)
            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
                e.printStackTrace()
            }
            return mIcon11
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            imageView_download.setImageBitmap(result)
        }
    }
}