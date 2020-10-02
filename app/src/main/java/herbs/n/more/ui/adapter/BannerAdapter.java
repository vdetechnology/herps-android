package herbs.n.more.ui.adapter;

import android.view.View;

import com.zhpan.bannerview.BaseBannerAdapter;
import com.zhpan.bannerview.BaseViewHolder;

import herbs.n.more.R;
import herbs.n.more.data.db.entities.SlideImage;
import herbs.n.more.ui.viewholder.BannerViewHolder;

public class BannerAdapter extends BaseBannerAdapter<SlideImage, BaseViewHolder<SlideImage>> {

    @Override
    protected void onBind(BaseViewHolder<SlideImage> holder, SlideImage data, int position, int pageSize) {
        holder.bindData(data, position, pageSize);
    }

    @Override
    public BaseViewHolder<SlideImage> createViewHolder(View itemView, int viewType) {
        return new BannerViewHolder(itemView);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_slide_mode;
    }
}

