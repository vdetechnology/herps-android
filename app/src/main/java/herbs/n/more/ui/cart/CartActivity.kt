package herbs.n.more.ui.cart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import herbs.n.more.R
import herbs.n.more.ui.dialog.MessageDialogFragment
import kotlinx.android.synthetic.main.fragment_detail.*


class CartActivity : AppCompatActivity(R.layout.activity_cart){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    fun showMessage(title: String, message : String) {
        MessageDialogFragment(title, message).apply {show(supportFragmentManager, "TAG") }
    }

    override fun onBackPressed() {
        //findNavController(R.id.fragment_cart).currentDestination?.id
        val navHostFragment: NavHostFragment? =
            supportFragmentManager.findFragmentById(R.id.fragment_cart) as NavHostFragment?
        if (navHostFragment!!.childFragmentManager.fragments[0].id == R.layout.fragment_payment){

        }else {
            super.onBackPressed()
        }
    }
}