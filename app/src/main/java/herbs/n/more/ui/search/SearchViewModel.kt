package herbs.n.more.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import herbs.n.more.data.db.entities.Cart
import herbs.n.more.data.db.entities.SearchHistory
import herbs.n.more.data.repositories.SearchRepository
import herbs.n.more.util.Coroutines

class SearchViewModel(
    private val repository: SearchRepository
) : ViewModel() {

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


}
