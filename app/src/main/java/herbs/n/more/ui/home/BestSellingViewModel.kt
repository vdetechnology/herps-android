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

    val bestSelling by lazyDeferred {
        repository.getBestSelling()
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

    suspend fun getPopular(pageindex : Int): LiveData<List<Product>> {
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
}
