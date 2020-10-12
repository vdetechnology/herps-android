package herbs.n.more.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import herbs.n.more.R

class DetailActivity : AppCompatActivity(R.layout.activity_detail){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right
        )
    }
}