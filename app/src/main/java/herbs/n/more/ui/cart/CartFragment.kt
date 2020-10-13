package herbs.n.more.ui.cart

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import herbs.n.more.R
import herbs.n.more.data.db.entities.Cart
import herbs.n.more.databinding.FragmentCartBinding
import herbs.n.more.ui.BaseFragment
import herbs.n.more.ui.auth.AuthActivity
import herbs.n.more.ui.dialog.ConfirmDeleteDialog
import herbs.n.more.util.Constant
import herbs.n.more.util.Coroutines
import herbs.n.more.util.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class CartFragment : BaseFragment(), KodeinAware, CartListener, ConfirmDeleteDialog.OnDialogClick{

    private lateinit var binding: FragmentCartBinding
    override val kodein by kodein()
    private val factory: CartViewModelFactory by instance()
    lateinit var viewModel: CartViewModel
    private var totalOrder: Double = 0.0
    private var percent: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart, container, false)
        viewModel = ViewModelProviders.of(this, factory).get(CartViewModel::class.java)
        binding.cart = viewModel
        binding.fragment = this
        binding.lifecycleOwner = this
        viewModel.cartListener = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()
    }

    private fun initData(){
        GlobalScope.async{bindSumOrder()}
        GlobalScope.async{bindDataCart()}
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun bindDataCart() = Coroutines.main {
        binding.rlLoading.visibility = View.VISIBLE
        viewModel.getAllCart().observe(this, androidx.lifecycle.Observer {
            binding.rlLoading.visibility = View.GONE
            if (it.isNullOrEmpty()) {
                binding.rvCart.visibility = View.GONE
                binding.llEmpty.visibility = View.VISIBLE
            } else {
                val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
                    addAll(it.toCartItem())
                }

                binding.rvCart.apply {
                    layoutManager = LinearLayoutManager(
                        activity,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                    adapter = mAdapter
                }
                binding.rvCart.addItemDecoration(DividerItemDecoration(activity))
                binding.rvCart.addItemDecoration(
                    DividerItemDecoration(activity, R.drawable.divider)
                )
            }
        })
    }

    private fun List<Cart>.toCartItem() : List<CartItem> {
        return this.map {
            CartItem(this@CartFragment, it)
        }
    }

    private fun bindSumOrder() = Coroutines.main {
        viewModel.sumOrder.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (it > 0) {
                    totalOrder = it
                    caculateDiscount()
                }
            }
        })
    }

    private fun caculateDiscount(){
        val totalDiscount = totalOrder * (percent / 100f)
        totalOrder -= totalDiscount
        binding.tvTotalDiscount.text = convertMoney(totalDiscount)
        binding.tvTotalTemp.text = convertMoney(totalOrder)
        binding.tvTotal.text = convertMoney(totalOrder)
        binding.tvTotalEnd.text = convertMoney(totalOrder)
    }

    fun showConfirmDelete(cart: Cart){
        val dialog : ConfirmDeleteDialog? = ConfirmDeleteDialog(this)
        dialog?.show(cart)
    }

    override fun onOKClicked(cart: Cart) {
        viewModel.deleteCart(cart)
    }

    fun continueOrder(){
        closeKeyBoard()
    }

    override fun onStarted() {
        binding.rlLoading.visibility = View.VISIBLE
    }

    override fun onSuccess(message: String) {
        binding.rlLoading.visibility = View.GONE
    }

    override fun onSuccessCode(percent: Int) {
        this.percent = percent
        binding.rlLoading.visibility = View.GONE
        binding.tvErrCode.visibility = View.VISIBLE
        binding.tvErrCode.text = String.format(resources.getString(R.string.code_success),percent.toString()) + resources.getString(R.string.percent)
        binding.tvErrCode.setTextColor(resources.getColor(R.color.colorPrimary))
        caculateDiscount()
    }

    override fun onFailure(message: String) {
        binding.rlLoading.visibility = View.GONE
        binding.tvErrCode.visibility = View.VISIBLE
        when(message) {
            Constant.CODE_NULL -> tv_err_code.text = resources.getString(R.string.code_is_blank)
            Constant.CODE_OK -> tv_err_code.text = ""
            else -> (activity as CartActivity).showMessage(
                resources.getString(R.string.discount_code),
                message
            )
        }
    }
}