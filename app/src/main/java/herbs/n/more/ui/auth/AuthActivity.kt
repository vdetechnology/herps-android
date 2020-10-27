package herbs.n.more.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.ViewModelProviders
import com.zing.zalo.zalosdk.oauth.LoginVia
import com.zing.zalo.zalosdk.oauth.OAuthCompleteListener
import com.zing.zalo.zalosdk.oauth.OauthResponse
import com.zing.zalo.zalosdk.oauth.ZaloSDK
import herbs.n.more.R
import herbs.n.more.ui.BaseActivity
import org.kodein.di.generic.instance
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein


class AuthActivity : BaseActivity(), KodeinAware {

    override val kodein by kodein()
    private lateinit var viewModel: AuthViewModel
    private val factory: AuthViewModelFactory by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        viewModel = ViewModelProviders.of(this, factory).get(AuthViewModel::class.java)
        if(ZaloSDK.Instance.isAuthenticate(null)) {
            onLoginSuccess()
        }
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
}