package herbs.n.more.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import herbs.n.more.R
import herbs.n.more.data.db.entities.Product
import herbs.n.more.data.db.entities.User
import herbs.n.more.databinding.ActivitySeeMoreBinding
import herbs.n.more.ui.BaseActivity
import herbs.n.more.ui.detail.DetailActivity
import herbs.n.more.ui.dialog.ConfirmLoginDialog
import herbs.n.more.util.Constant
import herbs.n.more.util.Coroutines
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*

class SeeMoreActivity : BaseActivity() , KodeinAware, BestSellingListener, ProductItemListener, ProductRecentlyItemListener {

    override val kodein by kodein()
    private val factory: BestSellingViewModelFactory by instance()
    private lateinit var viewModel: BestSellingViewModel
    private var user : User? = null

    private lateinit var bind: ActivitySeeMoreBinding
    private var type: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = DataBindingUtil.setContentView(this, R.layout.activity_see_more)
        viewModel = ViewModelProviders.of(this, factory).get(BestSellingViewModel::class.java)
        bind.activity = this
        bind.lifecycleOwner = this
        viewModel.bestSellingListener = this
        viewModel.user.observe(this, androidx.lifecycle.Observer { user ->
            this.user = user
        })

        type = intent.getStringExtra("type_see_more")
        initView()
        initData()
    }

    private fun initView() {
        when(type){
            Constant.SEE_MORE_BEST_SELLING -> bind?.tvTitle?.text = resources.getString(R.string.best_selling)
            Constant.SEE_MORE_RECENT-> bind?.tvTitle?.text = resources.getString(R.string.recently)
        }
    }

    private fun initData() {
        when(type){
            Constant.SEE_MORE_BEST_SELLING -> bindDataBestSelling()
            Constant.SEE_MORE_RECENT-> bindDataRecently()
        }
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun bindDataBestSelling() = Coroutines.main {
        bind.rlLoading.visibility = View.VISIBLE
        val bestSelling = viewModel.bestSellingFull.await()
        bestSelling?.observe(this, androidx.lifecycle.Observer {
            bind.rlLoading.visibility = View.GONE
            val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
                addAll(it.toProductItem())
            }
            bind.rvMore.apply {
                layoutManager = GridLayoutManager(this@SeeMoreActivity, 2, LinearLayoutManager.VERTICAL, false)
                adapter = mAdapter
            }
        })
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun bindDataRecently() = Coroutines.main {
        val recentlys = viewModel.recentlys()
        recentlys?.observe(this, androidx.lifecycle.Observer {
            if (it.isNotEmpty()) {
                val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
                    addAll(it.toProductItem())
                }

                bind.rvMore.apply {
                    layoutManager = GridLayoutManager(this@SeeMoreActivity, 2, LinearLayoutManager.VERTICAL, false)
                    adapter = mAdapter
                }
            }
        })
    }

    private fun List<Product>.toProductItem() : List<ProductItem>{
        return this.map {
            ProductItem(this@SeeMoreActivity, it, this@SeeMoreActivity)
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
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
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