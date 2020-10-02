package herbs.n.more.ui.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import com.zhpan.bannerview.BaseViewHolder;

import herbs.n.more.R;
import herbs.n.more.util.bean.CustomBean;

public class CustomPageViewHolder extends BaseViewHolder<CustomBean> {

    private OnSubViewClickListener mOnSubViewClickListener;

    public CustomPageViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bindData(CustomBean data, int position, int pageSize) {
        setImageResource(R.id.banner_image, data.getImageRes());
    }

    public void setOnSubViewClickListener(OnSubViewClickListener subViewClickListener) {
        mOnSubViewClickListener = subViewClickListener;
    }

    public interface OnSubViewClickListener {
        void onViewClick(View view, int position);
    }
}
