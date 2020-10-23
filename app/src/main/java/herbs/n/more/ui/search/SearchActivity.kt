package herbs.n.more.ui.search

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import herbs.n.more.R
import herbs.n.more.data.db.entities.SearchHistory
import herbs.n.more.databinding.ActivitySearchBinding
import herbs.n.more.ui.BaseActivity
import herbs.n.more.util.Coroutines
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*

class SearchActivity : BaseActivity(), KodeinAware, SearchHistoryItemListener {

    override val kodein by kodein()
    private val factory: SearchViewModelFactory by instance()
    private lateinit var viewModel: SearchViewModel
    private lateinit var bind: ActivitySearchBinding

    private var q: String = ""
    private var sort: Int = -1
    private var listCategory = arrayListOf<String>()
    private var fromValue: Float = 0f
    private var toValue: Float = 10000000f

    var searchs = arrayOf(
        "Đường huyết",
        "Nhân sâm",
        "Quà tặng cao cấp",
        "Ung thư",
        "Sinh lý nam",
        "Canxi NANO", "Gan"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = DataBindingUtil.setContentView(this, R.layout.activity_search)
        viewModel = ViewModelProviders.of(this, factory).get(SearchViewModel::class.java)
        bind.activity = this
        bind.lifecycleOwner = this

        initView()
        initData()
    }

    private fun initView() {
        bind.etSearch.requestFocus()
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(bind.etSearch, InputMethodManager.SHOW_IMPLICIT)
        bind.etSearch.doAfterTextChanged {
            if (bind.etSearch.text.isNotEmpty()) {
                bind.ivClear.visibility = View.VISIBLE
            } else {
                bind.ivClear.visibility = View.INVISIBLE
            }
        }

        bind.etSearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (event != null && event.keyCode === KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_SEARCH) {
                closeKeyBoard()
                v.clearFocus()
                q = v.text.toString()
                if (!v.text.isNullOrEmpty()) {
                    GlobalScope.async { getHistory(v.text.toString()) }
                    GlobalScope.async {
                        goToSearchResult(q, sort, listCategory, fromValue, toValue)
                        finish()
                    }
                }else{
                    goToSearchResult(q, sort, listCategory, fromValue, toValue)
                    finish()
                }
            }
            false
        })

        bind.ivClear.setOnClickListener {
            bind.etSearch.setText("")
        }

        for (text in searchs) {
            val chip = LayoutInflater.from(this).inflate(R.layout.item_chip, null) as Chip
            chip.text = text
            chip.setOnClickListener {
                q = chip.text.toString()
                goToSearchResult(q, sort, listCategory, fromValue, toValue)
                finish()
            }
            bind.chipGroup.addView(chip)
        }

    }

    fun seeMore() {
        bind.tvSeeMore.visibility = View.GONE
        bind.tvClear.visibility = View.VISIBLE
        bindDataHistoryMore()
    }

    fun clearHistory() = Coroutines.main {
        bind.tvClear.visibility = View.GONE
        viewModel.deleteHistory()
    }

    private fun initData() {
        q = intent.getStringExtra("q").toString()
        bind.etSearch.setText(q)
        bind.etSearch.setSelection(bind.etSearch.text.length)
        sort = intent.getIntExtra("sort", -1)
        if(intent.getStringArrayListExtra("category") != null) {
            listCategory = intent.getStringArrayListExtra("category") as ArrayList<String>
        }
        fromValue = intent.getFloatExtra("price_from", 0f)
        toValue = intent.getFloatExtra("price_to", 10000000f)
        GlobalScope.async { bindDataHistory() }
        GlobalScope.async { bindCount() }
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun bindDataHistory() = Coroutines.main {
        bind.rlLoading.visibility = View.VISIBLE
        viewModel.getLimit3()?.observe(this, androidx.lifecycle.Observer {
            bind.rlLoading.visibility = View.GONE
            val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
                addAll(it.toHistoryItem())
            }
            bind.rvHistory.apply {
                layoutManager =
                    LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
                adapter = mAdapter
            }
        })
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun getHistory(title: String) = Coroutines.main {
        bind.rlLoading.visibility = View.VISIBLE
        val calendar = Calendar.getInstance()
        var history = viewModel.getHistory(title)
        if (history != null) {
            history.update_date = calendar.timeInMillis
            viewModel.updateHistory(history)
        } else {
            viewModel.saveHistory(SearchHistory(title, calendar.timeInMillis))
        }
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun bindDataHistoryMore() = Coroutines.main {
        bind.rlLoading.visibility = View.VISIBLE
        viewModel.getLimit10()?.observe(this, androidx.lifecycle.Observer {
            bind.rlLoading.visibility = View.GONE
            val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
                addAll(it.toHistoryItem())
            }
            bind.rvHistory.apply {
                layoutManager =
                    LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
                adapter = mAdapter
            }
        })
    }

    private fun bindCount() = Coroutines.main {
        viewModel.countHistory.observe(this, androidx.lifecycle.Observer {
            when {
                it == 0 -> {
                    bind.tvTitleHistory.visibility = View.GONE
                    bind.rvHistory.visibility = View.GONE
                    bind.tvSeeMore.visibility = View.GONE
                    bind.tvClear.visibility = View.GONE
                }
                it <= 3 -> {
                    bind.tvTitleHistory.visibility = View.VISIBLE
                    bind.rvHistory.visibility = View.VISIBLE
                    bind.tvSeeMore.visibility = View.GONE
                    bind.tvClear.visibility = View.VISIBLE
                }
                else -> {
                    bind.tvTitleHistory.visibility = View.VISIBLE
                    bind.rvHistory.visibility = View.VISIBLE
                    bind.tvSeeMore.visibility = View.VISIBLE
                    bind.tvClear.visibility = View.GONE
                }
            }
        })
    }


    private fun List<SearchHistory>.toHistoryItem(): List<SearchHistoryItem> {
        return this.map {
            SearchHistoryItem(it, this@SearchActivity)
        }
    }

    override fun onItemClicked(searchHistory: SearchHistory) {
        bind.etSearch.setText(searchHistory.title)
        bind.etSearch.requestFocus()
        bind.etSearch.setSelection(bind.etSearch.text.length)
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(bind.etSearch, InputMethodManager.SHOW_IMPLICIT)
    }

    /*override fun onFailure(message: String) {
        bind.rlLoading.visibility = View.GONE
        when(message) {
            Constant.API_ERROR -> showMessage(
                resources.getString(R.string.server_error_title),
                resources.getString(R.string.server_error)
            )
            Constant.NO_INTERNET -> showMessage(
                resources.getString(R.string.network_error_title),
                resources.getString(R.string.network_error)
            )
        }
    }*/

}