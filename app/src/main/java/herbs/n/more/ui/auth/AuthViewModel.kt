package herbs.n.more.ui.auth

import android.view.View
import androidx.lifecycle.ViewModel
import herbs.n.more.R
import herbs.n.more.data.db.entities.User
import herbs.n.more.data.repositories.UserRepository
import herbs.n.more.util.*

class AuthViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    var name: String? = null
    var email: String? = null
    var password: String? = null
    var authListener: AuthListener? = null
    var user: User? = null

    fun getLoggedInUser() = userRepository.getUser()

    fun saveUser(){
        user?.let { userRepository.saveUserIO(it) }
    }

    fun onLoginButtonClick(view: View){

        if (!checkValidate())
            return
        authListener?.onStarted()
        Coroutines.main {
            try {
                var authResponse = userRepository.userLogin(email!!, password!!)
                authResponse.data?.let {
                    authListener?.onSuccess(it, authResponse.message)
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

    fun checkValidateRegister() : Boolean{
        var isName = false
        var isMail = false
        var isPass = false

        if (Validate.isNull(name)) {
            authListener?.onFailure(Constant.NAME_NULL)
        } else if (Validate.isShorterThan(name, 2)) {
            authListener?.onFailure(Constant.NAME_SHORTER)
        } else {
            authListener?.onFailure(Constant.NAME_OK)
            isName = true
        }

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
        } else if (Validate.isShorterThan(password, 6)) {
            authListener?.onFailure(Constant.PASSWORD_SHORTER)
        }else {
            authListener?.onFailure(Constant.PASSWORD_OK)
            isPass = true
        }

        return isName && isMail && isPass
    }

    fun onSignupButtonClick(view: View){
        if (!checkValidateRegister())
            return
        authListener?.onStarted()

        Coroutines.main {
            try {
                var authResponse = userRepository.userSignup(name!!, email!!, password!!)
                authResponse.data?.let {
                    authListener?.onSuccess(it, authResponse.message)
                    user = it
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

    fun onForgotButtonClick(view: View){

        if (Validate.isNull(email)) {
            authListener?.onFailure(Constant.EMAIL_NULL)
            return
        } else if (!Validate.isValidEmail(email)) {
            authListener?.onFailure(Constant.EMAIL_ISVALID)
            return
        } else {
            authListener?.onFailure(Constant.EMAIL_OK)
        }
        authListener?.onStarted()
        Coroutines.main {
            try {
                email?.let { authListener?.onFailure(it) }
            }catch (e: ApiException){
                authListener?.onFailure(e.message!!)
            }catch (e: NoInternetException){
                authListener?.onFailure(e.message!!)
            }
        }
    }

}