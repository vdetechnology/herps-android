package herbs.n.more.ui.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import herbs.n.more.R
import herbs.n.more.data.db.entities.Product
import herbs.n.more.data.db.entities.User
import herbs.n.more.databinding.ActivityResultBinding
import herbs.n.more.ui.BaseActivity
import herbs.n.more.ui.detail.DetailActivity
import herbs.n.more.ui.dialog.ConfirmLoginDialog
import herbs.n.more.ui.home.BestSellingListener
import herbs.n.more.ui.home.ProductItem
import herbs.n.more.ui.home.ProductItemListener
import herbs.n.more.util.Constant
import herbs.n.more.util.Coroutines
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*
import kotlin.collections.ArrayList

class ResultActivity : BaseActivity(), KodeinAware, BestSellingListener, ProductItemListener{

    override val kodein by kodein()
    private val factory: SearchViewModelFactory by instance()
    private lateinit var viewModel: SearchViewModel
    private lateinit var bind: ActivityResultBinding
    private var q: String = ""
    private var sort: Int = -1
    private var listCategory = arrayListOf<String>()
    private var fromValue: Float = 0f
    private var toValue: Float = 10000000f
    private var user : User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = DataBindingUtil.setContentView(this, R.layout.activity_result)
        viewModel = ViewModelProviders.of(this, factory).get(SearchViewModel::class.java)
        viewModel.bestSellingListener = this
        bind.activity = this
        bind.lifecycleOwner = this
        initData()
        initView()
    }

    private fun initView() {
        bind.etSearch.setText(q)
        if (listCategory.isNotEmpty()){
            var lable: String = ""
            if (listCategory?.size!! > 1) {
                for (title in listCategory!!) {
                    when (title) {
                        "1" -> lable += (resources.getString(R.string.functional_foods) + ", ")
                        "2" -> lable += (resources.getString(R.string.american_ginseng) + ", ")
                        "3" -> lable += resources.getString(R.string.gift_set)
                    }
                }
            }else{
                when (listCategory!![0].toString()) {
                    "1" -> lable += resources.getString(R.string.functional_foods)
                    "2" -> lable += resources.getString(R.string.american_ginseng)
                    "3" -> lable += resources.getString(R.string.gift_set)
                }
            }
            bind.tvTitle.text = lable
        }
    }

    private fun initData(){
        q = intent.getStringExtra("q").toString()
        sort = intent.getIntExtra("sort", -1)
        if(intent.getStringArrayListExtra("category") != null) {
            listCategory = intent.getStringArrayListExtra("category") as ArrayList<String>
        }
        fromValue = intent.getFloatExtra("price_from", 0f)
        toValue = intent.getFloatExtra("price_to", 10000000f)
        GlobalScope.async{bindCountCart()}
        bindSearchResult()
        viewModel.user.observe(this@ResultActivity, androidx.lifecycle.Observer {
            user = it
        })

    }

    fun goToSearchData(){
        goToSearchWithData(q, sort, listCategory, fromValue, toValue)
    }

    fun goToFilterData(){
        goToFilter(q, sort, listCategory, fromValue, toValue)
    }

    private fun bindCountCart() = Coroutines.main {
        viewModel.countCart.observe(this, Observer {
            if (it > 0){
                bind.tvNumberInCart.visibility = View.VISIBLE
                bind.tvNumberInCart.text = it.toString()
            }else{
                bind.tvNumberInCart.visibility = View.GONE
            }
        })
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun bindSearchResult() = Coroutines.main {
        bind.rlLoading.visibility = View.VISIBLE
         viewModel.getSearchResult().observe(this, androidx.lifecycle.Observer {
            bind.rlLoading.visibility = View.GONE
            val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
                addAll(it.toProductItem())
            }

            bind.rvProduct.apply {
                layoutManager = GridLayoutManager(this@ResultActivity, 2, LinearLayoutManager.VERTICAL, false)
                adapter = mAdapter
            }
        })
    }

    private fun List<Product>.toProductItem() : List<ProductItem>{
        return this.map {
            ProductItem(this@ResultActivity, it, this@ResultActivity)
        }
    }

    override fun onItemClicked(product: Product) {
        val calendar = Calendar.getInstance()
        val startTime = calendar.timeInMillis
        product.update_date = startTime
        viewModel.saveRecentlys(product)
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("id", product.id.toString())
            putExtra("user", user)
        }
        startActivity(intent)
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left)
    }

    override fun onLikeClicked(product: Product) {
        if (user != null){

        }else {
            val dialog : ConfirmLoginDialog? = ConfirmLoginDialog(this)
            dialog?.show()
        }
    }

    override fun onFailure(message: String) {
        bind.rlLoading.visibility = View.GONE
        when(message) {
            Constant.API_ERROR -> showMessage(
                resources.getString(R.string.server_error_title),
                resources.getString(R.string.server_error)
            )
            Constant.NO_INTERNET -> showMessage(
                resources.getString(R.string.network_error_title),
                resources.getString(R.string.network_error)
            )
        }
    }
}