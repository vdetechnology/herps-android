package herbs.n.more.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import herbs.n.more.R
import herbs.n.more.ui.dialog.MessageDialogFragment


class AuthActivity : AppCompatActivity(R.layout.activity_auth){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
    }

    fun showMessage(title: String, message : String) {
        MessageDialogFragment(title, message).apply {show(supportFragmentManager, "TAG") }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right
        )
    }
}