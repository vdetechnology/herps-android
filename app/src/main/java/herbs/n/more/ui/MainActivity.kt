package herbs.n.more.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import herbs.n.more.R
import herbs.n.more.databinding.ActivityMainBinding
import herbs.n.more.ui.adapter.MainPagerAdapter
import herbs.n.more.ui.category.CategoryFragment
import herbs.n.more.ui.home.HomeFragment
import herbs.n.more.ui.notification.NotificationFragment
import herbs.n.more.ui.profile.ProfileFragment
import herbs.n.more.ui.saved.SavedFragment


class MainActivity : AppCompatActivity() {

    private var bind: ActivityMainBinding? = null
    private val adapter: MainPagerAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = DataBindingUtil.setContentView(this, R.layout.activity_main)
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
    private fun initData() {
        val fragmentList = arrayListOf<Fragment>(
            HomeFragment(),
            CategoryFragment(),
            SavedFragment(),
            NotificationFragment(),
            ProfileFragment()
        )

        var adapter = MainPagerAdapter(supportFragmentManager,fragmentList)
        bind?.vpMain?.offscreenPageLimit = 5
        bind?.vpMain?.setAdapter(adapter)

        // binding with ViewPager
        bind?.bottomBar?.setupWithViewPager(bind!!.vpMain)
    }

    /**
     * set listeners
     */
    private fun initEvent() {
        // set listener to do something then item selected
        bind?.bottomBar?.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                }
                R.id.categoryFragment -> {
                }
                R.id.savedFragment -> {
                }
                R.id.profileFragment -> {
                }
            }
            true
        }
    }

}