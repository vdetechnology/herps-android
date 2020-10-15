package herbs.n.more.ui.cart

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import herbs.n.more.R
import herbs.n.more.ui.BaseActivity
import java.lang.Exception


class CartActivity : BaseActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
    }

    override fun onBackPressed() {
        val navHostFragment: NavHostFragment? =
            supportFragmentManager.findFragmentById(R.id.fragment_cart) as NavHostFragment?
        try {
            navHostFragment!!.childFragmentManager.fragments[0] as PaymentResultFragment
            onBackActivity()
        }catch (e : Exception){
            super.onBackPressed()
        }
    }
}