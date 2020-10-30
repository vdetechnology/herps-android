package herbs.n.more.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.view.animation.*
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.BaseViewHolder
import herbs.n.more.R
import herbs.n.more.data.db.entities.*
import herbs.n.more.databinding.FragmentDetailBinding
import herbs.n.more.ui.BaseFragment
import herbs.n.more.ui.adapter.DetailImagesAdapter
import herbs.n.more.ui.dialog.ConfirmLoginDialog
import herbs.n.more.ui.home.ProductItem
import herbs.n.more.ui.home.ProductItemListener
import herbs.n.more.util.Constant
import herbs.n.more.util.Coroutines
import herbs.n.more.util.Validate
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*
import kotlin.collections.ArrayList

class DetailFragment : BaseFragment() , KodeinAware, DetailListener, ProductItemListener{

    private lateinit var binding: FragmentDetailBinding
    override val kodein by kodein()
    private val factory: DetailViewModelFactory by instance()
    private lateinit var viewModel: DetailProductViewModel

    private var mSuggestedAdapter = GroupAdapter<GroupieViewHolder>()
    private var mCommentAdapter = GroupAdapter<GroupieViewHolder>()
    private var pageindex : Int = 1
    private var loadmore : Boolean = false
    private var loadmoreDisable : Boolean = false
    private var mViewPager: BannerViewPager<String, BaseViewHolder<String>>? = null
    var user : User? = null
    var product: DetailProduct? = null
    private var seeLess : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        binding.activity = this
        binding.lifecycleOwner = this
        viewModel = ViewModelProviders.of(this, factory).get(DetailProductViewModel::class.java)
        viewModel.detailListener = this
        initView()
        initData()

        return binding.root
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

