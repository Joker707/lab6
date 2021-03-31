package com.example.lab6

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.download.*


private const val url = "https://pp.userapi.com/c628718/v628718010/15fe/7GxzcHAsxdA.jpg"

class PicassoExample : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.download)

        button_download.setOnClickListener {
            Picasso.get().load(url).into(imageView_download)
        }

    }
}