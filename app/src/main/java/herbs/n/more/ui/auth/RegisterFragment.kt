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
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import herbs.n.more.R
import herbs.n.more.data.db.entities.User
import herbs.n.more.databinding.FragmentRegisterBinding
import herbs.n.more.ui.MainActivity
import herbs.n.more.util.*
import kotlinx.android.synthetic.main.fragment_register.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class RegisterFragment : Fragment(), AuthListener, KodeinAware{

    override val kodein by kodein()
    private val factory: AuthViewModelFactory by instance()
    private lateinit var binding: FragmentRegisterBinding
    private var isShow : Boolean = false
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        viewModel = ViewModelProviders.of(this, factory).get(AuthViewModel::class.java)
        binding.register = viewModel
        binding.fragment = this
        binding.lifecycleOwner = this
        viewModel.authListener = this
        return binding.root
    }

    override fun onStarted() {
        binding.progressBar.show()
    }

    override fun onSuccess(user: User, message: String) {
        binding.progressBar.hide()
        openDialog(user)
    }

    override fun onFailure(message: String) {
        binding.progressBar.hide()
        when(message) {
            Constant.EMAIL_NULL -> tv_err_mail.text = resources.getString(R.string.email_is_blank)
            Constant.EMAIL_OK -> tv_err_mail.text = ""
            Constant.EMAIL_ISVALID -> tv_err_mail.text = resources.getString(R.string.email_wrong_format)
            Constant.PASSWORD_NULL -> tv_err_pass.text = resources.getString(R.string.password_is_blank)
            Constant.PASSWORD_SHORTER -> tv_err_pass.text = resources.getString(R.string.password_shorter)
            Constant.PASSWORD_OK -> tv_err_pass.text = ""
            Constant.NAME_NULL -> tv_err_name.text = resources.getString(R.string.name_is_blank)
            Constant.NAME_SHORTER -> tv_err_name.text = resources.getString(R.string.name_shorter)
            Constant.NAME_OK -> tv_err_name.text = ""
            else -> (activity as AuthActivity).showMessage(resources.getString(R.string.register_error), message)
        }
    }

    fun onLogin(view: View){
        view.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.image_click))
        view.findNavController().popBackStack()
    }

    fun commingSoon(view: View){
        activity?.toast(resources.getString(R.string.comming_soon))
    }

    fun onBack(view: View){
        view.findNavController().popBackStack()
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

    private fun openDialog(user: User) {
        val view: View = layoutInflater.inflate(R.layout.bottom_sheet_register, null)
        val dialog = BottomSheetDialog(this.requireContext(), R.style.BottomSheetDialogTheme)
        dialog.setContentView(view)
        val btContinute = view.findViewById<View>(R.id.bt_continute) as TextView
        val btBack = view.findViewById<View>(R.id.bt_back_login) as TextView
        btContinute.setOnClickListener {
            Coroutines.io {
                viewModel.saveUser()
            }
            activity?.let {it ->
                Intent(it, MainActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                }
            }
            dialog.dismiss()
        }
        btBack.setOnClickListener {
            binding.root.findNavController().popBackStack()
            dialog.dismiss()
        }
        dialog.show()
    }
}