package herbs.n.more.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.ImageButton
import android.widget.TextView.OnEditorActionListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import herbs.n.more.R
import herbs.n.more.data.db.entities.User
import herbs.n.more.databinding.FragmentLoginBinding
import herbs.n.more.ui.MainActivity
import herbs.n.more.util.*
import kotlinx.android.synthetic.main.fragment_login.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class LoginFragment : Fragment(), AuthListener, KodeinAware {

    override val kodein by kodein()
    private val factory: AuthViewModelFactory by instance()
    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel : AuthViewModel
    private var isShow : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        viewModel = ViewModelProviders.of(this, factory).get(AuthViewModel::class.java)
        binding.viewmodel = viewModel
        binding.fragment = this
        viewModel.authListener = this

        viewModel.getLoggedInUser().observe(viewLifecycleOwner, androidx.lifecycle.Observer { user ->
            if (user != null){
                activity?.let {
                    Intent(it, MainActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                    }
                }
            }
        })

        return binding.root
    }

    override fun onStarted() {
        binding.progressBar.show()
    }

    override fun onSuccess(user: User) {
        binding.progressBar.hide()
        binding.rootLayout.snackbar("${user.displayName} is Logged In")
    }

    override fun onFailure(message: String) {
        binding.progressBar.hide()
        when(message) {
            Constant.EMAIL_NULL -> tv_err_mail.setText(resources.getString(R.string.email_is_blank))
            Constant.EMAIL_OK -> tv_err_mail.setText("")
            Constant.EMAIL_ISVALID -> tv_err_mail.setText(resources.getString(R.string.email_wrong_format))
            Constant.PASSWORD_NULL -> tv_err_pass.setText(resources.getString(R.string.password_is_blank))
            Constant.PASSWORD_OK -> tv_err_pass.setText("")
            else -> activity?.toast(message)
        }
    }

    fun onSingup(view: View){
        view.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.image_click))
        view.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

    fun onForgotPassword(view: View){
        view.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.image_click))
        view.findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
    }

    fun commingSoon(view: View){
        activity?.toast(resources.getString(R.string.comming_soon))
    }

    fun onBack(view: View){
        activity?.finish()
    }

    fun onShowPassword(view: View){
        val view = view as ImageButton
        if(!isShow){
            binding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            view.setImageResource(R.drawable.ic_show_pass_off)
            isShow = true
        } else{
            binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            view.setImageResource(R.drawable.ic_show_pass)
            isShow = false
        }
    }
}