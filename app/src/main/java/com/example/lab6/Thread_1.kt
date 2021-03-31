package com.example.lab6

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

private const val key = "seconds"

class Thread_1 : AppCompatActivity() {
    private var seconds = 0
    private var thread: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            seconds = savedInstanceState.getInt(key)
        }
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        thread = Thread {
            try {
                while (thread?.isInterrupted == false) {
                    Thread.sleep(1000)
                    secondsText.post {
                        secondsText.text = getString(R.string.seconds_thread, seconds++)
                    }
                    Log.i("Thread", "$seconds")
                }
            } catch (e: InterruptedException) {
                Log.i("Thread", "Thread is interrupted")
            }
        }

        thread?.start()
        super.onResume()
    }

    override fun onPause() {
        thread?.interrupt()
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