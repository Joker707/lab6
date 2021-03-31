package com.example.lab6

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

private const val key = "seconds"

@Suppress("DEPRECATION")
class Async_1 : AppCompatActivity() {
    private var seconds = 0
    private var task: AsyncTasc? = null


    @SuppressLint("StaticFieldLeak")
    inner class AsyncTasc : AsyncTask<Unit, Unit, Unit>() {

        override fun onProgressUpdate(vararg values: Unit?) {
            super.onProgressUpdate(*values)
            secondsText.post {
                secondsText.text = getString(R.string.seconds_task, seconds++)
            }
            Log.i("Task", "$seconds")
        }

        override fun doInBackground(vararg p0: Unit?) {
            while (!isCancelled) {
                Thread.sleep(1000)
                publishProgress()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            seconds = savedInstanceState.getInt(key)
        }
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        task = AsyncTasc()
        task?.execute()
        super.onResume()
    }

    override fun onPause() {
        task?.cancel(false)
        Log.i("Task", "Task is canceled")
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