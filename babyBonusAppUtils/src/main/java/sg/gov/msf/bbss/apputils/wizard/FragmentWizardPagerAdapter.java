package sg.gov.msf.bbss.apputils.wizard;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by bandaray on 20/1/2015.
 */
public class FragmentWizardPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;

    public FragmentWizardPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {
        super(fragmentManager);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

}

