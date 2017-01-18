package qorda_projects.tracktive;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by sorengoard on 11/01/2017.
 */

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryAdapterViewHolder>{


    public class StoryAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //Instantiate views
        public final TextView mTitleView;

        public StoryAdapterViewHolder(View view) {
            super(view);
        //assign views to views on layout
            mTitleView = (TextView) view.findViewById(R.id.card_story_title);
        //....
        }

        @Override
        public void onClick(View v) {


        }
    }
}
