package herbs.n.more.ui.saved

import androidx.lifecycle.ViewModel
import herbs.n.more.data.db.entities.Cart
import herbs.n.more.data.db.entities.Product
import herbs.n.more.data.repositories.SavedRepository
import herbs.n.more.ui.detail.DetailListener
import herbs.n.more.util.Coroutines
import java.lang.Exception

class SavedViewModel (
    private val repository: SavedRepository
) : ViewModel() {
    var detailListener : DetailListener? = null

    suspend fun getPopular(pageindex : Int): List<Product>? {
        try {
            return repository.getPopular(pageindex)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun saveRecentlys(product: Product) {
        Coroutines.io {
            repository.saveProducts(product)
        }
    }

    fun saveCart(cart: Cart) {
        Coroutines.io {
            repository.saveCart(cart)
        }
    }

    val countCart = repository.getCountCart()

    suspend fun cartByID(id: Int): Cart{
        return repository.getCartByID(id)
    }
}