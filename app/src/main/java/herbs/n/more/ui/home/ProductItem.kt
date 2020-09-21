package herbs.n.more.ui.home

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
        viewBinding.tvProductName.text = product.quote
        Glide
            .with(context)
            .load(product.thumbnail)
            .centerCrop()
            .placeholder(R.drawable.logo_herbs)
            .into(viewBinding.ivProductImage);

        viewBinding.ivProductImage.setOnClickListener {
            listener.onItemClicked(product)
        }
    }
}

interface ProductItemListener {
    fun onItemClicked(product: Product)
}