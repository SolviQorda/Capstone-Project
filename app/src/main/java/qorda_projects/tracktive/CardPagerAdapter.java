package qorda_projects.tracktive;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by sorengoard on 10/01/2017.
 */

public class CardPagerAdapter extends FragmentStatePagerAdapter{
    private List<CardFragment> fragments;
    private final String ADAPTER_POS = "adapter_position";

    public CardPagerAdapter(FragmentManager fm, List<CardFragment>fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public CardFragment getItem(int position){

        return this.fragments.get(position);
    }

    @Override
    public int getCount() {
        if (fragments != null) {
            return this.fragments.size();
        } else {
            return 0;
        }
    }
}
