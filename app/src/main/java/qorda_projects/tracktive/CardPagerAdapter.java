package qorda_projects.tracktive;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by sorengoard on 10/01/2017.
 */

public class CardPagerAdapter extends FragmentStatePagerAdapter{
    int mNumofTabs;

    public CardPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumofTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position){

        //find a way to ensure that the fragment gets instantiated with the right adapter.
        return new CardFragment();
    }

    @Override
    public int getCount() {
        return mNumofTabs;
    }
}
