package herbs.n.more.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import herbs.n.more.R
import herbs.n.more.ui.MainActivity
import kotlinx.android.synthetic.main.activity_main.*

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val main: MainActivity = activity as MainActivity
        main.bottomBar.visibility = View.VISIBLE
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

}