package herbs.n.more.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import herbs.n.more.R
import herbs.n.more.data.db.entities.User
import herbs.n.more.databinding.ActivitySignupBindingImpl
import herbs.n.more.util.hide
import herbs.n.more.util.show
import herbs.n.more.util.toast
import kotlinx.android.synthetic.main.activity_login.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class RegisterFragment : Fragment(), AuthListener, KodeinAware {

    override val kodein by kodein()
    private val factory: AuthViewModelFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var binding: ActivitySignupBindingImpl = DataBindingUtil.inflate(inflater, R.layout.activity_signup, container, false)
        var viewModel = ViewModelProviders.of(this, factory).get(AuthViewModel::class.java)
        binding.viewmodel = viewModel
        viewModel.authListener = this
        return binding.root
    }

    override fun onStarted() {
        progress_bar.show()
    }

    override fun onSuccess(user: User) {
        progress_bar.hide()
        activity?.toast("${user.name} is registed. Please login your account")
        activity?.onBackPressed()
    }

    override fun onFailure(message: String) {
        progress_bar.hide()
        activity?.toast(message)
    }
}