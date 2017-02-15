package qorda_projects.tracktive;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by sorengoard on 07/01/2017.
 */

public class StoryDetailActivity extends AppCompatActivity {

    public final String DETAIL_URI = "detailUri";
    //inflate layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_main);

        if (savedInstanceState == null) {

            Bundle args = new Bundle();
            if (args != null) {
                args.putParcelable(DETAIL_URI, getIntent().getData());
            }

            StoryDetailFragment detailFragment = new StoryDetailFragment();
            detailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction().add(R.id.container, detailFragment)
                    .commit();
        }
    }

    //onClickListener for Browser button - handles implicit intent.
}
