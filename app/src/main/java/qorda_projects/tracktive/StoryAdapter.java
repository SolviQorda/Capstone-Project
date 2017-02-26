package qorda_projects.tracktive;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import qorda_projects.tracktive.sync.ItemChoiceManager;

/**
 * Created by sorengoard on 11/01/2017.
 */

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryAdapterViewHolder>{

    private static final String LOG_TAG = StoryAdapter.class.getSimpleName().toString();

    //onclickhandler
    final private StoryAdapterOnClickHandler mOnClickHandler;
    final private View mEmptyView;

    final private Context mContext;
    private ArrayList<Story> mStories;
    final private ItemChoiceManager mICM;


    public class StoryAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //Instantiate views
        public final TextView mTitleView;
        public final TextView mDateView;
        public final TextView mSourceView;
        public final ImageView mBookmarked;

        public StoryAdapterViewHolder(View view) {
            super(view);
        //assign views to views on layout
            mTitleView = (TextView) view.findViewById(R.id.card_story_title);
            mDateView = (TextView) view.findViewById(R.id.card_list_date);
            mSourceView = (TextView) view.findViewById(R.id.card_story_source);
            mBookmarked = (ImageView) view.findViewById(R.id.list_item_bookmark_icon);
            view.setOnClickListener(this);
        }

        //Handle onClick ic_add interface clickHandler

        @Override
        public void onClick(View v) {
            int adapterPosition = getLayoutPosition();
            Story story = mStories.get(adapterPosition);
            int dbId = story.getDbId();
            Log.v(LOG_TAG, "dbId of story clicked: " + dbId);
            mOnClickHandler.onClick(dbId, this);
            mICM.onClick(this);
            Log.v(LOG_TAG, "onClick called in storyadapter");

        }

    }

    public interface StoryAdapterOnClickHandler {
        void onClick(int idPosition, StoryAdapterViewHolder viewHolder);
    }

    public StoryAdapter(Context context, StoryAdapterOnClickHandler onClickHandler, View emptyView, int choiceMode, ArrayList<Story> stories) {
        mContext = context;
        mOnClickHandler = onClickHandler;
        mEmptyView = emptyView;
        mStories = stories;
        mICM = new ItemChoiceManager(this);
        mICM.setChoiceMode(choiceMode);
    }

    @Override
    public StoryAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewGroup instanceof RecyclerView) {
            int layoutId = R.layout.card_list_item;

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
            view.setFocusable(true);
            return new StoryAdapterViewHolder(view);
        } else {
                throw new RuntimeException("Not bound to RecyclerView");
            }
        }

    @Override
    public void onBindViewHolder (StoryAdapterViewHolder storyAdapterViewHolder, int position) {

        Story story = mStories.get(position);

        String title = story.getTitle();
        storyAdapterViewHolder.mTitleView.setText(title);

        String source = story.getSource();
        storyAdapterViewHolder.mSourceView.setText(source);

        String date = story.getDate();
        storyAdapterViewHolder.mDateView.setText(date);

        String bookmarked = story.getBookmarked();
        int drawableId = Utility.bookmarkedOrNot(bookmarked);
        String bookmarkedString = mContext.getString(R.string.bookmarked);
        String unBookmarkedString = mContext.getString(R.string.un_bookmarked);
        if(bookmarked.equals("1")){
            storyAdapterViewHolder.mBookmarked.setContentDescription(bookmarkedString);
        } else {
            storyAdapterViewHolder.mBookmarked.setContentDescription(unBookmarkedString);
        }

        storyAdapterViewHolder.mBookmarked.setImageDrawable(mContext.getDrawable(drawableId));

        mICM.onBindViewHolder(storyAdapterViewHolder, position);

    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mICM.onRestoreInstanceState(savedInstanceState);
    }

    public void onSaveInstanceState(Bundle outState) {mICM.onSaveInstanceState(outState);}

    public int getSelectedItemPosition() {return mICM.getSelectedItemPosition(); }

    public void selectView(RecyclerView.ViewHolder viewHolder) {
            if(viewHolder instanceof StoryAdapterViewHolder) {
                StoryAdapterViewHolder storyAdapterViewHolder = (StoryAdapterViewHolder) viewHolder;
                storyAdapterViewHolder.onClick(storyAdapterViewHolder.itemView);
            }
        }


    @Override
    public int getItemCount() {
        if(null == mStories) return 0;
        return mStories.size();
    }

    public void swapArrayList (ArrayList<Story> newArrayList) {
        mStories = newArrayList;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public ArrayList<Story> getStories() {return mStories;}


}
