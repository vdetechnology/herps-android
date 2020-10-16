package herbs.n.more.ui.cart

import android.view.KeyEvent
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.xwray.groupie.databinding.BindableItem
import herbs.n.more.R
import herbs.n.more.data.db.entities.Cart
import herbs.n.more.databinding.ItemCartBinding
import java.text.DecimalFormat


class CartItem(
    private val context: CartFragment,
    private val cart: Cart
) : BindableItem<ItemCartBinding>(){

    override fun getLayout() = R.layout.item_cart

    override fun bind(viewBinding: ItemCartBinding, position: Int) {
        viewBinding.cart = cart
        val mDecimalFormat = DecimalFormat("###,###,##0")
        if (cart.total_sales != 0){
            viewBinding.tvPriceSale.text = (mDecimalFormat.format(cart.sale_price?.toDouble()).toString()).replace(
                ",",
                "."
            ) + context.resources.getString(R.string.vnd)
            viewBinding.tvPrice.text = (mDecimalFormat.format(cart.price?.toDouble()).toString()).replace(
                ",",
                "."
            ) + context.resources.getString(R.string.vnd)
            viewBinding.tvPrice.viewTreeObserver.addOnGlobalLayoutListener(object :
                OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewBinding.tvPrice.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    viewBinding.tvPrice.height //height is ready
                    val params: ViewGroup.LayoutParams = viewBinding.line.layoutParams
                    params.width = viewBinding.tvPrice.width
                    viewBinding.line.layoutParams = params;
                }
            })
        }else{
            viewBinding.tvPriceSale.text = (mDecimalFormat.format(cart.price?.toDouble()).toString()).replace(
                ",",
                "."
            ) + context.resources.getString(R.string.vnd)
        }
        Glide
            .with(context)
            .load(cart.image)
            .error(R.mipmap.ic_logo)
            .centerCrop()
            .into(viewBinding.ivProductImage);
        viewBinding.btDelete.setOnClickListener {
            context.closeKeyBoardAndClearFocus()
            context.showConfirmDelete(cart, viewBinding.etAmount)
        }
        viewBinding.ivBuyLate.setOnClickListener {
            context.closeKeyBoardAndClearFocus()
            Toast.makeText(
                context.activity,
                context.resources.getString(R.string.comming_soon),
                Toast.LENGTH_SHORT
            ).show()
        }
        viewBinding.tvBuyLate.setOnClickListener {
            context.closeKeyBoardAndClearFocus()
            Toast.makeText(
                context.activity,
                context.resources.getString(R.string.comming_soon),
                Toast.LENGTH_SHORT
            ).show()
        }
        viewBinding.cvMinus.setOnClickListener {
            context.closeKeyBoardAndClearFocus()
            if (cart.amount!! > 1){
                cart.amount = cart.amount?.minus(1)
                if (cart?.total_sales!! > 0) {
                    cart.total_order = cart.amount?.times(cart.sale_price!!)
                }else{
                    cart.total_order = cart.amount?.times(cart.price!!)
                }
                context.viewModel.updateCart(cart)
            }
        }
        viewBinding.cvPlus.setOnClickListener {
            context.closeKeyBoardAndClearFocus()
            cart.amount = cart.amount?.plus(1)
            if (cart.total_sales!! > 0) {
                cart.total_order = cart.amount?.times(cart.sale_price!!)
            }else{
                cart.total_order = cart.amount?.times(cart.price!!)
            }
            context.viewModel.updateCart(cart)
        }

        viewBinding.etAmount.setText(cart.amount.toString())

        viewBinding.etAmount.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus){
                if (viewBinding.etAmount.text.isEmpty()){
                    context.showConfirmDelete(cart, viewBinding.etAmount)
                }
            }
        }

        viewBinding.etAmount.addTextChangedListener {
            if (viewBinding.etAmount.text.toString() == "0"){
                context.showConfirmDelete(cart, viewBinding.etAmount)
            }else{
                saveAmount(viewBinding)
            }
        }

        viewBinding.etAmount.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (event != null && event.keyCode === KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                v.clearFocus()
            }
            false
        })

        viewBinding.rlCart.setOnClickListener {
            context.closeKeyBoardAndClearFocus()
        }
    }

    private fun saveAmount(viewBinding: ItemCartBinding){
        val number = viewBinding.etAmount.text.toString()
        if (number.isNotEmpty() && number != "0"){
            cart.amount = number.toInt()
            if (cart.total_sales!! > 0) {
                cart.total_order = cart.amount?.times(cart.sale_price!!)
            }else{
                cart.total_order = cart.amount?.times(cart.price!!)
            }
            context.viewModel.updateCart(cart).apply {
               viewBinding.etAmount.clearFocus()
            }
        }
    }
}