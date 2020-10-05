package herbs.n.more.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import herbs.n.more.data.repositories.DetailProductRepository

@Suppress("UNCHECKED_CAST")
class DetailViewModelFactory(
    private val repository: DetailProductRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DetailProductViewModel(repository) as T
    }
}