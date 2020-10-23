package herbs.n.more.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import herbs.n.more.data.db.entities.Product
import herbs.n.more.data.db.entities.SearchHistory
import herbs.n.more.data.repositories.SearchRepository
import herbs.n.more.ui.home.BestSellingListener
import herbs.n.more.util.ApiException
import herbs.n.more.util.Constant
import herbs.n.more.util.Coroutines
import herbs.n.more.util.NoInternetException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class SearchViewModel(
    private val repository: SearchRepository
) : ViewModel() {

    private val bestSelling = MutableLiveData<List<Product>>()
    var bestSellingListener: BestSellingListener? = null

    private var q: String = ""
    private var sort: Int = -1
    private var listCategory = arrayListOf<String>()
    private var fromValue: Float = 0f
    private var toValue: Float = 0f

    val user = repository.getUser()

    suspend fun getLimit3(): LiveData<List<SearchHistory>>? {
        return repository.getLimit3()
    }

    suspend fun getLimit10(): LiveData<List<SearchHistory>>? {
        return repository.getLimit10()
    }

    suspend fun deleteHistory(){
        return repository.deleteHistory()
    }

    suspend fun getHistory(title: String): SearchHistory {
        return repository.getHistory(title)
    }

    val countHistory = repository.getCount()

    fun saveHistory(searchHistory: SearchHistory) {
        Coroutines.io {
            repository.saveSearch(searchHistory)
        }
    }

    fun updateHistory(searchHistory: SearchHistory) {
        Coroutines.io {
            repository.updateHistory(searchHistory)
        }
    }

    val countCart = repository.getCountCart()

    suspend fun getSearchResult(): LiveData<List<Product>> {
        return withContext(Dispatchers.Main) {
            fetchSearch()
            bestSelling
        }
    }

    private suspend fun fetchSearch() {
        Coroutines.main {
            try {
                val response = repository.getSearchResult(q, sort, listCategory, fromValue, toValue)
                response.data?.let {
                    bestSelling.postValue(response.data)
                }
            } catch (e: ApiException) {
                bestSellingListener?.onFailure(Constant.API_ERROR)
            } catch (e: NoInternetException) {
                bestSellingListener?.onFailure(Constant.NO_INTERNET)
            } catch (e: Exception) {
                e.printStackTrace()
                bestSellingListener?.onFailure(Constant.API_ERROR)
            }
        }
    }

    fun saveRecentlys(product: Product) {
        Coroutines.io {
            repository.saveProducts(product)
        }
    }

}
