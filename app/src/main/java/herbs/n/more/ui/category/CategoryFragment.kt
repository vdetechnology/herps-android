package herbs.n.more.ui.category

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.annotation.RequiresApi
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import herbs.n.more.R
import herbs.n.more.data.db.entities.Category
import herbs.n.more.data.db.entities.Product
import herbs.n.more.data.db.entities.User
import herbs.n.more.databinding.FragmentCategoryBinding
import herbs.n.more.ui.BaseFragment
import herbs.n.more.ui.MainActivity
import herbs.n.more.ui.detail.DetailActivity
import herbs.n.more.ui.dialog.ConfirmLoginDialog
import herbs.n.more.ui.home.*
import herbs.n.more.util.Constant
import herbs.n.more.util.Coroutines
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*

class CategoryFragment : BaseFragment(), KodeinAware, BestSellingListener, ProductItemListener,
    ProductRecentlyItemListener {

    private lateinit var binding: FragmentCategoryBinding
    override val kodein by kodein()
    private val factory: BestSellingViewModelFactory by instance()
    private lateinit var viewModel: BestSellingViewModel

    private var user : User? = null
    private var mSuggestedAdapter = GroupAdapter<GroupieViewHolder>()
    private var mCategoryAdapter = GroupAdapter<GroupieViewHolder>()
    private var pageindex : Int = 1

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_category, container, false)
        viewModel = ViewModelProviders.of(this, factory).get(BestSellingViewModel::class.java)
        binding.fragment = this
        binding.lifecycleOwner = this
        viewModel.bestSellingListener = this

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
        GlobalScope.async{bindCategory()}
        GlobalScope.async{bindDataPopular()}
    }

    private fun bindCountCart() = Coroutines.main {
        viewModel.countCart.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it > 0){
                binding.tvNumberInCart.visibility = View.VISIBLE
                binding.tvNumberInCart.text = it.toString()
            }else{
                binding.tvNumberInCart.visibility = View.GONE
            }
        })
    }

    private fun bindCategory() = Coroutines.main {
        viewModel.categorys()?.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            mCategoryAdapter = GroupAdapter<GroupieViewHolder>().apply {
                if (it.isNotEmpty()) {
                    addAll(it.toCategoryItem())
                }
            }
            binding.rvCategory.apply {
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                adapter = mCategoryAdapter
            }
        })
    }

    private fun List<Category>.toCategoryItem() : List<CategoryItem>{
        return this.map {
            CategoryItem(this@CategoryFragment, it)
        }
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun bindDataPopular() = Coroutines.main {
        viewModel.getPopular(pageindex)?.let {
            mSuggestedAdapter = GroupAdapter<GroupieViewHolder>().apply {
                if (it.isNotEmpty()) {
                    addAll(it.toProductItem())
                }
            }
            binding.rvProduct.apply {
                layoutManager = GridLayoutManager(activity, 2, LinearLayoutManager.VERTICAL, false)
                adapter = mSuggestedAdapter
            }
            binding.svCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                    pageindex += 1
                    binding.pbLoadMore.visibility = View.VISIBLE
                    loadMorePopular()
                }
                if(scrollY > 5000){
                    binding.fbTop.visibility = View.VISIBLE
                }else{
                    binding.fbTop.visibility = View.GONE
                }
            })
        }
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun loadMorePopular() = Coroutines.main {
        viewModel.getPopular(pageindex)?.let {
            if (it.isNotEmpty()) {
                mSuggestedAdapter.addAll(it.toProductItem())
            }
            binding.pbLoadMore.visibility = View.GONE
        }
    }

    private fun List<Product>.toProductItem() : List<ProductItem>{
        return this.map {
            ProductItem(requireActivity(), it, this@CategoryFragment)
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

    override fun onLikeClicked(product: Product) {
        if (user != null){

        }else {
            val dialog : ConfirmLoginDialog? = activity?.let { ConfirmLoginDialog(it) }
            dialog?.show()
        }
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

    fun goToResult(category: String){
        goToSearchResult("",-1, arrayListOf(category),0f,10000000f)
    }

    fun scrollTop(){
        binding.svCategory.fullScroll(ScrollView.FOCUS_UP);
    }
}