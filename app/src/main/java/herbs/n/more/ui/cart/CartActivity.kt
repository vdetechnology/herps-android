package herbs.n.more.ui.cart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import herbs.n.more.R


class CartActivity : AppCompatActivity(R.layout.activity_cart){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }
}