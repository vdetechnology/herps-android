package herbs.n.more.ui.home

import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AnimationUtils
import com.bumptech.glide.Glide
import com.xwray.groupie.databinding.BindableItem
import herbs.n.more.R
import herbs.n.more.data.db.entities.Product
import herbs.n.more.databinding.ItemProductRectangleBinding
import java.text.DecimalFormat

class RecentlyProductItem(
    private  val context: HomeFragment,
    private val product: Product,
    private val listener: ProductRecentlyItemListener
) : BindableItem<ItemProductRectangleBinding>(){
    override fun getLayout() = R.layout.item_product_rectangle

    override fun bind(viewBinding: ItemProductRectangleBinding, position: Int) {
        val mDecimalFormat = DecimalFormat("###,###,##0")
        viewBinding.tvPrice.text = (mDecimalFormat.format(product.price?.toDouble()).toString()).replace(",", ".") +
                context.resources.getString(R.string.vnd)
        if (product.total_sales != 0){
            viewBinding.tvPriceSale.text = (mDecimalFormat.format(product.sale_price?.toDouble()).toString()).replace(",", ".") +
                    context.resources.getString(R.string.vnd)
            viewBinding.tvPriceSale.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewBinding.tvPriceSale.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    viewBinding.tvPriceSale.height //height is ready
                    val params: ViewGroup.LayoutParams = viewBinding.line.layoutParams
                    params.width = viewBinding.tvPriceSale.width
                    viewBinding.line.layoutParams = params;
                }
            })
        }
        viewBinding.product = product
        viewBinding.rbRating.rating = product.rating!!
        Glide
            .with(context)
            .load(product.image)
            .centerCrop()
            .into(viewBinding.ivProductImage);

        viewBinding.cvItem.setOnClickListener {
            listener.onItemClicked(product)
        }

        viewBinding.btLike.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(context.context, R.anim.image_click))
            //viewBinding.btLike.setImageDrawable(context.resources.getDrawable(R.drawable.ic_liked))
            listener.onLikeClicked(product)
        }
    }
}

interface ProductRecentlyItemListener {
    fun onItemClicked(product: Product)
    fun onLikeClicked(product: Product)
}