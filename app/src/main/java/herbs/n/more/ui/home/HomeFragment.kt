package herbs.n.more.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.constants.PageStyle
import com.zhpan.indicator.IndicatorView
import com.zhpan.indicator.enums.IndicatorSlideMode
import herbs.n.more.R
import herbs.n.more.data.db.entities.Product
import herbs.n.more.databinding.FragmentHomeBinding
import herbs.n.more.ui.adapter.ImageResourceAdapter
import herbs.n.more.ui.viewholder.ImageResourceViewHolder
import herbs.n.more.util.Coroutines
import herbs.n.more.util.hide
import herbs.n.more.util.show
import herbs.n.more.util.toast
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*
import kotlin.system.measureTimeMillis


class HomeFragment : Fragment(), KodeinAware, ProductItemListener {

    private lateinit var binding: FragmentHomeBinding
    override val kodein by kodein()
    private val factory: BestSellingViewModelFactory by instance()
    private lateinit var viewModel: BestSellingViewModel

    private var mViewPager: BannerViewPager<Int, ImageResourceViewHolder>? = null
    private lateinit var mIndicatorView : IndicatorView
    protected var mPictureList: MutableList<Int> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.searchButton.setOnClickListener { view -> onClickSearch(view) }
        binding.cartContainer.setOnClickListener { view -> onClickSearch(view) }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, factory).get(BestSellingViewModel::class.java)
        val time = measureTimeMillis {
            GlobalScope.async{setupViewPager(binding.root)}
            GlobalScope.async{bindUI()}
        }
        println("Completed in $time ms")
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

    @SuppressLint("FragmentLiveDataObserve")
    private fun bindUI() = Coroutines.main {
        progress_bar.show()
        val bestSelling = viewModel.bestSelling.await()
        bestSelling.observe(this, androidx.lifecycle.Observer {
            progress_bar.hide()
            initRecyclerView(it.toQuoteItem())
        })
    }

    private fun initRecyclerView(quoteItem: List<ProductItem>) {

        val mAdapter = GroupAdapter<ViewHolder>().apply {
            addAll(quoteItem)
        }

        rv_best_selling.apply {
            layoutManager = GridLayoutManager(
                activity,
                2,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = mAdapter
        }

    }

    private fun List<Product>.toQuoteItem() : List<ProductItem>{
        return this.map {
            ProductItem(this@HomeFragment, it, this@HomeFragment)
        }
    }

    override fun onItemClicked(product: Product) {
        context?.toast(product.author)
    }
}