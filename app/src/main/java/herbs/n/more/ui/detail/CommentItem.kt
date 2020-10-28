package herbs.n.more.ui.detail

import android.app.Activity
import com.bumptech.glide.Glide
import com.xwray.groupie.databinding.BindableItem
import herbs.n.more.R
import herbs.n.more.data.db.entities.Comment
import herbs.n.more.databinding.ItemRatingBinding

class CommentItem(
    private  val context: Activity,
    private val comment: Comment
) : BindableItem<ItemRatingBinding>(){

    override fun getLayout() = R.layout.item_rating

    override fun bind(viewBinding: ItemRatingBinding, position: Int) {
        viewBinding.comment = comment
        viewBinding.rbRating.rating = comment.rating!!
        Glide
            .with(context)
            .load(comment.author_avatar_urls)
            .error(R.drawable.ic_avatar)
            .centerCrop()
            .into(viewBinding.ivUserAvatar);
    }
}