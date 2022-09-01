package com.example.loveMusic.ui.nav_left

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.loveMusic.R
import com.example.loveMusic.ui.main.MainActivity
import com.example.loveMusic.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.Về)
        binding.aboutText.text = aboutText()

        binding.btnGotoFeedback.setOnClickListener {
            startActivity(Intent(this@AboutActivity, FeedbackActivity::class.java))
        }
    }
    private fun aboutText(): String{
        return "${getString(R.string.Developed)} ${getString(R.string.maiquyenlinh)} \n\n${getString(R.string.lop)} CNTT4 K59 \n\n${getString(R.string.truongdaihoc)} \n\n${getString(R.string.detaitotnghiep)}" +
                "\n\n${getString(R.string.gopyFeedback)}\n\n${getString(R.string.hoanthientothon)}"
    }
}