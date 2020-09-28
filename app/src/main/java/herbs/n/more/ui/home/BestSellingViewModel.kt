package herbs.n.more.ui.home

import androidx.lifecycle.ViewModel
import herbs.n.more.data.repositories.BestSellingRepository
import herbs.n.more.util.transform.lazyDeferred

class BestSellingViewModel(
    repository: BestSellingRepository
) : ViewModel() {
    val bestSelling by lazyDeferred {
        repository.getBestSelling()
    }

    val user = repository.getUser()
}