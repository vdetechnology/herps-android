package herbs.n.more.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.utils.BannerUtils
import com.zhpan.indicator.enums.IndicatorSlideMode
import herbs.n.more.R
import herbs.n.more.ui.adapter.WelcomeAdapter
import herbs.n.more.util.bean.CustomBean
import herbs.n.more.util.transform.PageTransformerFactory
import herbs.n.more.ui.viewholder.CustomPageViewHolder
import herbs.n.more.util.transform.TransformerStyle
import kotlinx.android.synthetic.main.activity_welcome.*
import net.simplifiedcoding.mvvmsampleapp.data.preferences.PreferenceProvider
import kotlin.collections.ArrayList

class IntroActivity : AppCompatActivity() {

    private lateinit var mViewPager: BannerViewPager<CustomBean, CustomPageViewHolder>
    protected var mDrawableList: MutableList<Int> = ArrayList()

    private var tit : Array<String?> = arrayOfNulls(4)
    private var des : Array<String?> = arrayOfNulls(4)

    private val data: List<CustomBean>
        get() {
            val list = ArrayList<CustomBean>()
            tit = arrayOf(resources.getString(R.string.title0), resources.getString(R.string.title1), resources.getString(R.string.title2), resources.getString(R.string.title3))
            des = arrayOf(resources.getString(R.string.description0), resources.getString(R.string.description1), resources.getString(R.string.description2), resources.getString(R.string.description3))
            for (i in 0..3) {
                val drawable = resources.getIdentifier("intro_slide_$i", "mipmap", packageName)
                mDrawableList.add(drawable)
            }
            for (i in mDrawableList.indices) {
                val customBean = CustomBean()
                customBean.imageRes = mDrawableList[i]
                customBean.imageTitle = tit[i]
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
            setPageTransformer(PageTransformerFactory.createPageTransformer(TransformerStyle.SCALE_IN))
            setIndicatorMargin(0, 0, 0, resources.getDimension(R.dimen.dp_130).toInt())
            setIndicatorSliderGap(resources.getDimension(R.dimen.dp_10).toInt())
            setIndicatorSlideMode(IndicatorSlideMode.WORM)
            setIndicatorSliderRadius(resources.getDimension(R.dimen.dp_5).toInt(), resources.getDimension(R.dimen.dp_5).toInt())
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
            setIndicatorSliderColor(ContextCompat.getColor(this@IntroActivity, R.color.colorIndicatorInActive),
                    ContextCompat.getColor(this@IntroActivity, R.color.colorPrimary))
        }.create(data)
    }

    fun onClick(view: View) {
        PreferenceProvider(this).saveIntro(1)
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
        tv_title?.text = tit[position]

        setAnimation(tv_describe)
        setAnimation(tv_title)

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

    private fun setAnimation(textView: TextView){
        val translationAnim = ObjectAnimator.ofFloat(textView, "translationX", -120f, 0f)
        translationAnim.apply {
            duration = ANIMATION_DURATION.toLong()
            interpolator = DecelerateInterpolator()
        }
        val alphaAnimator = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f)
        alphaAnimator.apply {
            duration = ANIMATION_DURATION.toLong()
        }
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(translationAnim, alphaAnimator)
        animatorSet.start()
    }

    companion object {
        private const val ANIMATION_DURATION = 700
    }
}