    @SuppressLint("ClickableViewAccessibility")
    private fun initView(){
        binding.swipeRefresh.setColorSchemeColors(resources.getColor(R.color.colorPrimary))
        binding.swipeRefresh.setOnRefreshListener {
            mSuggestedAdapter.clear()
            loadmoreDisable = false
            loadmore = false
            pageindex = 1
            initData()
            binding.swipeRefresh.isRefreshing = false
            binding.etComment.setText("")
        }

        mViewPager = binding.root?.findViewById(R.id.banner_view)
        mViewPager?.apply {
            setIndicatorSliderRadius(resources.getDimensionPixelOffset(R.dimen.dp_4), resources.getDimensionPixelOffset(R.dimen.dp_4))
            setLifecycleRegistry(lifecycle)
            setOnPageClickListener{ position: Int -> pageClick(position) }
            setAdapter(DetailImagesAdapter())
        }?.create()
        setupIndicatorView()

        binding.svHome.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            /*if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                if (!loadmoreDisable) {
                    pageindex += 1
                    loadmore = true
                    loadMorePopular()
                    binding.pbLoadMore.visibility = View.VISIBLE
                }
            }*/
            if (seeLess) {
                if (scrollY == (binding.llDescription.parent as View).top + binding.llDescription.top) {
                    binding.tvDescription.maxLines = 6
                    binding.tvSeeMore.visibility = View.VISIBLE
                    binding.tvSeeLess.visibility = View.GONE
                    seeLess = false
                }
            }
            if(scrollY > 5000){
                binding.fbTop.visibility = View.VISIBLE
            }else{
                binding.fbTop.visibility = View.GONE
            }
        })

        binding.etComment.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (binding.etComment.hasFocus()) {
                    v.parent.requestDisallowInterceptTouchEvent(true)
                    when (event.action and MotionEvent.ACTION_MASK) {
                        MotionEvent.ACTION_SCROLL -> {
                            v.parent.requestDisallowInterceptTouchEvent(false)
                            return true
                        }
                    }
                }
                return false
            }
        })
    }

    private fun pageClick(position: Int){
        val bundle = bundleOf("images" to product?.images,
        "title" to product?.title,
        "position" to position)
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_detailFragment_to_viewImageFragment, bundle)
    }

    private fun setupIndicatorView() {
        mViewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tvNumberPage.text = (position + 1).toString() + "/" + mViewPager!!.data.size
            }
        })
    }

    private fun initData(){
        user = activity?.intent?.getSerializableExtra("user") as User?
        GlobalScope.async{bindCountCart()}
        GlobalScope.async{bindDataDetail()}
        GlobalScope.async{bindDataRelatedProduct()}
        GlobalScope.async{bindDataComment()}
    }

    private fun bindDataDetail() = Coroutines.main {
        val detail = viewModel.getDetail(activity?.intent?.getStringExtra("id").toString())
        detail?.observe(viewLifecycleOwner, Observer {
            binding.product = it
            product = it
            if(it.comment == 0) binding.tvCommentEmpty.visibility = View.VISIBLE
            Glide
                .with(this)
                .load(it.image)
                .error(R.mipmap.ic_logo)
                .centerCrop()
                .into(binding.ivMoveCart)
            if (it.images.isNullOrEmpty()){
                binding.ivNoImage.visibility = View.VISIBLE
                mViewPager?.visibility = View.GONE
                if (it.image.isNullOrEmpty()){
                    binding.ivNoImage.setImageResource(R.drawable.ic_logo)
                    binding.ivNoImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
                }else{
                    Glide
                        .with(this)
                        .load(it.image)
                        .error(R.mipmap.ic_logo)
                        .centerCrop()
                        .into(binding.ivNoImage)
                }
            }else {
                binding.ivNoImage.visibility = View.GONE
                mViewPager?.visibility = View.VISIBLE
                mViewPager?.refreshData(it.images)
            }
            if (it.images?.size!! > 1) {
                binding.tvNumberPage.text = (mViewPager!!.currentItem + 1).toString() + "/" + it.images?.size!!
            }else{
                binding.tvNumberPage.visibility = View.GONE
            }

            binding.rbRating.rating = it.rating!!
            binding.rbRating1.rating = it.rating!!
            if (it.total_sales != 0){
                binding.tvPriceSale.text = (convertMoney(it.sale_price?.toDouble()!!).toString()).replace(",", ".")
                binding.tvPrice.text = (convertMoney(it.price?.toDouble()!!).toString()).replace(",", ".")
                binding.tvPrice.viewTreeObserver.addOnGlobalLayoutListener(object :
                    ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        binding.tvPrice.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        binding.tvPrice.height //height is ready
                        val params: ViewGroup.LayoutParams = binding.line.layoutParams
                        params.width = binding.tvPrice.width
                        binding.line.layoutParams = params
                    }
                })
            }else{
                binding.tvPriceSale.text = (convertMoney(it.price?.toDouble()!!).toString()).replace(",", ".")
            }
            detail.removeObservers(this)
        })
    }

    private fun bindDataRelatedProduct() = Coroutines.main {
        viewModel.getRelatedProduct(activity?.intent?.getStringExtra("id")?.toInt()!!)?.let{
            if (!loadmore) {
                mSuggestedAdapter = GroupAdapter<GroupieViewHolder>().apply {
                    if (it.isNotEmpty()) {
                        addAll(it.toProductItem())
                    }
                }
                binding.rvSuggested.apply {
                    layoutManager = GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false)
                    adapter = mSuggestedAdapter
                }
            }
        }
    }

    private fun loadMorePopular() = Coroutines.main {
        viewModel.getRelatedProduct(pageindex)?.let{
            if (loadmore) {
                if (it.isNotEmpty()) {
                    mSuggestedAdapter.addAll(it.toProductItem())
                } else {
                    loadmoreDisable = true
                }
                binding.pbLoadMore.visibility = View.GONE
            }
        }
    }

    private fun List<Product>.toProductItem() : List<ProductItem>{
        return this.map {
            ProductItem(requireActivity(), it, this@DetailFragment)
        }
    }

    private fun bindDataComment() = Coroutines.main {
        viewModel.getComments(activity?.intent?.getStringExtra("id").toString(), 1, 5)?.let{
                mCommentAdapter = GroupAdapter<GroupieViewHolder>().apply {
                    if (it.isNotEmpty()) {
                        if (it.size < 5){
                            binding.tvSeeAll.visibility = View.GONE
                            addAll(it.toCommentItem())
                        }else {
                            var comments: ArrayList<Comment> = ArrayList()
                            for (x in 0..3) {
                                comments.add(it[x])
                            }
                            addAll(comments.toCommentItem())
                        }
                    }
                }
                binding.rvRating.apply {
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

    private fun bindCountCart() = Coroutines.main {
        viewModel.countCart.observe(viewLifecycleOwner, Observer {
            if (it > 0){
                binding.tvNumberInCart.visibility = View.VISIBLE
                binding.tvNumberInCart.text = it.toString()
            }else{
                binding.tvNumberInCart.visibility = View.GONE
            }
        })
    }

    fun onBackClick(view: View){
        activity?.finish()
        activity?.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
    }

    fun onSeeMoreClick(view: View){
        binding.tvDescription.maxLines = 2000
        binding.tvSeeMore.visibility = View.GONE
        binding.tvSeeLess.visibility = View.VISIBLE
    }

    fun onSeeLessClick(view: View){
        seeLess = true
        val scrollTo: Int =
            (binding.llDescription.parent as View).top + binding.llDescription.top
        binding.svHome.smoothScrollTo(0, scrollTo)
    }

    override fun onItemClicked(product: Product) {
        val calendar = Calendar.getInstance()
        val startTime = calendar.timeInMillis
        product.update_date = startTime
        viewModel.saveRecentlys(product)
        val intent = Intent(activity, DetailActivity::class.java).apply {
            putExtra("id", product.id.toString())
            putExtra("user", user)
        }
        startActivity(intent)

    }

    override fun onLikeClicked(product: Product) {
        if (user != null){

        }else {
            val dialog : ConfirmLoginDialog? = ConfirmLoginDialog(requireActivity())
            dialog?.show()
        }
    }

    fun onLikeDetail() {
        if (user != null){

        }else {
            val dialog : ConfirmLoginDialog? = ConfirmLoginDialog(requireActivity())
            dialog?.show()
        }
    }

    fun flyToCart(){
        if (product != null) {
            val cartLocation = binding.btCart.getLocationOnScreen()
            val imgLocation = binding.ivMoveCart.getLocationOnScreen()

            val animSet = AnimationSet(true)
            animSet.fillAfter = true
            animSet.duration = 700
            val translate: Animation = TranslateAnimation(
                0F,
                cartLocation.x.toFloat() * 4,
                0F,
                (cartLocation.y.toFloat() - imgLocation.y.toFloat()) * 4
            )
            animSet.addAnimation(translate)
            val aniSlide = AnimationUtils.loadAnimation(requireActivity(), R.anim.anim_zoom_out)
            animSet.addAnimation(aniSlide)
            val alphaAnim = AlphaAnimation(1f, 0.5f)
            animSet.addAnimation(alphaAnim)
            binding.ivMoveCart.startAnimation(animSet)
            animSet.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    Handler().postDelayed({
                        binding.ivMoveCart.alpha = 0f
                    }, 200)
                    saveCart()
                }

                override fun onAnimationStart(animation: Animation?) {
                    binding.ivMoveCart.alpha = 1f
                    binding.ivMoveCart.scaleX = 1f
                    binding.ivMoveCart.scaleY = 1f
                }

            })
        }
    }

    private fun saveCart()  = Coroutines.main {
        if (product != null) {
            var cart = viewModel.cartByID(product?.id!!)
            if (cart != null) {
                cart.amount = cart.amount?.plus(1)
                if (binding.product?.total_sales!! > 0) {
                    cart.total_order = cart.amount?.times(cart.sale_price!!)
                } else {
                    cart.total_order = cart.amount?.times(cart.price!!)
                }
                viewModel.saveCart(cart)
            } else {
                val calendar = Calendar.getInstance()
                val cartNew = Cart()
                cartNew.id = binding.product?.id
                cartNew.title = binding.product?.title
                cartNew.image = binding.product?.image
                cartNew.price = binding.product?.price?.toDouble()
                cartNew.total_sales = binding.product?.total_sales
                cartNew.total_sales_percent = binding.product?.total_sales_percent
                cartNew.amount = 1
                cartNew.update_date = calendar.timeInMillis
                if (binding.product?.total_sales!! > 0) {
                    cartNew.total_order = binding.product?.sale_price?.toDouble()
                    cartNew.sale_price = binding.product?.sale_price?.toDouble()
                } else {
                    cartNew.total_order = binding.product?.price?.toDouble()
                }
                viewModel.saveCart(cartNew)
            }
        }
    }

    private fun View.getLocationOnScreen(): Point
    {
        val location = IntArray(2)
        this.getLocationOnScreen(location)
        return Point(location[0],location[1])
    }

    fun buyNow(){
        saveCart()
        goToCart()
    }

    fun scrollTop(){
        binding.svHome.fullScroll(ScrollView.FOCUS_UP)
    }

    private fun checkValidateRating() : Boolean{
        var isComment = false
        var isMail = false
        var isName = false

        if (Validate.isNull(binding.etComment.text.toString())) {
            binding.tvErrComment.text = resources.getString(R.string.rating_is_blank)
        } else {
            binding.tvErrComment.text = ""
            isComment = true
        }

        if (Validate.isNull(binding.etMail.text.toString())) {
            binding.tvErrMail.text = resources.getString(R.string.email_is_blank)
        } else if (!Validate.isValidEmail(binding.etMail.text.toString())) {
            binding.tvErrMail.text = resources.getString(R.string.email_wrong_format)
        } else {
            binding.tvErrMail.text = ""
            isMail = true
        }

        if (Validate.isNull(binding.etName.text.toString())) {
            binding.tvErrName.text = resources.getString(R.string.name_is_blank)
        } else if (Validate.isShorterThan(binding.etName.text.toString(), 2)) {
            binding.tvErrName.text = resources.getString(R.string.name_shorter)
        }else {
            binding.tvErrName.text = ""
            isName = true
        }

        return isComment && isMail && isName
    }

    fun onRatingButtonClick() {
        if (!checkValidateRating())
            return
        addComment()
    }

    private fun addComment() = Coroutines.main {
        viewModel.addComment(product?.id.toString(), user?.id.toString(), binding.etMail.text.toString(),
            binding.etName.text.toString(), binding.rbYourRating.rating.toInt(), binding.etComment.text.toString(),
            "Android device " + android.os.Build.MODEL.toString()).let{
            binding.rlLoading.visibility = View.GONE
            if (it?.code == 200){
                openDialog(it.message)
                binding.etComment.setText("")
            }else{
                binding.etComment.setText("")
            }
        }
    }

    private fun openDialog(message: String) {
        val view: View = layoutInflater.inflate(R.layout.bottom_sheet_rating, null)
        val dialog = BottomSheetDialog(this.requireContext(), R.style.BottomSheetDialogTheme)
        dialog.setContentView(view)
        val tvMessage = view.findViewById<View>(R.id.tv_message) as TextView
        tvMessage.text = message
        val btClose = view.findViewById<View>(R.id.bt_close) as TextView
        btClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun scrollToComment(){
        val scrollTo: Int =
            (binding.llComment.parent as View).top + binding.llComment.top
        binding.svHome.smoothScrollTo(0, scrollTo)
    }
}