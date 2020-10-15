package herbs.n.more.ui.cart

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import herbs.n.more.R
import herbs.n.more.data.db.entities.Cart
import herbs.n.more.databinding.FragmentPaymentBinding
import herbs.n.more.ui.BaseFragment
import herbs.n.more.ui.MainActivity
import herbs.n.more.ui.auth.AuthActivity
import herbs.n.more.util.Constant
import herbs.n.more.util.Coroutines
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class PaymentFragment : BaseFragment(), KodeinAware, CartListener{

    private lateinit var binding: FragmentPaymentBinding
    override val kodein by kodein()
    private val factory: CartViewModelFactory by instance()
    lateinit var viewModel: CartViewModel
    private var totalOrder: Double = 0.0
    private var percent: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment, container, false)
        viewModel = ViewModelProviders.of(this, factory).get(CartViewModel::class.java)
        binding.cart = viewModel
        binding.fragment = this
        binding.lifecycleOwner = this
        viewModel.cartListener = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        percent = arguments?.getInt("percent")!!
        binding.rbCod.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                binding.llMethods.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_item))
        }
        initData()
    }

    private fun initData(){
        GlobalScope.apply{getUser()}
        GlobalScope.async{bindSumOrder()}
        GlobalScope.async{bindDataCart()}
    }

    private fun getUser(){
        viewModel.user.observe(viewLifecycleOwner, androidx.lifecycle.Observer { user ->
            if (user != null) {
                binding.etName.setText(user.displayName)
                binding.etEmail.setText(user.email)
            }
        })
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
                    addAll(it.toPaymentItem())
                }

                binding.rvCart.apply {
                    layoutManager = LinearLayoutManager(
                        activity,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                    adapter = mAdapter
                }
            }
        })
    }

    private fun List<Cart>.toPaymentItem() : List<PaymentItem> {
        return this.map {
            PaymentItem(this@PaymentFragment, it)
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
        if (percent > 0){
            binding.tvTotalDiscount.text = "-" + convertMoney(totalDiscount)
        }else {
            binding.tvTotalDiscount.text = convertMoney(totalDiscount)
        }
        binding.tvTotalTemp.text = convertMoney(totalOrder)
        binding.tvTotal.text = convertMoney(totalOrder - totalDiscount)
        binding.tvTotalEnd.text = convertMoney(totalOrder - totalDiscount)
    }

    fun continueOrder(){
        closeKeyBoard()
        if(viewModel.checkValidate()) {
            if (!binding.rbCod.isChecked) {
                binding.llMethods.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_error))
                val scrollTo: Int = (binding.llMethods.parent as View).top + binding.llMethods.top
                binding.svHome.smoothScrollTo(0, scrollTo)
            } else {
                binding.llMethods.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_item))
                deleteCart()
            }
        }
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun deleteCart() = Coroutines.io {
        val deleteCart = viewModel.deleteAllCart.await()
        deleteCart.let {
            val bundle = bundleOf(
                Pair("total", totalOrder - totalOrder * (percent / 100f)),
                Pair("code_order", "25251325"),
                Pair("methods", resources.getString(R.string.payment_on_delivery))
            )
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_paymentFragment_to_paymentResultFragment, bundle)
        }
    }

    override fun onStarted() {
        binding.rlLoading.visibility = View.VISIBLE
    }

    override fun onSuccess(message: String) {
        binding.rlLoading.visibility = View.GONE
    }

    override fun onSuccessCode(percent: Int) {

    }

    override fun onFailure(message: String) {
        binding.rlLoading.visibility = View.GONE
        when(message) {
            Constant.NAME_NULL -> {binding.tvErrName.text = resources.getString(R.string.name_is_blank)
                binding.svHome.smoothScrollTo(0, 0)}
            Constant.NAME_SHORTER -> {binding.tvErrName.text = resources.getString(R.string.name_shorter)
                binding.svHome.smoothScrollTo(0, 0)}
            Constant.NAME_OK -> binding.tvErrName.text = ""

            Constant.ADDRESS_NULL -> {binding.tvErrAddress.text = resources.getString(R.string.address_is_blank)
                binding.svHome.smoothScrollTo(0, 0)}
            Constant.ADDRESS_OK -> binding.tvErrAddress.text = ""

            Constant.PHONE_NULL -> {binding.tvErrPhone.text = resources.getString(R.string.phone_is_blank)
                binding.svHome.smoothScrollTo(0, 0)}
            Constant.PHONE_SHORTER -> {binding.tvErrPhone.text = resources.getString(R.string.phone_shorter)
                binding.svHome.smoothScrollTo(0, 0)}
            Constant.PHONE_INVALID -> {binding.tvErrPhone.text = resources.getString(R.string.phone_not_contain_space)
                binding.svHome.smoothScrollTo(0, 0)}
            Constant.PHONE_OK -> binding.tvErrPhone.text = ""

            Constant.EMAIL_NULL -> {binding.tvErrEmail.text = resources.getString(R.string.email_is_blank)
                binding.svHome.smoothScrollTo(0, 0)}
            Constant.EMAIL_ISVALID -> {binding.tvErrEmail.text = resources.getString(R.string.email_wrong_format)
                binding.svHome.smoothScrollTo(0, 0)}
            Constant.EMAIL_OK -> binding.tvErrEmail.text = ""

            Constant.API_ERROR -> (activity as MainActivity).showMessage(
                resources.getString(R.string.server_error_title),
                resources.getString(R.string.server_error)
            )
            Constant.NO_INTERNET -> (activity as MainActivity).showMessage(
                resources.getString(R.string.network_error_title),
                resources.getString(R.string.network_error)
            )

            else -> (activity as AuthActivity).showMessage(resources.getString(R.string.payment), message)
        }
    }
}