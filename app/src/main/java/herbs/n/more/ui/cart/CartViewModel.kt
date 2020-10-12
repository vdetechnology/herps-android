package herbs.n.more.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import herbs.n.more.data.db.entities.Cart
import herbs.n.more.data.repositories.CartRepository
import herbs.n.more.util.Coroutines

class CartViewModel (
    private val repository: CartRepository
) : ViewModel() {

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
}