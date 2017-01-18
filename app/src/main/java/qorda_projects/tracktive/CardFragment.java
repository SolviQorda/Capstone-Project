package qorda_projects.tracktive;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sorengoard on 09/01/2017.
 */

public class CardFragment extends Fragment {

    private StoryAdapter

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);

        RecyclerView cardRecyclerView = (RecyclerView) findViewById
    }
}
