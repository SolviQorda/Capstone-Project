package qorda_projects.tracktive;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import static qorda_projects.tracktive.sync.TracktiveSyncAdapter.LOG_TAG;

/**
 * Created by sorengoard on 10/01/2017.
 */

public class CardPagerAdapter extends FragmentStatePagerAdapter{
    int mNumofTabs;
    private final String ADAPTER_POS = "adapter_position";

    public CardPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumofTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position){

        Fragment cardFragment = new CardFragment();
        Bundle args = new Bundle();
        args.putInt(ADAPTER_POS, position);
        Log.v(LOG_TAG, "adapter pos =" + position);
        cardFragment.setArguments(args);

        //find a way to ensure that the fragment gets instantiated with the right adapter.
        return cardFragment;
    }

    @Override
    public int getCount() {
        return mNumofTabs;
    }
}
