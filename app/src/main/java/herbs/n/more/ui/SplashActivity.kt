package herbs.n.more.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import herbs.n.more.R
import net.simplifiedcoding.mvvmsampleapp.data.preferences.PreferenceProvider

class SplashActivity: AppCompatActivity(R.layout.activity_splash) {

    private val SPLASH_TIME_OUT = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        YoYo.with(Techniques.Bounce)
            .duration(4000)
            .playOn(findViewById(R.id.logo))

        /*YoYo.with(Techniques.FadeInUp)
            .duration(5000)
            .playOn(findViewById(R.id.appname))*/
        val preferenceProvider  = PreferenceProvider(this)
        if (preferenceProvider.getIntro() == 1){
            Handler().postDelayed({
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }, SPLASH_TIME_OUT.toLong())
        }else {
            Handler().postDelayed({
                startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
                finish()
            }, SPLASH_TIME_OUT.toLong())
        }
    }
}