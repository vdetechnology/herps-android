package herbs.n.more.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import herbs.n.more.R

class AuthActivity : AppCompatActivity(R.layout.activity_auth) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
    }
}