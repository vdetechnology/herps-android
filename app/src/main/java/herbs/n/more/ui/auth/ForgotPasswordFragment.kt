package herbs.n.more.ui.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import herbs.n.more.R
import herbs.n.more.data.db.entities.User
import herbs.n.more.databinding.FragmentForgotPasswordBinding
import herbs.n.more.util.hide
import herbs.n.more.util.show
import herbs.n.more.util.toast
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class ForgotPasswordFragment  : Fragment(), AuthListener, KodeinAware {

    override val kodein by kodein()
    private val factory: AuthViewModelFactory by instance()
    private lateinit var binding: FragmentForgotPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_forgot_password, container, false)
        var viewModel = ViewModelProviders.of(this, factory).get(AuthViewModel::class.java)
        binding.forgot = viewModel
        binding.fragment = this
        viewModel.authListener = this
        return binding.root
    }

    override fun onStarted() {
        binding.progressBar.show()
    }

    override fun onSuccess(user: User, message : String) {
        binding.progressBar.hide()
        activity?.onBackPressed()
    }

    override fun onFailure(message: String) {
        binding.progressBar.hide()
        activity?.toast(message)
    }

    fun onBack(view: View){
        view.findNavController().popBackStack()
    }

    fun onHelp(view: View){
        val url = "http://herbs.vn/"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
}