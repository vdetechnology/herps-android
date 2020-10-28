package herbs.n.more.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomnavigation.BottomNavigationView
import herbs.n.more.R
import herbs.n.more.data.db.entities.User
import herbs.n.more.databinding.ActivityMainBinding
import herbs.n.more.ui.adapter.MainPagerAdapter
import herbs.n.more.ui.category.CategoryFragment
import herbs.n.more.ui.dialog.ConfirmLoginDialog
import herbs.n.more.ui.dialog.MessageDialogFragment
import herbs.n.more.ui.home.BestSellingViewModel
import herbs.n.more.ui.home.BestSellingViewModelFactory
import herbs.n.more.ui.home.HomeFragment
import herbs.n.more.ui.notification.NotificationFragment
import herbs.n.more.ui.profile.ProfileFragment
import herbs.n.more.ui.saved.SavedFragment
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import q.rorbin.badgeview.Badge
import q.rorbin.badgeview.QBadgeView


class MainActivity : AppCompatActivity(), KodeinAware, BottomNavigationView.OnNavigationItemSelectedListener {

    private var bind: ActivityMainBinding? = null
    override val kodein by kodein()
    private val factory: BestSellingViewModelFactory by instance()
    private lateinit var viewModel: BestSellingViewModel
    private var user : User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProviders.of(this, factory).get(BestSellingViewModel::class.java)
        supportActionBar?.hide()
        initView()
        initData()
        initEvent()
    }

    /**
     * change BottomNavigationViewEx style
     */
    private fun initView() {
        bind?.bottomBar?.enableItemShiftingMode(false)
        bind?.bottomBar?.enableAnimation(true)
        bind?.bottomBar?.enableShiftingMode(false)
        //bind?.bottomBar?.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_transparent))
        bind?.bottomBar?.setTextSize(11f)
    }

    /**
     * create fragments
     */
    fun initData() {
        val fragmentList = arrayListOf<Fragment>(
            HomeFragment(),
            CategoryFragment(),
            SavedFragment(),
            NotificationFragment(),
            ProfileFragment()
        )

        var adapter = MainPagerAdapter(supportFragmentManager, fragmentList)
        bind?.vpMain?.offscreenPageLimit = 5
        bind?.vpMain?.adapter = adapter

        // binding with ViewPager
        bind?.bottomBar?.setupWithViewPager(bind!!.vpMain)

        viewModel.user.observe(this, androidx.lifecycle.Observer { user ->
            this.user = user
        })
    }

    /**
     * go to home screen
     */
    fun goHome() {
        // set listener to do something then item selected
        bind?.bottomBar?.currentItem = 0
    }

    /**
     * set listeners
     */
    private fun initEvent() {
        // set listener to do something then item selected
        bind?.bottomBar?.onNavigationItemSelectedListener = this@MainActivity
    }

    fun showMessage(title: String, message: String) {
        MessageDialogFragment(title, message).apply {show(supportFragmentManager, "TAG") }
    }

    /**
     * add badge to item notification
     */
    private fun addBadgeAt(position: Int, number: Int): Badge? {
        // add badge
        return QBadgeView(this)
            .setBadgeNumber(number)
            .setGravityOffset(12f, 2f, true)
            .bindTarget(bind?.bottomBar?.getBottomNavigationItemView(position))
    }

    /**
     * on Navigation Item Selected
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.profileFragment, R.id.savedFragment, R.id.notifiFragment -> {
                if (user == null){
                    val dialog : ConfirmLoginDialog? = ConfirmLoginDialog(this)
                    dialog?.show()
                    return false
                }
                return true
            }
        }
        return true
    }
}