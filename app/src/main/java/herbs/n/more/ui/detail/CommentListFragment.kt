package herbs.n.more.ui.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import herbs.n.more.R
import herbs.n.more.data.db.entities.Comment
import herbs.n.more.databinding.FragmentCommentListBinding
import herbs.n.more.ui.BaseFragment
import herbs.n.more.util.Constant
import herbs.n.more.util.Coroutines
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class CommentListFragment : BaseFragment() , KodeinAware, DetailListener {

    private lateinit var binding: FragmentCommentListBinding
    override val kodein by kodein()
    private val factory: DetailViewModelFactory by instance()
    private lateinit var viewModel: DetailProductViewModel
    private var mCommentAdapter = GroupAdapter<GroupieViewHolder>()
    private var pageindex : Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_comment_list, container, false)
        binding.fragment = this
        binding.lifecycleOwner = this
        viewModel = ViewModelProviders.of(this, factory).get(DetailProductViewModel::class.java)
        viewModel.detailListener = this
        initView()
        initData()

        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView(){
        binding.svHome.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                pageindex += 1
                loadMoreComment()
                binding.pbLoadMore.visibility = View.VISIBLE
            }
        })
    }

    private fun initData(){
        GlobalScope.async{bindDataComment()}
    }

    private fun bindDataComment() = Coroutines.main {
        viewModel.getComments(arguments?.getInt("id")!!.toString(), pageindex, 10)?.let{
            mCommentAdapter = GroupAdapter<GroupieViewHolder>().apply {
                if (it.isNotEmpty()) {
                    addAll(it.toCommentItem())
                }
            }
            binding.rvComment.apply {
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                adapter = mCommentAdapter
            }
        }
    }

    private fun List<Comment>.toCommentItem() : List<CommentItem>{
        return this.map {
            CommentItem(requireActivity(), it)
        }
    }

    private fun loadMoreComment() = Coroutines.main {
        viewModel.getComments(arguments?.getInt("id")!!.toString(), pageindex, 10)?.let{
            if (it.isNotEmpty()) {
                mCommentAdapter.addAll(it.toCommentItem())
            }
            binding.pbLoadMore.visibility = View.GONE
        }
    }

    override fun onStarted() {
        binding.rlLoading.visibility = View.VISIBLE
    }

    override fun onSuccess() {
        binding.rlLoading.visibility = View.GONE
    }

    override fun onFailure(message: String) {
        binding.rlLoading.visibility = View.GONE
        when(message) {
            Constant.API_ERROR -> (activity as DetailActivity).showMessage(
                resources.getString(R.string.server_error_title),
                resources.getString(R.string.server_error)
            )
            Constant.NO_INTERNET -> (activity as DetailActivity).showMessage(
                resources.getString(R.string.network_error_title),
                resources.getString(R.string.network_error)
            )
        }
    }
}