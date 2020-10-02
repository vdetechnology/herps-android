package herbs.n.more.ui.viewholder;

import android.view.View;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.zhpan.bannerview.BaseViewHolder;
import com.zhpan.bannerview.utils.BannerUtils;

import herbs.n.more.R;
import herbs.n.more.data.db.entities.SlideImage;
import herbs.n.more.util.CornerImageView;

public class BannerViewHolder extends BaseViewHolder<SlideImage> {

    public BannerViewHolder(@NonNull View itemView) {
        super(itemView);
        CornerImageView imageView = findView(R.id.banner_image);
        imageView.setRoundCorner(BannerUtils.dp2px(0));
    }

    @Override
    public void bindData(SlideImage data, int position, int pageSize) {
        CornerImageView imageView = findView(R.id.banner_image);
        Glide.with(imageView).load(data.getUrl()).into(imageView);
        BannerUtils.log("NetViewHolder", "position:" + position);
    }
}
