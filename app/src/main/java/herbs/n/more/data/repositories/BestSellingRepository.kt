package herbs.n.more.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import herbs.n.more.data.db.AppDatabase
import herbs.n.more.data.db.entities.Product
import herbs.n.more.data.db.entities.SlideImage
import herbs.n.more.data.network.MyApi
import herbs.n.more.data.network.SafeApiRequest
import herbs.n.more.ui.home.BestSellingListener
import herbs.n.more.util.ApiException
import herbs.n.more.util.Constant
import herbs.n.more.util.NoInternetException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class BestSellingRepository(
    private val api: MyApi,
    private val db: AppDatabase
) : SafeApiRequest(){

    private val bestSelling = MutableLiveData<List<Product>>()
    private val bestSellingAll = MutableLiveData<List<Product>>()
    private val banners = MutableLiveData<List<SlideImage>>()
    private val campaigns = MutableLiveData<List<SlideImage>>()

    fun getUser() = db.getUserDao().getuser()

    fun deleteUser() = db.getUserDao().deleteUser()

    fun getProducts(int: Int) = db.getProductDao().getProducts(int)

    fun getAllProducts() = db.getProductDao().getAllProducts()

    fun saveProducts(product: Product) = db.getProductDao().saveProduct(product)

    suspend fun getBestSelling(bestSellingListener: BestSellingListener): LiveData<List<Product>> {
        return withContext(Dispatchers.Main) {
            fetchProducts(bestSellingListener)
            bestSelling
        }
    }

    private suspend fun fetchProducts(bestSellingListener: BestSellingListener) {
        try {
            val response = apiRequest { api.getBestSelling() }
            bestSelling.postValue(response.data)
        } catch (e: ApiException){
            bestSellingListener?.onFailure(Constant.API_ERROR)
        } catch (e: NoInternetException){
            bestSellingListener?.onFailure(Constant.NO_INTERNET)
        } catch (e: Exception) {
            e.printStackTrace()
            bestSellingListener?.onFailure(Constant.API_ERROR)
        }
    }

    suspend fun getBestSellingFull(bestSellingListener: BestSellingListener): LiveData<List<Product>> {
        return withContext(Dispatchers.Main) {
            fetchProductsFull(bestSellingListener)
            bestSellingAll
        }
    }

    private suspend fun fetchProductsFull(bestSellingListener: BestSellingListener) {
        try {
            val response = apiRequest { api.getBestSellingFull("20") }
            bestSellingAll.postValue(response.data)
        } catch (e: ApiException){
            bestSellingListener?.onFailure(Constant.API_ERROR)
        } catch (e: NoInternetException){
            bestSellingListener?.onFailure(Constant.NO_INTERNET)
        } catch (e: Exception) {
            e.printStackTrace()
            bestSellingListener?.onFailure(Constant.API_ERROR)
        }
    }

    suspend fun getPopular(pageindex : Int): List<Product> {
        return withContext(Dispatchers.IO) {
            val response = apiRequest { api.getPopular(pageindex,8) }
            response.data
        }
    }

    /*private suspend fun fetchPopular(pageindex : Int) {
        try {
            val response = apiRequest { api.getPopular(pageindex,8) }
            popular.postValue(response.data)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/

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

    fun getCountCart() = db.getCartDao().getCount()
}