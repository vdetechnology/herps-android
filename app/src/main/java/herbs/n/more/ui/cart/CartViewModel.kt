package herbs.n.more.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import herbs.n.more.data.db.entities.Cart
import herbs.n.more.data.repositories.CartRepository
import herbs.n.more.util.*
import herbs.n.more.util.transform.lazyDeferred
import java.lang.Exception

class CartViewModel (
    private val repository: CartRepository
) : ViewModel() {

    var code: String? = null
    var cartListener: CartListener? = null
    var name: String? = null
    var address: String? = null
    var phone: String? = null
    var email: String? = null
    var note: String? = null

    val user = repository.getUser()

    fun getAllCart(): LiveData<List<Cart>> {
        return  repository.getAllCart()
    }

    fun updateCart(cart: Cart) {
        Coroutines.io {
            repository.updateCart(cart)
        }
    }

    fun deleteCart(cart: Cart) {
        Coroutines.io {
            repository.deleteCart(cart)
        }
    }

    val deleteAllCart by lazyDeferred {
        repository.deleteAllCart()
    }

    val sumOrder = repository.getSumOrder()

    fun onDiscountClick(){
        if (Validate.isNull(code)) {
            cartListener?.onFailure(Constant.CODE_NULL)
            return
        } else {
            cartListener?.onFailure(Constant.CODE_OK)
        }
        cartListener?.onStarted()
        Coroutines.main {
            try {
                var codeResponse = repository.getDiscountCode(code!!)
                codeResponse.success?.let { it ->
                    if (it){
                        cartListener?.onSuccessCode(20)
                    }else{
                        cartListener?.onFailure(codeResponse.message)
                    }
                    return@main
                }
                cartListener?.onFailure(codeResponse.message)
            } catch (e: ApiException){
                cartListener?.onFailure(Constant.API_ERROR)
            } catch (e: NoInternetException){
                cartListener?.onFailure(Constant.NO_INTERNET)
            } catch (e: Exception) {
                e.printStackTrace()
                cartListener?.onFailure(Constant.API_ERROR)
            }
        }
    }

    fun checkValidate() : Boolean{
        var isName = false
        var isAddress = false
        var isPhone = false
        var isEmail = false

        if (Validate.isNull(name)) {
            cartListener?.onFailure(Constant.NAME_NULL)
        } else if (Validate.isShorterThan(name, 2)) {
            cartListener?.onFailure(Constant.NAME_SHORTER)
        } else {
            cartListener?.onFailure(Constant.NAME_OK)
            isName = true
        }

        if (Validate.isNull(address)) {
            cartListener?.onFailure(Constant.ADDRESS_NULL)
        } else {
            cartListener?.onFailure(Constant.ADDRESS_OK)
            isAddress = true
        }

        if (Validate.isNull(phone)) {
            cartListener?.onFailure(Constant.PHONE_NULL)
        } else if (Validate.isShorterThan(phone, 10)) {
            cartListener?.onFailure(Constant.PHONE_SHORTER)
        }else if (phone?.contains(" ")!!) {
            cartListener?.onFailure(Constant.PHONE_INVALID)
        }else {
            cartListener?.onFailure(Constant.PHONE_OK)
            isPhone = true
        }

        if (Validate.isNull(email)) {
            cartListener?.onFailure(Constant.EMAIL_NULL)
        } else if (!Validate.isValidEmail(email)) {
            cartListener?.onFailure(Constant.EMAIL_ISVALID)
        } else {
            cartListener?.onFailure(Constant.EMAIL_OK)
            isEmail= true
        }

        return isName && isAddress && isPhone && isEmail
    }
}