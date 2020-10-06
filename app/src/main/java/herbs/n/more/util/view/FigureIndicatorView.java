package herbs.n.more.util.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.zhpan.bannerview.utils.BannerUtils;
import com.zhpan.indicator.base.BaseIndicatorView;

/**
 * <pre>
 *   Created by zhangpan on 2019-10-18.
 *   Description:
 * </pre>
 */
public class FigureIndicatorView extends BaseIndicatorView {

    private int radius = BannerUtils.dp2px(20);

    private int backgroundColor = Color.parseColor("#CCF1F3F4");

    private int textColor = Color.BLACK;

    private int textSize = BannerUtils.dp2px(13);

    private Paint mPaint;

    public FigureIndicatorView(Context context) {
        this(context, null);
    }

    public FigureIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FigureIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(2 * radius, 2 * radius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getPageSize() > 1) {
            mPaint.setColor(backgroundColor);
            mPaint.setColor(textColor);
            mPaint.setTextSize(textSize);
            mPaint.setTextAlign(Paint.Align.CENTER);
            String text = getCurrentPosition() + 1 + "/" + getPageSize();
            int textWidth = (int) mPaint.measureText(text);
            Paint.FontMetricsInt fontMetricsInt = mPaint.getFontMetricsInt();
            int baseline = (getMeasuredHeight() - fontMetricsInt.bottom - fontMetricsInt.top) / 2;
            canvas.drawText(text, getWidth()/2, baseline, mPaint);
        }
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    public void setBackgroundColor(@ColorInt int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
}
