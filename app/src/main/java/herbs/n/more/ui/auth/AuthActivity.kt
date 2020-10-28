package herbs.n.more.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.zing.zalo.zalosdk.oauth.LoginVia
import com.zing.zalo.zalosdk.oauth.OAuthCompleteListener
import com.zing.zalo.zalosdk.oauth.OauthResponse
import com.zing.zalo.zalosdk.oauth.ZaloSDK
import herbs.n.more.R
import herbs.n.more.data.db.entities.User
import herbs.n.more.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_auth.*
import org.kodein.di.generic.instance
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein


class AuthActivity : BaseActivity(), KodeinAware, AuthListener {

    override val kodein by kodein()
    private lateinit var viewModel: AuthViewModel
    private val factory: AuthViewModelFactory by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        viewModel = ViewModelProviders.of(this, factory).get(AuthViewModel::class.java)
        viewModel.authListener = this
    }

    fun loginZalo() {
        ZaloSDK.Instance.authenticate(this, LoginVia.APP_OR_WEB, listener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ZaloSDK.Instance.onActivityResult(this, requestCode, resultCode, data)
    }

    private val listener = object : OAuthCompleteListener() {
        override fun onGetOAuthComplete(response: OauthResponse?) {
            if (TextUtils.isEmpty(response?.oauthCode)) {
                onLoginError(response?.errorCode ?: -1, response?.errorMessage ?: "Unknown error")
            } else {
                onLoginSuccess()
            }
        }

        override fun onAuthenError(errorCode: Int, message: String?) {
            onLoginError(errorCode, message ?: "Unknown error")
        }
    }

    fun onLoginSuccess() {
        //Get Profile
        ZaloSDK.Instance.getProfile(this, { data: JSONObject ->
            val id = data.optString("id")
            val name = data.optString("name")
            val pic = data.optJSONObject("picture")
            val picData = pic?.optJSONObject("data")
            val url = picData?.optString("url").toString()
            viewModel.onLoginZaloClick(id, name, url)
        }, arrayOf("id", "name", "picture"))
    }

    fun onLoginError(code: Int, message: String) {
        Log.e("Login Zalo error: ", "[$code] $message")
    }

    override fun onStarted() {
        rl_loading.visibility = View.VISIBLE
    }

    override fun onSuccess(user: User?, message: String) {
        rl_loading.visibility = View.GONE
    }

    override fun onFailure(message: String) {
        rl_loading.visibility = View.GONE
        showMessage(
            resources.getString(R.string.login_error),
            message
        )
    }
}