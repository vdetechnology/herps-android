package herbs.n.more.ui

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import herbs.n.more.ui.cart.CartActivity

open class BaseActivity : AppCompatActivity() {

    fun closeKeyBoard() {
        val view: View? = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun goToCart() {
        val intent = Intent(this, CartActivity::class.java).apply {
        }
        startActivity(intent)

    }
}