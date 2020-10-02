package herbs.n.more.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.BaseViewHolder
import com.zhpan.indicator.IndicatorView
import herbs.n.more.R
import herbs.n.more.data.db.entities.Product
import herbs.n.more.data.db.entities.SlideImage
import herbs.n.more.data.db.entities.User
import herbs.n.more.databinding.FragmentHomeBinding
import herbs.n.more.ui.adapter.BannerAdapter
import herbs.n.more.ui.adapter.CampaignAdapter
import herbs.n.more.ui.auth.AuthActivity
import herbs.n.more.ui.dialog.ConfirmLoginDialog
import herbs.n.more.util.Coroutines
import herbs.n.more.util.hide
import herbs.n.more.util.show
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

    private var mViewPager: BannerViewPager<SlideImage, BaseViewHolder<SlideImage>>? = null
    private var mViewPagerCampaign: BannerViewPager<SlideImage, BaseViewHolder<SlideImage>>? = null
    private lateinit var mIndicatorView : IndicatorView
    private lateinit var mIndicatorView2 : IndicatorView
    private var user : User? = null
    private var mSuggestedAdapter = GroupAdapter<GroupieViewHolder>()
    private var pageindex : Int = 1
    private var loadmore : Boolean = false
    private var loadmoreDisable : Boolean = false

    @RequiresApi(Build.VERSION_CODES.M)
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
        viewModel.user.observe(viewLifecycleOwner, androidx.lifecycle.Observer { user ->
            this.user = user
            if (user != null) {
                binding.tvUserName.setTextAppearance(R.style.ColorMainTextBold15)
            }else{
                binding.tvUserName.setTextAppearance(R.style.ColorMainTextRegular15)
            }
        })
        binding.swiperefresh.setColorSchemeColors(resources.getColor(R.color.colorPrimary))
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.swiperefresh.setOnRefreshListener {
            mSuggestedAdapter.clear()
            loadmoreDisable = false
            loadmore = false
            pageindex = 1
            initData()
        }
        initData()
    }

    private fun initData(){
        GlobalScope.async{setTimeText()}
        GlobalScope.async{setupViewPager(binding.root)}
        GlobalScope.async{setupSlideCampaign(binding.root)}
        GlobalScope.async{bindDataBestSelling()}
        GlobalScope.async{bindBanners()}
        GlobalScope.async{bindCampaigns()}
        GlobalScope.async{bindDataRecently()}
        GlobalScope.async{bindDataPopular()}
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

    private fun setupViewPager(view: View){
        mViewPager = view?.findViewById(R.id.banner_view)
        mIndicatorView = view?.findViewById(R.id.indicator_view)
        mIndicatorView.setVisibility(View.VISIBLE)
        mViewPager?.apply {
            setIndicatorView(mIndicatorView)
            setIndicatorSliderRadius(resources.getDimensionPixelOffset(R.dimen.dp_4), resources.getDimensionPixelOffset(R.dimen.dp_4))
            setLifecycleRegistry(getLifecycle())
            setOnPageClickListener{ position: Int -> pageClick(position) }
            setAdapter(BannerAdapter())
        }?.create()
    }


    private fun setupSlideCampaign(view: View) {
        mViewPagerCampaign = view?.findViewById(R.id.banner_view_advertisement)
        mIndicatorView2 = view?.findViewById(R.id.indicator_view_2)
        mIndicatorView2.setVisibility(View.VISIBLE)
        mViewPagerCampaign?.apply {
            setIndicatorView(mIndicatorView2)
            setLifecycleRegistry(getLifecycle())
            setPageMargin(getResources().getDimensionPixelOffset(R.dimen.dp_0))
            setRevealWidth(getResources().getDimensionPixelOffset(R.dimen.dp_30))
            setIndicatorSliderRadius(resources.getDimensionPixelOffset(R.dimen.dp_3), resources.getDimensionPixelOffset(R.dimen.dp_3))
            setOnPageClickListener{ position: Int -> pageClick(position) }
            setAdapter(CampaignAdapter())
        }?.create()
    }


    @SuppressLint("FragmentLiveDataObserve")
    private fun bindDataBestSelling() = Coroutines.main {
        progress_bar.show()
        val bestSelling = viewModel.bestSelling.await()
        bestSelling.observe(this, androidx.lifecycle.Observer {
            progress_bar.hide()
            swiperefresh.isRefreshing = false
            val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
                addAll(it.toProductItem())
            }

            rv_best_selling.apply {
                layoutManager = GridLayoutManager(activity, 2, LinearLayoutManager.VERTICAL, false)
                adapter = mAdapter
            }
        })
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun bindDataRecently() = Coroutines.main {
        val recentlys = viewModel.recentlys.await()
        recentlys.observe(this, androidx.lifecycle.Observer {
            if (it.isNotEmpty()) {
                rv_recently.visibility = View.VISIBLE
                tv_more_recently.visibility = View.VISIBLE
                tv_recently.visibility = View.VISIBLE
                swiperefresh.isRefreshing = false
                val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
                    addAll(it.toRecentlyItem())
                }

                rv_recently.apply {
                    layoutManager =
                        LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                    adapter = mAdapter
                }
            }else{
                rv_recently.visibility = View.GONE
                tv_more_recently.visibility = View.GONE
                tv_recently.visibility = View.GONE
            }
        })
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun bindDataPopular() = Coroutines.main {
        viewModel.getPopular(pageindex).removeObservers(this);
        viewModel.getPopular(pageindex).observe(this, androidx.lifecycle.Observer {
            if (!loadmore) {
                mSuggestedAdapter = GroupAdapter<GroupieViewHolder>().apply {
                    if (it.isNotEmpty()) {
                        addAll(it.toProductItem())
                    }
                }
                rv_suggested.apply {
                    layoutManager = GridLayoutManager(activity, 2, LinearLayoutManager.VERTICAL, false)
                    adapter = mSuggestedAdapter
                }
                sv_home.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                    if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                        if (!loadmoreDisable) {
                            pageindex += 1
                            loadmore = true
                            loadMorePopular()
                        }
                    }
                })
            }
        })
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun loadMorePopular() = Coroutines.main {
        viewModel.getPopular(pageindex).removeObservers(this);
        viewModel.getPopular(pageindex).observe(this, androidx.lifecycle.Observer {
            if (loadmore) {
                if (it.isNotEmpty()) {
                    mSuggestedAdapter.addAll(it.toProductItem())
                } else {
                    loadmoreDisable = true
                }
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

    @SuppressLint("FragmentLiveDataObserve")
    private fun bindBanners() = Coroutines.main {
        val banners = viewModel.banners.await()
        banners.observe(this, androidx.lifecycle.Observer {
            swiperefresh.isRefreshing = false
            mViewPager?.refreshData(it)
        })
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun bindCampaigns() = Coroutines.main {
        val campaigns = viewModel.campaigns.await()
        campaigns.observe(this, androidx.lifecycle.Observer {
            swiperefresh.isRefreshing = false
            mViewPagerCampaign?.refreshData(it)
        })
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
        if (user != null){

        }else {
            activity?.let {
                Intent(it, AuthActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(it)
                }
            }
        }
    }

    protected fun pageClick(position: Int) {
        if (position != mViewPager!!.currentItem) {
            mViewPager!!.setCurrentItem(position, true)
        }
    }

    override fun onItemClicked(product: Product) {
        val calendar = Calendar.getInstance()
        val startTime = calendar.timeInMillis
        product.update_date = startTime
        viewModel.saveRecentlys(product)
    }

    override fun onLikeClicked(product: Product) {
        if (user != null){

        }else {
            val dialog : ConfirmLoginDialog? = activity?.let { ConfirmLoginDialog(it) }
            dialog?.show()
        }
    }
}