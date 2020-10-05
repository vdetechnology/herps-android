package herbs.n.more.ui.detail

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.BaseViewHolder
import com.zhpan.indicator.base.IIndicator
import herbs.n.more.R
import herbs.n.more.data.db.entities.DetailProduct
import herbs.n.more.databinding.ActivityDetailBinding
import herbs.n.more.ui.adapter.DetailImagesAdapter
import herbs.n.more.util.view.FigureIndicatorView
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.text.DecimalFormat

class DetailActivity : AppCompatActivity(), KodeinAware, DetailListener {

    private lateinit var binding: ActivityDetailBinding
    override val kodein by kodein()
    private val factory: DetailViewModelFactory by instance()
    private lateinit var viewModel: DetailProductViewModel

    private var mViewPager: BannerViewPager<String, BaseViewHolder<String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        viewModel = ViewModelProviders.of(this, factory).get(DetailProductViewModel::class.java)
        viewModel.detailListener = this
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

    fun initView(){
        binding.swipeRefresh.setColorSchemeColors(resources.getColor(R.color.colorPrimary))
        binding.swipeRefresh.setOnRefreshListener {
            initData()
        }

        mViewPager = binding.root?.findViewById(R.id.banner_view)
        mViewPager?.apply {
            setIndicatorView(setupIndicatorView())
            setIndicatorSliderRadius(resources.getDimensionPixelOffset(R.dimen.dp_4), resources.getDimensionPixelOffset(R.dimen.dp_4))
            setLifecycleRegistry(getLifecycle())
            //setOnPageClickListener{ position: Int -> pageClick(position) }
            setAdapter(DetailImagesAdapter())
        }?.create()
    }

    private fun setupIndicatorView(): IIndicator? {
        val indicatorView = FigureIndicatorView(this)
        indicatorView.setTextSize(resources.getDimensionPixelSize(R.dimen.sp_15))
        return indicatorView
    }

    fun initData(){
        viewModel.getDetail(intent.getStringExtra("id").toString()).removeObservers(this);
        viewModel.getDetail(intent.getStringExtra("id").toString()).observe(this, Observer {
            binding.product = it
            binding.swipeRefresh.isRefreshing = false
            mViewPager?.refreshData(it.images)

            val mDecimalFormat = DecimalFormat("###,###,##0")
            binding.tvPrice.text = (mDecimalFormat.format(it.price?.toDouble()).toString()).replace(",", ".") +
                    resources.getString(R.string.vnd)
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
}