package herbs.n.more.ui.adapter

import android.view.View
import com.zhpan.bannerview.BaseBannerAdapter
import herbs.n.more.R
import herbs.n.more.ui.bean.CustomBean
import herbs.n.more.ui.viewholder.CustomPageViewHolder


class WelcomeAdapter : BaseBannerAdapter<CustomBean, CustomPageViewHolder>() {

    var mOnSubViewClickListener: CustomPageViewHolder.OnSubViewClickListener? = null

    override fun onBind(holder: CustomPageViewHolder, data: CustomBean, position: Int, pageSize: Int) {
        holder.bindData(data, position, pageSize)
    }

    override fun createViewHolder(itemView: View, viewType: Int): CustomPageViewHolder? {
        val customPageViewHolder = CustomPageViewHolder(itemView)
        customPageViewHolder.setOnSubViewClickListener(mOnSubViewClickListener)
        return customPageViewHolder
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_custom_view
    }
}
