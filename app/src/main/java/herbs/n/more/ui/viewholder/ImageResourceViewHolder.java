package herbs.n.more.ui.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import com.zhpan.bannerview.BaseViewHolder;

import herbs.n.more.R;
import herbs.n.more.util.CornerImageView;

public class ImageResourceViewHolder extends BaseViewHolder<Integer> {

    public ImageResourceViewHolder(@NonNull View itemView, int roundCorner) {
        super(itemView);
        CornerImageView imageView = findView(R.id.banner_image);
        imageView.setRoundCorner(roundCorner);
    }

    @Override
    public void bindData(Integer data, int position, int pageSize) {
        setImageResource(R.id.banner_image, data);
    }
}
