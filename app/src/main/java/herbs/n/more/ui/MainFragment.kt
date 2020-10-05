package herbs.n.more.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import herbs.n.more.R
import herbs.n.more.databinding.FragmentMainBinding
import herbs.n.more.ui.adapter.MainPagerAdapter
import herbs.n.more.ui.category.CategoryFragment
import herbs.n.more.ui.home.HomeFragment
import herbs.n.more.ui.notification.NotificationFragment
import herbs.n.more.ui.profile.ProfileFragment
import herbs.n.more.ui.saved.SavedFragment

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
        initData()
        initEvent()
    }

    private fun initView() {
        binding?.bottomBar?.enableItemShiftingMode(false)
        binding?.bottomBar?.enableAnimation(true)
        binding?.bottomBar?.enableShiftingMode(false)
        //bind?.bottomBar?.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_transparent))
        binding?.bottomBar?.setTextSize(11f)
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

        var adapter = MainPagerAdapter(activity?.supportFragmentManager,fragmentList)
        binding?.vpMain?.offscreenPageLimit = 5
        binding?.vpMain?.setAdapter(adapter)

        // binding with ViewPager
        binding?.bottomBar?.setupWithViewPager(binding!!.vpMain)
    }

    /**
     * set listeners
     */
    private fun initEvent() {
        // set listener to do something then item selected
        binding?.bottomBar?.setOnNavigationItemSelectedListener { item ->
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