package herbs.n.more.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import herbs.n.more.data.db.entities.Cart
import herbs.n.more.data.repositories.CartRepository
import herbs.n.more.util.*
import java.lang.Exception

class CartViewModel (
    private val repository: CartRepository
) : ViewModel() {

    var code: String? = null
    var cartListener: CartListener? = null

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
}