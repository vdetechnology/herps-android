package herbs.n.more.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import herbs.n.more.data.repositories.SearchRepository

@Suppress("UNCHECKED_CAST")
class SearchViewModelFactory(
    private val repository: SearchRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchViewModel(repository) as T
    }
}