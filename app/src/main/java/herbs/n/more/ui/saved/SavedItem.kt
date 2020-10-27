package herbs.n.more.ui.saved

import android.app.Activity
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.xwray.groupie.databinding.BindableItem
import herbs.n.more.R
import herbs.n.more.data.db.entities.Product
import herbs.n.more.databinding.ItemSavedBinding
import java.text.DecimalFormat


class SavedItem(
    private  val context: Activity,
    private val product: Product,
    private val listener: SavedItemListener
) : BindableItem<ItemSavedBinding>(){

    override fun getLayout() = R.layout.item_saved

    override fun bind(viewBinding: ItemSavedBinding, position: Int) {
        val mDecimalFormat = DecimalFormat("###,###,##0")

        if (product.total_sales != 0){
            viewBinding.tvPriceSale.text = (mDecimalFormat.format(product.sale_price?.toDouble()).toString()).replace(",", ".") + context.resources.getString(R.string.vnd)
            viewBinding.tvPrice.text = (mDecimalFormat.format(product.price?.toDouble()).toString()).replace(",", ".") + context.resources.getString(R.string.vnd)
            viewBinding.tvPrice.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewBinding.tvPrice.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    viewBinding.tvPrice.height //height is ready
                    val params: ViewGroup.LayoutParams = viewBinding.line.layoutParams
                    params.width = viewBinding.tvPrice.width
                    viewBinding.line.layoutParams = params;
                }
            })
        }else{
            viewBinding.tvPriceSale.text = (mDecimalFormat.format(product.price?.toDouble()).toString()).replace(",", ".") + context.resources.getString(R.string.vnd)
        }

        viewBinding.product = product
        viewBinding.rbRating.rating = product.rating!!
        Glide
            .with(context)
            .load(product.image)
            .error(R.mipmap.ic_logo)
            .centerCrop()
            .into(viewBinding.ivProductImage);
        Glide
            .with(context)
            .load(product.image)
            .error(R.mipmap.ic_logo)
            .centerCrop()
            .into(viewBinding.ivProduct);

        viewBinding.cvItem.setOnClickListener {
            listener.onItemClicked(product)
        }

        viewBinding.cvDelete.setOnClickListener {
            listener.onDeleteClicked(product)
        }

        viewBinding.btAddCart.setOnClickListener {
            listener.onAddToCartClicked(product, viewBinding.ivProduct)
        }
    }
}

interface SavedItemListener {
    fun onItemClicked(product: Product)
    fun onDeleteClicked(product: Product)
    fun onAddToCartClicked(product: Product, imageView: ImageView)
}