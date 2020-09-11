package herbs.n.more.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.constants.PageStyle
import com.zhpan.indicator.enums.IndicatorSlideMode
import herbs.n.more.R
import herbs.n.more.ui.adapter.ImageResourceAdapter
import herbs.n.more.ui.viewholder.ImageResourceViewHolder
import java.util.*

class HomeFragment : Fragment() {

    private var mViewPager: BannerViewPager<Int, ImageResourceViewHolder>? = null
    protected var mPictureList: MutableList<Int> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view  = inflater.inflate(R.layout.fragment_home, container, false)
        setupViewPager(view)
        return view
    }

    private fun setupViewPager(view: View) {
        mViewPager = view?.findViewById(R.id.banner_view)
        mViewPager?.apply {
            setPageMargin(getResources().getDimensionPixelOffset(R.dimen.dp_10))
            setRevealWidth(getResources().getDimensionPixelOffset(R.dimen.dp_8))
            setPageStyle(PageStyle.MULTI_PAGE_SCALE)
            setIndicatorSlideMode(IndicatorSlideMode.SCALE)
            setIndicatorSliderColor(resources.getColor(R.color.colorIndicator), resources.getColor(R.color.colorMenu))
            setIndicatorSliderRadius(resources.getDimensionPixelOffset(R.dimen.dp_4), resources.getDimensionPixelOffset(R.dimen.dp_5))
            setLifecycleRegistry(getLifecycle())
            setOnPageClickListener{ position: Int -> pageClick(position) }
            adapter = ImageResourceAdapter(getResources().getDimensionPixelOffset(R.dimen.dp_8))
            setInterval(5000);
        }?.create(getPicList(3))
    }

    private fun getPicList(count:Int): MutableList<Int> {
        mPictureList.clear()
        for (i in 1..count) {
            val drawable = resources.getIdentifier("slide_$i", "mipmap", activity?.packageName)
            mPictureList.add(drawable)
        }
        return mPictureList;
    }

    protected fun pageClick(position: Int) {
        if (position != mViewPager!!.currentItem) {
            mViewPager!!.setCurrentItem(position, true)
        }
        Toast.makeText(activity, "position:$position", Toast.LENGTH_SHORT).show()
    }

}