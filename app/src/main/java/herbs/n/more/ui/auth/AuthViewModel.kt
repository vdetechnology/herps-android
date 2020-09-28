package herbs.n.more.ui.auth

import android.view.View
import androidx.lifecycle.ViewModel
import herbs.n.more.data.repositories.UserRepository
import herbs.n.more.util.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class AuthViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    var name: String? = null
    var email: String? = null
    var password: String? = null
    var authListener: AuthListener? = null

    fun getLoggedInUser() = userRepository.getUser()

    fun onLoginButtonClick(view: View){

        if (!checkValidate())
            return
        authListener?.onStarted()
        Coroutines.main {
            try {
                var authResponse = userRepository.userLogin(email!!, password!!)
                authResponse.data?.let {
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

    fun checkValidate() : Boolean{
        var isMail = false
        var isPass = false
            if (Validate.isNull(email)) {
                authListener?.onFailure(Constant.EMAIL_NULL)
            } else if (!Validate.isValidEmail(email)) {
                authListener?.onFailure(Constant.EMAIL_ISVALID)
            } else {
                authListener?.onFailure(Constant.EMAIL_OK)
                isMail = true
            }
            if (Validate.isNull(password)) {
                authListener?.onFailure(Constant.PASSWORD_NULL)
            } else {
                authListener?.onFailure(Constant.PASSWORD_OK)
                isPass = true
            }

        return isMail && isPass
    }

    fun onSignupButtonClick(view: View){
        authListener?.onStarted()
        if (name.isNullOrEmpty()){
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

        Coroutines.main {
            try {
                var authResponse = userRepository.userSignup(name!!, email!!, password!!)
                authResponse.data?.let {
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

    fun checkEmail(email : String){

    }
}