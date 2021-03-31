package com.example.lab6

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

private const val key = "seconds"

class Coroutines_1 : AppCompatActivity() {
    private var seconds = 0
    private var job: Job? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            seconds = savedInstanceState.getInt(key)
        }
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        job = lifecycleScope.launchWhenResumed {
            while (isActive) {
                delay(1000)
                secondsText.post {
                    secondsText.text = getString(R.string.seconds_cor, seconds++)
                }
                Log.i("Job", "$seconds")
            }
        }
        super.onResume()
    }

    override fun onPause() {
        job?.cancel()
        Log.i("Job", "Job is canceled")
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(key, seconds)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.getInt(key)
        super.onRestoreInstanceState(savedInstanceState)
    }
}