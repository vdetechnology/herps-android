package herbs.n.more.ui.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import herbs.n.more.R
import herbs.n.more.data.db.entities.User
import herbs.n.more.databinding.FragmentForgotPasswordBinding
import herbs.n.more.ui.BaseFragment
import herbs.n.more.util.Constant
import herbs.n.more.util.hide
import herbs.n.more.util.show
import kotlinx.android.synthetic.main.fragment_login.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class ForgotPasswordFragment  : BaseFragment(), AuthListener, KodeinAware {

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
        binding.rlLoading.visibility = View.VISIBLE
    }

    override fun onSuccess(user: User?, message : String) {
        binding.rlLoading.visibility = View.GONE
        openDialog(message)
    }

    override fun onFailure(message: String) {
        binding.rlLoading.visibility = View.GONE
        when(message) {
            Constant.EMAIL_NULL -> tv_err_mail.text = resources.getString(R.string.email_is_blank)
            Constant.EMAIL_OK -> tv_err_mail.text = ""
            Constant.EMAIL_ISVALID -> tv_err_mail.text = resources.getString(R.string.email_wrong_format)
            else -> (activity as AuthActivity).showMessage(resources.getString(R.string.forgot_error), message)
        }
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

    private fun openDialog(email: String) {
        val view: View = layoutInflater.inflate(R.layout.bottom_sheet_forgot, null)
        val dialog = BottomSheetDialog(this.requireContext(), R.style.BottomSheetDialogTheme)
        dialog.setContentView(view)
        val tvMessage = view.findViewById<View>(R.id.tv_message) as TextView
        tvMessage.text = Html.fromHtml(String.format(resources?.getString(R.string.forgot_text1), email))
        val btBack = view.findViewById<View>(R.id.bt_back_login)
        btBack.setOnClickListener {
            binding.root.findNavController().popBackStack()
            dialog.dismiss()
        }
        dialog.show()
    }
}