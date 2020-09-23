package herbs.n.more.ui.home

import android.view.animation.AnimationUtils
import com.bumptech.glide.Glide
import com.xwray.groupie.databinding.BindableItem
import herbs.n.more.R
import herbs.n.more.data.db.entities.Product
import herbs.n.more.databinding.ItemProductSquareBinding

class ProductItem(
    private  val context: HomeFragment,
    private val product: Product,
    private val listener: ProductItemListener
) : BindableItem<ItemProductSquareBinding>(){

    override fun getLayout() = R.layout.item_product_square

    override fun bind(viewBinding: ItemProductSquareBinding, position: Int) {
        viewBinding.product = product
        Glide
            .with(context)
            .load(product.thumbnail)
            .centerCrop()
            .placeholder(R.drawable.logo_herbs)
            .into(viewBinding.ivProductImage);

        viewBinding.cvItem.setOnClickListener {
            listener.onItemClicked(product)
        }

        viewBinding.btLike.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(context.context, R.anim.image_click))
            viewBinding.btLike.setImageDrawable(context.resources.getDrawable(R.drawable.ic_liked))
            listener.onLikeClicked(product)
        }
    }
}

interface ProductItemListener {
    fun onItemClicked(product: Product)
    fun onLikeClicked(product: Product)
}