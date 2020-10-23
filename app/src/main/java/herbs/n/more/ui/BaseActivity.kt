package herbs.n.more.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import herbs.n.more.R
import herbs.n.more.ui.cart.CartActivity
import herbs.n.more.ui.dialog.MessageDialogFragment
import herbs.n.more.ui.search.FilterActivity
import herbs.n.more.ui.search.ResultActivity
import herbs.n.more.ui.search.SearchActivity

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    fun closeKeyBoard() {
        val view: View? = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun closeKeyBoardAndClearFocus() {
        val view: View? = this.currentFocus
        if (view != null) {
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        window?.decorView?.clearFocus()
    }

    fun goToCart() {
        val intent = Intent(this, CartActivity::class.java)
        startActivity(intent)

    }

    fun goToFilter(
        q: String,
        sort: Int,
        category: ArrayList<String>?,
        price_from: Float,
        price_to: Float
    ) {
        val intent = Intent(this, FilterActivity::class.java)
        intent.putExtra("q", q)
        intent.putExtra("sort", sort)
        intent.putStringArrayListExtra("category", category)
        intent.putExtra("price_from", price_from)
        intent.putExtra("price_to", price_to)
        startActivity(intent)
    }

    fun goToSearchResult(
        q: String,
        sort: Int,
        category: ArrayList<String>?,
        price_from: Float,
        price_to: Float
    ) {
        val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("q", q)
            intent.putExtra("sort", sort)
            intent.putStringArrayListExtra("category", category)
            intent.putExtra("price_from", price_from)
            intent.putExtra("price_to", price_to)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    fun goToSearchWithData(
        q: String,
        sort: Int,
        category: ArrayList<String>?,
        price_from: Float,
        price_to: Float
    ) {
        val intent = Intent(this, SearchActivity::class.java)
        intent.putExtra("q", q)
        intent.putExtra("sort", sort)
        intent.putStringArrayListExtra("category", category)
        intent.putExtra("price_from", price_from)
        intent.putExtra("price_to", price_to)
        startActivity(intent)
    }

    fun onBackActivity(){
        finish()
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
    }

    fun showMessage(title: String, message: String) {
        MessageDialogFragment(title, message).apply {show(supportFragmentManager, "TAG") }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            R.anim.anim_slide_in_left, R.anim.anim_slide_out_right
        )
    }
}