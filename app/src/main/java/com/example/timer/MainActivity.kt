package com.example.timer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.example.timer.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentProgress = 0
    private var isTimerActivated = false
    private var startRunning: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        setViewValues()
        startProcess()
    }

    private fun setViewValues() {
        binding.slider.addOnChangeListener { _, value, _ ->
            binding.counterTextView.text = value.toInt().toString()
            binding.progressBar.progress = value.toInt()
            binding.startButton.isEnabled = binding.slider.value != 0f
        }
    }

    private fun startProcess() {
        binding.startButton.setOnClickListener {
            currentProgress = binding.slider.value.toInt()
            if (currentProgress > 0 && !isTimerActivated) {
                Toast.makeText(applicationContext, R.string.start_message, Toast.LENGTH_SHORT)
                    .show()
                setEndValues()
                startCountDown()
            } else scope.launch {
                startRunning?.cancel()
                setStartValues()
                Toast.makeText(applicationContext, R.string.stop_message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCountDown() {
        startRunning = scope.launch {
            while (currentProgress > 0) {
                delay(1000)
                currentProgress--
                binding.counterTextView.text = currentProgress.toString()
                binding.progressBar.progress = currentProgress
            }
            setStartValues()
        }
    }

    private fun setStartValues() {
        currentProgress = 0
        isTimerActivated = false
        binding.startButton.setText(R.string.go)
        binding.slider.isEnabled = true
        binding.counterTextView.text = binding.slider.value.toInt().toString()
        binding.progressBar.progress = binding.slider.value.toInt()

    }

    private fun setEndValues() {
        isTimerActivated = true
        binding.startButton.setText(R.string.stop)
        binding.slider.isEnabled = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_COUNTER, currentProgress)
        outState.putBoolean(IS_ACTIVE, isTimerActivated)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentProgress = savedInstanceState.getInt(KEY_COUNTER)
        isTimerActivated = savedInstanceState.getBoolean(IS_ACTIVE)
        binding.counterTextView.text = currentProgress.toString()
        binding.startButton.isEnabled = binding.slider.value != 0f
        binding.progressBar.progress = currentProgress
        setEndValues()
        startCountDown()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    companion object {
        private const val KEY_COUNTER = "counter"
        private const val IS_ACTIVE = "is active"
    }
}