package com.example.films.activities

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.films.R
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val gifView = findViewById<GifImageView>(R.id.iconoSplash)
        val gifDrawable = GifDrawable(resources, R.drawable.count)

        gifDrawable.loopCount = 1
        gifView.setImageDrawable(gifDrawable)
        gifDrawable.start()

        gifDrawable.addAnimationListener {
            val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)
            gifView.startAnimation(fadeOut)

            gifView.postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, 800)
        }
    }
}
