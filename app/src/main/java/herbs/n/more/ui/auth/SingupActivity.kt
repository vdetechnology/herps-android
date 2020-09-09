package herbs.n.more.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class SingupActivity : AppCompatActivity() , AuthListener, KodeinAware {

    override val kodein by kodein()
    private val factory: AuthViewModelFactory by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        var binding: ActivitySignupBindingImpl = DataBindingUtil.setContentView(this, R.layout.activity_signup)
        var viewModel = ViewModelProviders.of(this, factory).get(AuthViewModel::class.java)
        binding.viewmodel = viewModel
        viewModel.authListener = this
    }

    override fun onStarted() {
        progress_bar.show()
    }

    override fun onSuccess(user: User) {
        progress_bar.hide()
        toast("${user.name} is registed. Please login your account")
        onBackPressed()
    }

    override fun onFailure(message: String) {
        progress_bar.hide()
        toast(message)
    }
}