package herbs.n.more.ui.saved

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import herbs.n.more.R
import herbs.n.more.data.db.entities.Cart
import herbs.n.more.data.db.entities.Product
import herbs.n.more.data.db.entities.User
import herbs.n.more.databinding.FragmentSavedBinding
import herbs.n.more.ui.BaseFragment
import herbs.n.more.ui.MainActivity
import herbs.n.more.ui.detail.DetailActivity
import herbs.n.more.ui.home.BestSellingListener
import herbs.n.more.util.Constant
import herbs.n.more.util.Coroutines
import herbs.n.more.util.view.CircleAnimationUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*

class SavedFragment  : BaseFragment(), KodeinAware, BestSellingListener, SavedItemListener{

    private lateinit var binding: FragmentSavedBinding
    override val kodein by kodein()
    private val factory: SavedViewModelFactory by instance()
    private lateinit var viewModel: SavedViewModel

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_saved, container, false)
        viewModel = ViewModelProviders.of(this, factory).get(SavedViewModel::class.java)
        binding.fragment = this
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
        initData()
    }

    private fun initView(){

    }

    private fun initData(){
        GlobalScope.async{bindCountCart()}
        GlobalScope.async{bindDataPopular()}
    }

    private fun bindCountCart() = Coroutines.main {
        viewModel.countCart.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it > 0) {
                binding.tvNumberInCart.visibility = View.VISIBLE
                binding.tvNumberInCart.text = it.toString()
            } else {
                binding.tvNumberInCart.visibility = View.GONE
            }
        })
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun bindDataPopular() = Coroutines.main {
        viewModel.getPopular(pageindex)?.let {
            if (!loadmore) {
                mSuggestedAdapter = GroupAdapter<GroupieViewHolder>().apply {
                    if (it.isNotEmpty()) {
                        addAll(it.toSavedItem())
                    }
                }
                binding.rvProduct.apply {
                    layoutManager = GridLayoutManager(
                        activity,
                        2,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                    adapter = mSuggestedAdapter
                }
                /*binding.svCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                    if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                        if (!loadmoreDisable) {
                            pageindex += 1
                            loadmore = true
                            binding.pbLoadMore.visibility = View.VISIBLE
                            loadMorePopular()
                        }
                    }
                })*/
            }
        }
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun loadMorePopular() = Coroutines.main {
        viewModel.getPopular(pageindex)?.let {
            if (loadmore) {
                if (it.isNotEmpty()) {
                    mSuggestedAdapter.addAll(it.toSavedItem())
                } else {
                    loadmoreDisable = true
                }
                binding.pbLoadMore.visibility = View.GONE
            }
        }
    }

    private fun List<Product>.toSavedItem() : List<SavedItem>{
        return this.map {
            SavedItem(requireActivity(), it, this@SavedFragment)
        }
    }

    override fun onItemClicked(product: Product) {
        val calendar = Calendar.getInstance()
        val startTime = calendar.timeInMillis
        product.update_date = startTime
        viewModel.saveRecentlys(product)
        val intent = Intent(activity, DetailActivity::class.java).apply {
            putExtra("id", product.id.toString())
            putExtra("user", user)
        }
        startActivity(intent)
        activity?.overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    override fun onDeleteClicked(product: Product) {
    }

    override fun onAddToCartClicked(product: Product, imageView: ImageView) {
        makeFlyAnimation(imageView, product)
    }

    private fun makeFlyAnimation(targetView: ImageView, product: Product) {
        val destView = binding.btCart
        CircleAnimationUtil().attachActivity(activity).setTargetView(targetView).setMoveDuration(500)
            .setDestView(destView).setAnimationListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    saveCart(product)
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            }).startAnimation()
    }

    private fun saveCart(product: Product)  = Coroutines.main {
        if (product != null) {
            var cart = viewModel.cartByID(product?.id!!)
            if (cart != null) {
                cart.amount = cart.amount?.plus(1)
                if (product.total_sales!! > 0) {
                    cart.total_order = cart.amount?.times(cart.sale_price!!)
                } else {
                    cart.total_order = cart.amount?.times(cart.price!!)
                }
                viewModel.saveCart(cart)
            } else {
                val calendar = Calendar.getInstance()
                val cartNew = Cart()
                cartNew.id = product?.id
                cartNew.title = product?.title
                cartNew.image = product?.image
                cartNew.price = product?.price?.toDouble()
                cartNew.total_sales = product?.total_sales
                cartNew.total_sales_percent = product?.total_sales_percent
                cartNew.amount = 1
                cartNew.update_date = calendar.timeInMillis
                if (product?.total_sales!! > 0) {
                    cartNew.total_order = product?.sale_price?.toDouble()
                    cartNew.sale_price = product?.sale_price?.toDouble()
                } else {
                    cartNew.total_order = product?.price?.toDouble()
                }
                viewModel.saveCart(cartNew)
            }
        }
    }

    private fun View.getLocationOnScreen(): Point
    {
        val location = IntArray(2)
        this.getLocationOnScreen(location)
        return Point(location[0], location[1])
    }

    override fun onFailure(message: String) {
        binding.rlLoading.visibility = View.GONE
        when(message) {
            Constant.API_ERROR -> (activity as MainActivity).showMessage(
                resources.getString(R.string.server_error_title),
                resources.getString(R.string.server_error)
            )
            Constant.NO_INTERNET -> (activity as MainActivity).showMessage(
                resources.getString(R.string.network_error_title),
                resources.getString(R.string.network_error)
            )
        }
    }
}