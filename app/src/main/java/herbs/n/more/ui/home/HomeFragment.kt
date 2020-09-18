package herbs.n.more.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.constants.PageStyle
import com.zhpan.indicator.IndicatorView
import com.zhpan.indicator.enums.IndicatorSlideMode
import herbs.n.more.R
import herbs.n.more.databinding.FragmentHomeBinding
import herbs.n.more.ui.adapter.ImageResourceAdapter
import herbs.n.more.ui.viewholder.ImageResourceViewHolder
import java.util.*


class HomeFragment : Fragment() {

    private var mViewPager: BannerViewPager<Int, ImageResourceViewHolder>? = null
    private lateinit var mIndicatorView : IndicatorView
    protected var mPictureList: MutableList<Int> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.searchButton.setOnClickListener { view -> onClickSearch(view) }
        binding.cartContainer.setOnClickListener { view -> onClickSearch(view) }
        setupViewPager(binding.root)
        return binding.root
    }

    private fun setupViewPager(view: View) {
        mViewPager = view?.findViewById(R.id.banner_view)
        mIndicatorView = view?.findViewById(R.id.indicator_view)
        mIndicatorView.setVisibility(View.VISIBLE)
        mViewPager?.apply {
            setPageMargin(getResources().getDimensionPixelOffset(R.dimen.dp_0))
            setRevealWidth(getResources().getDimensionPixelOffset(R.dimen.dp_30))
            setPageStyle(PageStyle.MULTI_PAGE_SCALE)
            setIndicatorVisibility(View.GONE)
            setIndicatorSlideMode(IndicatorSlideMode.WORM)
            setIndicatorSliderColor(resources.getColor(R.color.colorIndicatorInActive), resources.getColor(R.color.colorPrimary))
            setIndicatorSliderRadius(resources.getDimensionPixelOffset(R.dimen.dp_4), resources.getDimensionPixelOffset(R.dimen.dp_4))
            setIndicatorView(mIndicatorView)
            setLifecycleRegistry(getLifecycle())
            setOnPageClickListener{ position: Int -> pageClick(position) }
            adapter = ImageResourceAdapter(getResources().getDimensionPixelOffset(R.dimen.dp_20))
            setInterval(5000);
        }?.create(getPicList(3))
    }

    private fun getPicList(count:Int): MutableList<Int> {
        mPictureList.clear()
        for (i in 0..count) {
            val drawable = resources.getIdentifier("slide_$i", "mipmap", activity?.packageName)
            mPictureList.add(drawable)
        }
        return mPictureList;
    }

    fun onClickSearch(v: View) {
        v.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.image_click))
    }

    protected fun pageClick(position: Int) {
        if (position != mViewPager!!.currentItem) {
            mViewPager!!.setCurrentItem(position, true)
        }
        Toast.makeText(activity, "position:$position", Toast.LENGTH_SHORT).show()
    }

}