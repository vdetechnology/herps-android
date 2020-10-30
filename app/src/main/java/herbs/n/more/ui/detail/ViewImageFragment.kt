package herbs.n.more.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.BaseViewHolder
import herbs.n.more.R
import herbs.n.more.databinding.FragmentViewImageBinding
import herbs.n.more.ui.BaseFragment
import herbs.n.more.ui.adapter.ZoomImagesAdapter
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein


class ViewImageFragment : BaseFragment() , KodeinAware {

    private lateinit var binding: FragmentViewImageBinding
    override val kodein by kodein()
    private var mViewPager: BannerViewPager<String, BaseViewHolder<String>>? = null
    private var images: List<String> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_image, container, false)
        binding.fragment = this
        initView()
        initData()

        return binding.root
    }

    private fun initView(){
        mViewPager = binding.root?.findViewById(R.id.banner_view)
        mViewPager?.apply {
            setIndicatorSliderRadius(resources.getDimensionPixelOffset(R.dimen.dp_4), resources.getDimensionPixelOffset(R.dimen.dp_4))
            setLifecycleRegistry(lifecycle)
            setAdapter(ZoomImagesAdapter())
        }?.create()
    }

    private fun initData(){
        images = arguments?.getStringArrayList("images")!!
        if (images.isNotEmpty()){
            mViewPager?.refreshData(images)
        }
        binding.tvTitle.text = arguments?.getString("title")!!
        mViewPager?.currentItem = arguments?.getInt("position")!!
    }
}