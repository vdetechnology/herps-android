package herbs.n.more.ui.adapter;

import android.view.View;

import com.zhpan.bannerview.BaseBannerAdapter;

import herbs.n.more.R;
import herbs.n.more.ui.viewholder.ImageResourceViewHolder;

public class ImageResourceAdapter extends BaseBannerAdapter<Integer, ImageResourceViewHolder> {

    private int roundCorner;
    private int layoutId;

    public ImageResourceAdapter(int roundCorner, int layoutID) {
        this.roundCorner = roundCorner;
        this.layoutId = layoutID;
    }


    @Override
    protected void onBind(ImageResourceViewHolder holder, Integer data, int position, int pageSize) {
        holder.bindData(data, position, pageSize);
    }

    @Override
    public ImageResourceViewHolder createViewHolder(View itemView, int viewType) {
        return new ImageResourceViewHolder(itemView, roundCorner);
    }

    @Override
    public int getLayoutId(int viewType) {
        return layoutId;
    }
}
