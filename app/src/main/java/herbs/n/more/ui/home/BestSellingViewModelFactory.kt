package herbs.n.more.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import herbs.n.more.data.repositories.BestSellingRepository

@Suppress("UNCHECKED_CAST")
class BestSellingViewModelFactory(
    private val repository: BestSellingRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BestSellingViewModel(repository) as T
    }
}