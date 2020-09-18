package herbs.n.more.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import herbs.n.more.R

class SplashActivity: AppCompatActivity(R.layout.activity_splash) {

    private val SPLASH_TIME_OUT = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        YoYo.with(Techniques.Bounce)
            .duration(7000)
            .playOn(findViewById(R.id.logo))

        YoYo.with(Techniques.FadeInUp)
            .duration(5000)
            .playOn(findViewById(R.id.appname))

        Handler().postDelayed({ // This method will be executed once the timer is over
            // Start your app main activity
            startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
            finish()
        }, SPLASH_TIME_OUT.toLong())
    }
}