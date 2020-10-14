package herbs.n.more.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import herbs.n.more.R
import herbs.n.more.databinding.FragmentPaymentResultBinding
import herbs.n.more.ui.BaseFragment

class PaymentResultFragment : BaseFragment() {

    private lateinit var binding: FragmentPaymentResultBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment_result, container, false)
        binding.fragment = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvCodeOrders.text = arguments?.getString("code_order")!!
        binding.tvTotalOrders.text = convertMoney(arguments?.getDouble("total")!!)
        binding.tvPaymentMethods.text = arguments?.getString("methods")!!
    }

    fun onBackHomeClick(view: View){
        onBackActivity(view)
    }
}