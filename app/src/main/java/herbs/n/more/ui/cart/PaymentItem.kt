package herbs.n.more.ui.cart

import com.bumptech.glide.Glide
import com.xwray.groupie.databinding.BindableItem
import herbs.n.more.R
import herbs.n.more.data.db.entities.Cart
import herbs.n.more.databinding.ItemPaymentBinding
import java.text.DecimalFormat


class PaymentItem(
    private val context: PaymentFragment,
    private val cart: Cart
) : BindableItem<ItemPaymentBinding>(){

    override fun getLayout() = R.layout.item_payment

    override fun bind(viewBinding: ItemPaymentBinding, position: Int) {
        viewBinding.cart = cart
        val mDecimalFormat = DecimalFormat("###,###,##0")
        if (cart.total_sales != 0){
            viewBinding.tvPriceSale.text = (mDecimalFormat.format(cart.sale_price?.toDouble()).toString()).replace(
                ",",
                "."
            ) + context.resources.getString(R.string.vnd)
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
    }
}