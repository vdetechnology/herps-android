package herbs.n.more.ui.search

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import herbs.n.more.R
import herbs.n.more.databinding.ActivityFilterBinding
import java.text.DecimalFormat

class FilterActivity : AppCompatActivity() {

    private lateinit var bind: ActivityFilterBinding
    private var leftValue: Float = 0f
    private var rightValue: Float = 10000000f
    private var listCategory = arrayListOf<String>()
    private var q: String = ""
    private var sort: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        overridePendingTransition(R.anim.anim_slide_from_bottom, R.anim.anim_stay);
        bind = DataBindingUtil.setContentView(this, R.layout.activity_filter)
        bind.activity = this
        initData()
        initView()

    }

    private fun initData() {
        q = intent.getStringExtra("q").toString()
        sort = intent.getIntExtra("sort", -1)
        if(intent.getStringArrayListExtra("category") != null) {
            listCategory = intent.getStringArrayListExtra("category") as ArrayList<String>
        }
        leftValue = intent.getFloatExtra("price_from", 0f)
        rightValue = intent.getFloatExtra("price_to", 10000000f)
    }

    private fun initView() {
        ArrayAdapter.createFromResource(
            this,
            R.array.filter_array,
            R.layout.item_spinner
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
            bind.snFilter.adapter = adapter
            bind.snFilter.setSelection(sort)
        }

        bind.sbRange?.setRange(0f, 20000000f)
        bind.sbRange?.setProgress(leftValue, rightValue)
        bind.sbRange?.setIndicatorTextDecimalFormat("###,###,##0đ")
        bind.sbRange.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onRangeChanged(
                view: RangeSeekBar?,
                leftValue: Float,
                rightValue: Float,
                isFromUser: Boolean
            ) {
                this@FilterActivity.leftValue = leftValue
                this@FilterActivity.rightValue = rightValue
            }

            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
            }

            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
            }

        })

        for (string in listCategory){
            when(string){
                "20" -> {
                    bind.tbFunctionalFoods.isChecked = true
                    bind.tbFunctionalFoods.setTextColor(resources.getColor(R.color.colorWhite))
                }
                "16" -> {
                    bind.tbAmericanGinseng.isChecked = true
                    bind.tbAmericanGinseng.setTextColor(resources.getColor(R.color.colorWhite))
                }
                "25" -> {
                    bind.tbGiftSet.isChecked = true
                    bind.tbGiftSet.setTextColor(resources.getColor(R.color.colorWhite))
                }
            }
        }

        bind.tbFunctionalFoods.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                buttonView.setTextColor(resources.getColor(R.color.colorWhite))
                listCategory.add("20")
            }else{
                buttonView.setTextColor(resources.getColor(R.color.text_color_hint))
                listCategory.remove("20")
            }
        }
        bind.tbAmericanGinseng.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                buttonView.setTextColor(resources.getColor(R.color.colorWhite))
                listCategory.add("16")
            }else{
                buttonView.setTextColor(resources.getColor(R.color.text_color_hint))
                listCategory.remove("16")
            }
        }
        bind.tbGiftSet.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                listCategory.add("25")
                buttonView.setTextColor(resources.getColor(R.color.colorWhite))
            }else{
                buttonView.setTextColor(resources.getColor(R.color.text_color_hint))
                listCategory.remove("25")
            }
        }
    }

    fun convertMoney(double: Double) : String{
        val mDecimalFormat = DecimalFormat("###,###,##0")
        return mDecimalFormat.format(double).replace(",", ".") + resources?.getString(R.string.vnd)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.anim_stay, R.anim.anim_slide_out_bottom)
    }

    fun clearFilter(){
        bind.snFilter.setSelection(0)
        bind.tbFunctionalFoods.isChecked = false
        bind.tbAmericanGinseng.isChecked = false
        bind.tbGiftSet.isChecked = false
        bind.sbRange?.setProgress(0f, 10000000f)
    }

    fun goToSearchResult() {
        val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("q", q)
            intent.putExtra("sort", bind.snFilter.selectedItemPosition)
            intent.putStringArrayListExtra("category", listCategory)
            intent.putExtra("price_from", leftValue)
            intent.putExtra("price_to", rightValue)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.anim_stay, R.anim.anim_slide_out_bottom)
    }

}