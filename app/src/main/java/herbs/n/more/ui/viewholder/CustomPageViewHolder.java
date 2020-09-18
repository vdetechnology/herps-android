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
        //TextView tvSkip = findView(R.id.tv_skip);
        setImageResource(R.id.banner_image, data.getImageRes());
       /* setOnClickListener(R.id.tv_skip, view -> {
            if (null != mOnSubViewClickListener)
                mOnSubViewClickListener.onViewClick(view, getAdapterPosition());
        });
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(tvSkip, "alpha", 0, 1);
        alphaAnimator.setDuration(1500);
        alphaAnimator.start();*/
    }

    public void setOnSubViewClickListener(OnSubViewClickListener subViewClickListener) {
        mOnSubViewClickListener = subViewClickListener;
    }

    public interface OnSubViewClickListener {
        void onViewClick(View view, int position);
    }
}
