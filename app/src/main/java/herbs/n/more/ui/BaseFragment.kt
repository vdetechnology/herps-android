package herbs.n.more.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import herbs.n.more.R
import herbs.n.more.data.db.entities.User
import herbs.n.more.ui.cart.CartActivity
import herbs.n.more.ui.search.SearchActivity
import herbs.n.more.util.Coroutines
import java.text.DecimalFormat

open class BaseFragment : Fragment() {

    fun closeKeyBoard() {
        val view: View? = activity?.currentFocus
        if (view != null) {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun closeKeyBoardAndClearFocus() {
        val view: View? = activity?.currentFocus
        if (view != null) {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        activity?.window?.decorView?.clearFocus()
    }

    fun onBackActivity(view: View){
        activity?.finish()
        activity?.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
    }

    fun onBackNavController(view: View){
        view.findNavController().popBackStack()
    }

    fun goToCart() {
        val intent = Intent(activity, CartActivity::class.java)
        startActivity(intent)

    }

    fun goToSearch() {
        val intent = Intent(activity, SearchActivity::class.java)
        startActivity(intent)

    }

    fun convertMoney(double: Double) : String{
        val mDecimalFormat = DecimalFormat("###,###,##0")
        return mDecimalFormat.format(double).replace(",", ".") + context?.resources?.getString(R.string.vnd)
    }
}