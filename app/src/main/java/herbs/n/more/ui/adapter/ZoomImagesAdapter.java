package herbs.n.more.ui.adapter;

import android.view.View;
import com.zhpan.bannerview.BaseBannerAdapter;
import com.zhpan.bannerview.BaseViewHolder;
import herbs.n.more.R;
import herbs.n.more.ui.viewholder.ZoomImagesHolder;

public class ZoomImagesAdapter extends BaseBannerAdapter<String, BaseViewHolder<String>> {

    @Override
    protected void onBind(BaseViewHolder<String> holder, String data, int position, int pageSize) {
        holder.bindData(data, position, pageSize);
    }

    @Override
    public BaseViewHolder<String> createViewHolder(View itemView, int viewType) {
        return new ZoomImagesHolder(itemView);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_slide_zoom;
    }
}

