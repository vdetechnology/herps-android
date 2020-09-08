package herbs.n.more.ui.auth

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModel
import herbs.n.more.data.repositories.UserRepository
import herbs.n.more.util.ApiException
import herbs.n.more.util.Coroutines
import herbs.n.more.util.NoInternetException

class AuthViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    var name: String? = null
    var email: String? = null
    var password: String? = null;
    var passwordconfirm: String? = null;
    var authListener: AuthListener? = null

    fun getLoggedInUser() = userRepository.getUser()

    fun onLogin(view: View){
        Intent(view.context, LoginActivity::class.java).also {
            view.context.startActivity(it)
        }
    }

    fun onLoginButtonClick(view: View){
        authListener?.onStarted()
        if (email.isNullOrEmpty() || password.isNullOrEmpty()){
            authListener?.onFailure("Invalid emmail or password")
            return
        }

        Coroutines.main {
            try {
                var authResponse = userRepository.userLogin(email!!, password!!)
                authResponse.user?.let {
                    authListener?.onSuccess(it)
                    userRepository.saveUser(it)
                    return@main
                }
                authListener?.onFailure(authResponse.message!!)
            }catch (e: ApiException){
                authListener?.onFailure(e.message!!)
            }catch (e: NoInternetException){
                authListener?.onFailure(e.message!!)
            }
        }
    }

    fun onSingup(view: View){
        Intent(view.context, SingupActivity::class.java).also {
            view.context.startActivity(it)
        }
    }

    fun onSignupButtonClick(view: View){
        authListener?.onStarted()
        if (email.isNullOrEmpty()){
            authListener?.onFailure("Name is required")
            return
        }
        if (email.isNullOrEmpty()){
            authListener?.onFailure("Email is required")
            return
        }
        if (password.isNullOrEmpty()){
            authListener?.onFailure("Please enter a password")
            return
        }
        if (password != passwordconfirm){
            authListener?.onFailure("Password confirm did not match")
            return
        }

        Coroutines.main {
            try {
                var authResponse = userRepository.userSignup(name!!, email!!, password!!)
                authResponse.user?.let {
                    authListener?.onSuccess(it)
                    return@main
                }
                authListener?.onFailure(authResponse.message!!)
            }catch (e: ApiException){
                authListener?.onFailure(e.message!!)
            }catch (e: NoInternetException){
                authListener?.onFailure(e.message!!)
            }
        }
    }
}