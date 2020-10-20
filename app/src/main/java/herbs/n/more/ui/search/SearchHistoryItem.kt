package herbs.n.more.ui.search

import com.xwray.groupie.databinding.BindableItem
import herbs.n.more.R
import herbs.n.more.data.db.entities.SearchHistory
import herbs.n.more.databinding.ItemSearchHistoryBinding


class SearchHistoryItem(
    private val searchHistory: SearchHistory,
    private val listener: SearchHistoryItemListener
) : BindableItem<ItemSearchHistoryBinding>(){

    override fun getLayout() = R.layout.item_search_history

    override fun bind(viewBinding: ItemSearchHistoryBinding, position: Int) {

        viewBinding.history = searchHistory
        viewBinding.rlHistory.setOnClickListener {
            listener.onItemClicked(searchHistory)
        }
    }
}

interface SearchHistoryItemListener {
    fun onItemClicked(searchHistory: SearchHistory)
}