package herbs.n.more.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.BaseViewHolder
import herbs.n.more.R
import herbs.n.more.data.db.entities.DetailProduct
import herbs.n.more.data.db.entities.Product
import herbs.n.more.data.db.entities.User
import herbs.n.more.databinding.ActivityDetailBinding
import herbs.n.more.ui.adapter.DetailImagesAdapter
import herbs.n.more.ui.dialog.ConfirmLoginDialog
import herbs.n.more.ui.home.ProductItem
import herbs.n.more.ui.home.ProductItemListener
import herbs.n.more.util.Coroutines
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.text.DecimalFormat
import java.util.*

class DetailActivity : AppCompatActivity(), KodeinAware, DetailListener, ProductItemListener {

    private lateinit var binding: ActivityDetailBinding
    override val kodein by kodein()
    private val factory: DetailViewModelFactory by instance()
    private lateinit var viewModel: DetailProductViewModel

    private var mSuggestedAdapter = GroupAdapter<GroupieViewHolder>()
    private var pageindex : Int = 1
    private var loadmore : Boolean = false
    private var loadmoreDisable : Boolean = false
    private var mViewPager: BannerViewPager<String, BaseViewHolder<String>>? = null
    private var user : User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        viewModel = ViewModelProviders.of(this, factory).get(DetailProductViewModel::class.java)
        viewModel.detailListener = this
        binding.activity = this
        initView()
        initData()
    }

    override fun onStarted() {
        binding.rlLoading.visibility = View.VISIBLE
    }

    override fun onSuccess(detailProduct: DetailProduct) {
        binding.rlLoading.visibility = View.GONE
    }

    override fun onFailure(message: String) {
        binding.rlLoading.visibility = View.GONE
    }

    private fun initView(){
        binding.swipeRefresh.setColorSchemeColors(resources.getColor(R.color.colorPrimary))
        binding.swipeRefresh.setOnRefreshListener {
            mSuggestedAdapter.clear()
            loadmoreDisable = false
            loadmore = false
            pageindex = 1
            initData()
        }

        mViewPager = binding.root?.findViewById(R.id.banner_view)
        mViewPager?.apply {
            setIndicatorSliderRadius(resources.getDimensionPixelOffset(R.dimen.dp_4), resources.getDimensionPixelOffset(R.dimen.dp_4))
            setLifecycleRegistry(getLifecycle())
            //setOnPageClickListener{ position: Int -> pageClick(position) }
            setAdapter(DetailImagesAdapter())
        }?.create()
        setupIndicatorView()

        binding.svHome.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                if (!loadmoreDisable) {
                    pageindex += 1
                    loadmore = true
                    loadMorePopular()
                }
            }
        })
    }

    private fun setupIndicatorView() {
        mViewPager?.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tvNumberPage.text = (position + 1).toString() + "/" + mViewPager!!.data.size
            }
        })
    }

    private fun initData(){
        user = intent.getSerializableExtra("user") as User?
        GlobalScope.async{bindDataPopular()}
        GlobalScope.also{bindDataDetail()}
    }

    private fun bindDataDetail(){
        viewModel.getDetail(intent.getStringExtra("id").toString()).removeObservers(this)
        viewModel.getDetail(intent.getStringExtra("id").toString()).observe(this, Observer {
            binding.product = it
            binding.swipeRefresh.isRefreshing = false
            mViewPager?.refreshData(it.images)
            if (it.images?.size!! > 1) {
                binding.tvNumberPage.text = (mViewPager!!.currentItem + 1).toString() + "/" + it.images?.size!!
            }else{
                binding.tvNumberPage.visibility = View.GONE
            }
            val mDecimalFormat = DecimalFormat("###,###,##0")
            binding.tvPrice.text = (mDecimalFormat.format(it.price?.toDouble()).toString()).replace(",", ".") +
                    resources.getString(R.string.vnd)
            binding.rbRating.rating = it.rating!!
            binding.rbRating1.rating = it.rating!!
            if (it.total_sales != 0){
                binding.tvPriceSale.text = (mDecimalFormat.format(it.sale_price?.toDouble()).toString()).replace(",", ".") +
                        resources.getString(R.string.vnd)
                binding.tvPriceSale.viewTreeObserver.addOnGlobalLayoutListener(object :
                    ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        binding.tvPriceSale.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        binding.tvPriceSale.height //height is ready
                        val params: ViewGroup.LayoutParams = binding.line.layoutParams
                        params.width = binding.tvPriceSale.width
                        binding.line.layoutParams = params;
                    }
                })
            }
        })
    }

    private fun bindDataPopular() = Coroutines.main {
        viewModel.getPopular(pageindex).removeObservers(this);
        viewModel.getPopular(pageindex).observe(this, androidx.lifecycle.Observer {
            if (!loadmore) {
                mSuggestedAdapter = GroupAdapter<GroupieViewHolder>().apply {
                    if (it.isNotEmpty()) {
                        addAll(it.toProductItem())
                    }
                }
                binding.rvSuggested.apply {
                    layoutManager = GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false)
                    adapter = mSuggestedAdapter
                }
            }
        })
    }

    private fun loadMorePopular() = Coroutines.main {
        viewModel.getPopular(pageindex - 1).removeObservers(this);
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
            ProductItem(this@DetailActivity, it, this@DetailActivity)
        }
    }

    fun onBackClick(view: View){
        finish()
    }

    fun onSeeMoreClick(view: View){
        binding.tvDescription.maxLines = 2000
        binding.tvSeeMore.visibility = View.GONE
        binding.tvSeeLess.visibility = View.VISIBLE
    }

    fun onSeeLessClick(view: View){
        binding.tvDescription.maxLines = 6
        binding.tvSeeMore.visibility = View.VISIBLE
        binding.tvSeeLess.visibility = View.GONE
        val scrollTo: Int = (binding.tvDescription.parent as View).top + binding.tvDescription.top
        binding.svHome.smoothScrollTo(0, scrollTo);
    }

    override fun onItemClicked(product: Product) {
        val calendar = Calendar.getInstance()
        val startTime = calendar.timeInMillis
        product.update_date = startTime
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("id", product.id.toString())
        }
        startActivity(intent)
    }

    override fun onLikeClicked(product: Product) {
        if (user != null){

        }else {
            val dialog : ConfirmLoginDialog? = ConfirmLoginDialog(this)
            dialog?.show()
        }
    }

    fun onLikeDetail() {
        if (user != null){

        }else {
            val dialog : ConfirmLoginDialog? = ConfirmLoginDialog(this)
            dialog?.show()
        }
    }
}