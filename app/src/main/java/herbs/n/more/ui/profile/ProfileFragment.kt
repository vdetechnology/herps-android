package herbs.n.more.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.zing.zalo.zalosdk.oauth.ZaloSDK
import herbs.n.more.R
import herbs.n.more.data.db.entities.User
import herbs.n.more.databinding.FragmentProfileBinding
import herbs.n.more.ui.MainActivity
import herbs.n.more.ui.home.BestSellingViewModel
import herbs.n.more.ui.home.BestSellingViewModelFactory
import herbs.n.more.util.Coroutines
import herbs.n.more.util.toast
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class ProfileFragment : Fragment(), KodeinAware {

    private lateinit var binding: FragmentProfileBinding
    override val kodein by kodein()
    private val factory: BestSellingViewModelFactory by instance()
    private lateinit var viewModel: BestSellingViewModel
    protected var user : User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        viewModel = ViewModelProviders.of(this, factory).get(BestSellingViewModel::class.java)
        binding.bestselling = viewModel
        binding.fragment = this
        binding.lifecycleOwner = this
        viewModel.user.observe(viewLifecycleOwner, androidx.lifecycle.Observer { user ->
            if (user !=null)
                this.user = user
        })

        return binding.root
    }

    fun logOut(view: View){
        Coroutines.main {
            val delete = viewModel.deleteUser.await()
            delete.let {
                ZaloSDK.Instance.unauthenticate()
                activity?.toast("Đăng xuất thành công")
                (activity as MainActivity).goHome()
            }
        }
    }
}