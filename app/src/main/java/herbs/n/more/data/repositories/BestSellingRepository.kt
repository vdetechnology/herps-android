package herbs.n.more.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import herbs.n.more.data.db.AppDatabase
import herbs.n.more.data.db.entities.Product
import herbs.n.more.data.db.entities.SlideImage
import herbs.n.more.data.network.MyApi
import herbs.n.more.data.network.SafeApiRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class BestSellingRepository(
    private val api: MyApi,
    private val db: AppDatabase
) : SafeApiRequest(){

    private val bestSelling = MutableLiveData<List<Product>>()
    private val popular = MutableLiveData<List<Product>>()
    private val banners = MutableLiveData<List<SlideImage>>()
    private val campaigns = MutableLiveData<List<SlideImage>>()

    fun getUser() = db.getUserDao().getuser()

    fun deleteUser() = db.getUserDao().deleteUser()

    fun getProducts() = db.getProductDao().getProducts()

    fun saveProducts(product: Product) = db.getProductDao().saveProduct(product)

    suspend fun getBestSelling(): LiveData<List<Product>> {
        return withContext(Dispatchers.IO) {
            fetchProducts()
            bestSelling
        }
    }

    private suspend fun fetchProducts() {
        try {
            val response = apiRequest { api.getBestSelling() }
            bestSelling.postValue(response.data)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getPopular(pageindex : Int): LiveData<List<Product>> {
        return withContext(Dispatchers.IO) {
            fetchPopular(pageindex)
            popular
        }
    }

    private suspend fun fetchPopular(pageindex : Int) {
        try {
            val response = apiRequest { api.getPopular(pageindex,8) }
            popular.postValue(response.data)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getBanners(): LiveData<List<SlideImage>> {
        return withContext(Dispatchers.IO) {
            fetchBanners()
            banners
        }
    }

    private suspend fun fetchBanners() {
        try {
            val response = apiRequest { api.getBanners() }
            banners.postValue(response.data)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getCampaigns(): LiveData<List<SlideImage>> {
        return withContext(Dispatchers.IO) {
            fetchCampaigns()
            campaigns
        }
    }

    private suspend fun fetchCampaigns() {
        try {
            val response = apiRequest { api.getCampaigns() }
            campaigns.postValue(response.data)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}