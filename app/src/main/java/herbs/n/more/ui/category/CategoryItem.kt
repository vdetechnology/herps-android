package herbs.n.more.ui.category

import com.bumptech.glide.Glide
import com.xwray.groupie.databinding.BindableItem
import herbs.n.more.R
import herbs.n.more.data.db.entities.Category
import herbs.n.more.databinding.ItemCategoryBinding

class CategoryItem(
    private  val context: CategoryFragment,
    private val category: Category
) : BindableItem<ItemCategoryBinding>(){

    override fun getLayout() = R.layout.item_category

    override fun bind(viewBinding: ItemCategoryBinding, position: Int) {
        Glide
            .with(context)
            .load(category.image)
            .error(R.mipmap.ic_logo)
            .centerCrop()
            .into(viewBinding.ivCategory);

        viewBinding.cvCategory.setOnClickListener {
            context.goToResult(category.id.toString())
        }
    }
}