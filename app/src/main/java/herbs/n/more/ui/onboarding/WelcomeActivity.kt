package herbs.n.more.ui.onboarding

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.utils.BannerUtils
import com.zhpan.indicator.enums.IndicatorSlideMode
import herbs.n.more.R
import herbs.n.more.ui.MainActivity
import herbs.n.more.ui.adapter.WelcomeAdapter
import herbs.n.more.ui.bean.CustomBean
import herbs.n.more.ui.transform.PageTransformerFactory
import herbs.n.more.ui.viewholder.CustomPageViewHolder
import herbs.n.more.ui.transform.TransformerStyle
import kotlinx.android.synthetic.main.activity_welcome.*
import kotlinx.android.synthetic.main.fragment_first_screen.view.*
import java.util.*
import kotlin.collections.ArrayList

class WelcomeActivity : AppCompatActivity() {

    private lateinit var mViewPager: BannerViewPager<CustomBean, CustomPageViewHolder>
    protected var mDrawableList: MutableList<Int> = ArrayList()

    private var des : Array<String?> = arrayOfNulls(3)

    private val data: List<CustomBean>
        get() {
            val list = ArrayList<CustomBean>()
            des = arrayOf(resources.getString(R.string.description1), resources.getString(R.string.description2), resources.getString(R.string.description3))
            for (i in 0..2) {
                val drawable = resources.getIdentifier("guide$i", "mipmap", packageName)
                mDrawableList.add(drawable)
            }
            for (i in mDrawableList.indices) {
                val customBean = CustomBean()
                customBean.imageRes = mDrawableList[i]
                customBean.imageDescription = des[i]
                list.add(customBean)
            }
            return list
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        supportActionBar?.hide()
        setupViewPager()
        updateUI(0)
    }

    private fun setupViewPager() {
        mViewPager = findViewById(R.id.viewpager)
        mViewPager.apply {
            setCanLoop(false)
            setPageTransformer(PageTransformerFactory.createPageTransformer(TransformerStyle.ACCORDION))
            setIndicatorMargin(0, 0, 0, resources.getDimension(R.dimen.dp_100).toInt())
            setIndicatorSliderGap(resources.getDimension(R.dimen.dp_10).toInt())
            setIndicatorSlideMode(IndicatorSlideMode.WORM)
            setIndicatorSliderRadius(resources.getDimension(R.dimen.dp_3).toInt(), resources.getDimension(R.dimen.dp_4).toInt())
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    BannerUtils.log("position:$position")
                    updateUI(position)
                }
            })

            adapter = WelcomeAdapter().apply {
                mOnSubViewClickListener = CustomPageViewHolder.OnSubViewClickListener { _, position ->
                    //goToMain()
                }
            }
            setIndicatorSliderColor(ContextCompat.getColor(this@WelcomeActivity, R.color.colorWhite),
                    ContextCompat.getColor(this@WelcomeActivity, R.color.white_alpha_75))
        }.create(data)
    }

    fun onClick(view: View) {
        goToMain()
        finish()
    }

    fun onNext(view: View) {
        mViewPager.setCurrentItem(mViewPager.currentItem + 1)
    }

    private fun goToMain(){
        val intent = Intent(this, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun updateUI(position: Int) {
        tv_describe?.text = des[position]
        val translationAnim = ObjectAnimator.ofFloat(tv_describe, "translationX", -120f, 0f)
        translationAnim.apply {
            duration = ANIMATION_DURATION.toLong()
            interpolator = DecelerateInterpolator()
        }
        val alphaAnimator = ObjectAnimator.ofFloat(tv_describe, "alpha", 0f, 1f)
        alphaAnimator.apply {
            duration = ANIMATION_DURATION.toLong()
        }
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(translationAnim, alphaAnimator)
        animatorSet.start()

        if (position == mViewPager.data.size - 1 && btn_start?.visibility == View.GONE) {
            btn_start?.visibility = View.VISIBLE
            btn_next?.visibility = View.GONE
            ObjectAnimator
                    .ofFloat(btn_start, "alpha", 0f, 1f)
                    .setDuration(ANIMATION_DURATION.toLong()).start()
        } else {
            btn_start?.visibility = View.GONE
            btn_next?.visibility = View.VISIBLE
            ObjectAnimator
                .ofFloat(btn_next, "alpha", 0f, 1f)
                .setDuration(ANIMATION_DURATION.toLong()).start()
        }
    }

    companion object {
        private const val ANIMATION_DURATION = 700
    }
}
