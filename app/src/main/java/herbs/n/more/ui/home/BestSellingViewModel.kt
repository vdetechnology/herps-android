package herbs.n.more.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import herbs.n.more.data.db.entities.Product
import herbs.n.more.data.repositories.BestSellingRepository
import herbs.n.more.util.Coroutines
import herbs.n.more.util.transform.lazyDeferred

class BestSellingViewModel(
    private val repository: BestSellingRepository
) : ViewModel() {

    var bestSellingListener: BestSellingListener? = null

    val bestSelling by lazyDeferred {
        bestSellingListener?.let { repository.getBestSelling(it) }
    }

    val bestSellingFull by lazyDeferred {
        bestSellingListener?.let { repository.getBestSellingFull(it) }
    }

    val user = repository.getUser()

    val banners by lazyDeferred {
        repository.getBanners()
    }

    val campaigns by lazyDeferred {
        repository.getCampaigns()
    }

    val deleteUser by lazyDeferred {
        repository.deleteUser()
    }

    suspend fun getPopular(pageindex : Int): List<Product> {
        return  repository.getPopular(pageindex)
    }

    val recentlys by lazyDeferred {
        repository.getAllProducts()
    }

    fun saveRecentlys(product: Product) {
        Coroutines.io {
            repository.saveProducts(product)
        }
    }

    val countCart = repository.getCountCart()
}
