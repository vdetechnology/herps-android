package herbs.n.more.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.indicator.IndicatorView
import herbs.n.more.R
import herbs.n.more.data.db.entities.Product
import herbs.n.more.databinding.FragmentHomeBinding
import herbs.n.more.ui.MainActivity
import herbs.n.more.ui.adapter.ImageResourceAdapter
import herbs.n.more.ui.auth.AuthActivity
import herbs.n.more.ui.viewholder.ImageResourceViewHolder
import herbs.n.more.util.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*


class HomeFragment : Fragment(), KodeinAware, ProductItemListener, ProductRecentlyItemListener {

    private lateinit var binding: FragmentHomeBinding
    override val kodein by kodein()
    private val factory: BestSellingViewModelFactory by instance()
    private lateinit var viewModel: BestSellingViewModel

    private var mViewPager: BannerViewPager<Int, ImageResourceViewHolder>? = null
    private var mViewPagerAdvertisement: BannerViewPager<Int, ImageResourceViewHolder>? = null
    private lateinit var mIndicatorView : IndicatorView
    private lateinit var mIndicatorView2 : IndicatorView
    protected var mPictureList: MutableList<Int> = ArrayList()
    protected var mAvertisementList: MutableList<Int> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.searchButton.setOnClickListener { view -> onClickSearch(view) }
        binding.cartContainer.setOnClickListener { view -> onClickCart(view) }
        binding.tvMoreBestSelling.setOnClickListener { view -> seeMoreBestSelling(view) }
        binding.tvMoreRecently.setOnClickListener { view -> seeMoreBestRecently(view) }
        binding.rlUser.setOnClickListener { view -> goToLogin() }
        viewModel = ViewModelProviders.of(this, factory).get(BestSellingViewModel::class.java)
        binding.bestselling = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()
        swiperefresh.setOnRefreshListener {
            initData()
        }
    }

    private fun initData(){
        GlobalScope.async{setTimeText()}
        GlobalScope.async{setupViewPager(binding.root)}
        GlobalScope.async{setupSlideAdvertisement(binding.root)}
        GlobalScope.async{bindData()}
    }

    private fun setTimeText(){
        val currentDate = Calendar.getInstance().time
        if (!DateFormat.is24HourFormat(activity)) {
            if (currentDate.hours > 12 && currentDate.hours < 18) {
                tv_time_hi.text = getString(R.string.title_affternoon)
            } else if (currentDate.hours > 18) {
                tv_time_hi.text = getString(R.string.title_evening)
            }
        }
    }

    private fun setupViewPager(view: View) {
        mViewPager = view?.findViewById(R.id.banner_view)
        mIndicatorView = view?.findViewById(R.id.indicator_view)
        mIndicatorView.setVisibility(View.VISIBLE)
        mViewPager?.apply {
            setLifecycleRegistry(getLifecycle())
            setIndicatorView(mIndicatorView)
            setIndicatorSliderRadius(resources.getDimensionPixelOffset(R.dimen.dp_4), resources.getDimensionPixelOffset(R.dimen.dp_4))
            setLifecycleRegistry(getLifecycle())
            setOnPageClickListener{ position: Int -> pageClick(position) }
            adapter = ImageResourceAdapter(getResources().getDimensionPixelOffset(R.dimen.dp_0), R.layout.item_slide_mode)
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

    private fun setupSlideAdvertisement(view: View) {
        mViewPagerAdvertisement = view?.findViewById(R.id.banner_view_advertisement)
        mIndicatorView2 = view?.findViewById(R.id.indicator_view_2)
        mIndicatorView2.setVisibility(View.VISIBLE)
        mViewPagerAdvertisement?.apply {
            setLifecycleRegistry(getLifecycle())
            setIndicatorView(mIndicatorView2)
            setLifecycleRegistry(getLifecycle())
            setPageMargin(getResources().getDimensionPixelOffset(R.dimen.dp_0))
            setRevealWidth(getResources().getDimensionPixelOffset(R.dimen.dp_30))
            setIndicatorSliderRadius(resources.getDimensionPixelOffset(R.dimen.dp_3), resources.getDimensionPixelOffset(R.dimen.dp_3))
            setOnPageClickListener{ position: Int -> pageClick(position) }
            adapter = ImageResourceAdapter(getResources().getDimensionPixelOffset(R.dimen.dp_25), R.layout.item_slide_advertisement)
        }?.create(getAdvertisementList(3))
    }

    private fun getAdvertisementList(count:Int): MutableList<Int> {
        mAvertisementList.clear()
        for (i in 0..count) {
            val drawable = resources.getIdentifier("slide_$i", "mipmap", activity?.packageName)
            mAvertisementList.add(drawable)
        }
        return mAvertisementList;
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun bindData() = Coroutines.main {
        progress_bar.show()
        val bestSelling = viewModel.bestSelling.await()
        bestSelling.observe(this, androidx.lifecycle.Observer {
            progress_bar.hide()
            swiperefresh.isRefreshing = false
            initRecyclerView(it.toProductItem(), it.toRecentlyItem())
        })
    }

    private fun initRecyclerView(bestSellingItem: List<ProductItem>, recentlyItem: List<RecentlyProductItem>) {

        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(bestSellingItem)
        }

        val mSuggestedAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(bestSellingItem)
        }

        val mRecentlyAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(recentlyItem)
        }

        rv_best_selling.apply {
            layoutManager = GridLayoutManager(activity, 2, LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter
        }

        rv_suggested.apply {
            layoutManager = GridLayoutManager(activity, 2, LinearLayoutManager.VERTICAL, false)
            adapter = mSuggestedAdapter
        }

        rv_recently.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = mRecentlyAdapter
        }
        sv_home.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                mSuggestedAdapter.addAll(bestSellingItem)
            }
        })

    }

    private fun List<Product>.toProductItem() : List<ProductItem>{
        return this.map {
            ProductItem(this@HomeFragment, it, this@HomeFragment)
        }
    }

    private fun List<Product>.toRecentlyItem() : List<RecentlyProductItem>{
        return this.map {
            RecentlyProductItem(this@HomeFragment, it, this@HomeFragment)
        }
    }

    fun onClickSearch(v: View) {
        v.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.image_click))
    }

    fun onClickCart(v: View) {
        v.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.image_click))
    }

    fun seeMoreBestSelling(v: View) {
        v.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.image_click))
    }

    fun seeMoreBestRecently(v: View) {
        v.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.image_click))
    }

    fun goToLogin() {
        activity?.let {
            Intent(it, AuthActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(it)
            }
        }
    }

    protected fun pageClick(position: Int) {
        if (position != mViewPager!!.currentItem) {
            mViewPager!!.setCurrentItem(position, true)
        }
        Toast.makeText(activity, "position:$position", Toast.LENGTH_SHORT).show()
    }

    override fun onItemClicked(product: Product) {
        context?.toast(product.author)
    }

    override fun onLikeClicked(product: Product) {
        context?.toast(product.id.toString())
    }
}