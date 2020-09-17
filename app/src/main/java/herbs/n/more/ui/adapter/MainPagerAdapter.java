package herbs.n.more.ui.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class MainPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> data;

    public MainPagerAdapter(FragmentManager fm, List<Fragment> data) {
        super(fm);
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Fragment getItem(int position) {
        return data.get(position);
    }
}
