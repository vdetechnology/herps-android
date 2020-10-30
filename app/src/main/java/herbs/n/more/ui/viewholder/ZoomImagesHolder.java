package herbs.n.more.ui.viewholder;

import android.view.View;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.zhpan.bannerview.BaseViewHolder;
import herbs.n.more.R;

public class ZoomImagesHolder extends BaseViewHolder<String> {

    public ZoomImagesHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bindData(String data, int position, int pageSize) {
        PhotoView imageView = findView(R.id.banner_image);
        Glide.with(imageView).load(data).error(R.mipmap.ic_logo).into(imageView);
    }
}
