package herbs.n.more.ui.cart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import herbs.n.more.R
import herbs.n.more.ui.dialog.MessageDialogFragment


class CartActivity : AppCompatActivity(R.layout.activity_cart){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    fun showMessage(title: String, message : String) {
        MessageDialogFragment(title, message).apply {show(supportFragmentManager, "TAG") }
    }
}