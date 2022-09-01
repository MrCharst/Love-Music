package com.example.loveMusic.ui.nav_left

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loveMusic.R
import com.example.loveMusic.databinding.ActivityFeedbackBinding
import com.example.loveMusic.ui.main.MainActivity

class FeedbackActivity : AppCompatActivity() {

    lateinit var binding: ActivityFeedbackBinding

    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.phanhoi)
        binding.sendFA.setOnClickListener {
            val feedbackMsg = binding.feedbackMsgFA.text.toString().trim()
            val subject = binding.topicFA.text.toString().trim()
            if (feedbackMsg.length >= 20 && subject.length >= 3) {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:")
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("maiquyenlinh15320@gmail.com"))
                intent.putExtra(Intent.EXTRA_SUBJECT, subject)
                intent.putExtra(Intent.EXTRA_TEXT, feedbackMsg)
                try {
                    startActivity(Intent.createChooser(intent, getString(R.string.gmail)))
                } catch (ex: ActivityNotFoundException) {
                    Toast.makeText(this, R.string.cocaigisai, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, R.string.chitiethown, Toast.LENGTH_SHORT).show()
            }
        }
    }
}