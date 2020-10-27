package herbs.n.more.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import herbs.n.more.R
import herbs.n.more.data.db.entities.User
import herbs.n.more.databinding.FragmentLoginBinding
import herbs.n.more.ui.BaseFragment
import herbs.n.more.ui.MainActivity
import herbs.n.more.util.Constant
import herbs.n.more.util.hide
import herbs.n.more.util.show
import herbs.n.more.util.toast
import kotlinx.android.synthetic.main.fragment_login.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class LoginFragment : BaseFragment(), AuthListener, KodeinAware {

    override val kodein by kodein()
    private val factory: AuthViewModelFactory by instance()
    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: AuthViewModel
    private var isShow: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        viewModel = ViewModelProviders.of(this, factory).get(AuthViewModel::class.java)
        binding.viewmodel = viewModel
        binding.fragment = this
        viewModel.authListener = this

        viewModel.getLoggedInUser().observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { user ->
                if (user != null) {
                    activity?.let { it ->
                        Intent(it, MainActivity::class.java).also {
                            it.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(it)
                            activity?.overridePendingTransition(
                                R.anim.anim_slide_in_right,
                                R.anim.anim_slide_out_left
                            )
                        }
                    }
                }
            })

        return binding.root
    }

    override fun onStarted() {
        binding.progressBar.show()
    }

    override fun onSuccess(user: User?, message: String) {
        binding.progressBar.hide()
    }

    override fun onFailure(message: String) {
        binding.progressBar.hide()
        when (message) {
            Constant.EMAIL_NULL -> tv_err_mail.text = resources.getString(R.string.email_is_blank)
            Constant.EMAIL_OK -> tv_err_mail.text = ""
            Constant.EMAIL_ISVALID -> tv_err_mail.text =
                resources.getString(R.string.email_wrong_format)
            Constant.PASSWORD_NULL -> tv_err_pass.text =
                resources.getString(R.string.password_is_blank)
            Constant.PASSWORD_INVALID -> tv_err_pass.text =
                resources.getString(R.string.password_not_contain_space)
            Constant.PASSWORD_OK -> tv_err_pass.text = ""
            else -> (activity as AuthActivity).showMessage(
                resources.getString(R.string.login_error),
                message
            )
        }
    }

    fun onSingup(view: View) {
        view.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.image_click))
        view.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

    fun onForgotPassword(view: View) {
        view.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.image_click))
        view.findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
    }

    fun commingSoon() {
        activity?.toast(resources.getString(R.string.comming_soon))
    }

    fun loginZalo() {
        (activity as AuthActivity).loginZalo()
    }

    fun onBack() {
        activity?.finish()
        activity?.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
    }

    fun onShowPassword(view: View) {
        val view = view as ImageButton
        if (!isShow) {
            binding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            view.setImageResource(R.drawable.ic_show_pass_off)
            isShow = true
            binding.etPassword.setSelection(binding.etPassword.text.length)
        } else {
            binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            view.setImageResource(R.drawable.ic_show_pass)
            isShow = false
            binding.etPassword.setSelection(binding.etPassword.text.length)
        }
    }
}