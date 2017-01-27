package qorda_projects.tracktive;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import qorda_projects.tracktive.sync.ItemChoiceManager;

/**
 * Created by sorengoard on 11/01/2017.
 */

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryAdapterViewHolder>{

    private static final String LOG_TAG = StoryAdapter.class.getSimpleName().toString();

    //onclickhandler
    final private StoryAdapterOnClickHandler mOnClickHandler;
    final private View mEmptyView;

    private Cursor mCursor;
    final private Context mContext;
    final private ItemChoiceManager mICM;


    public class StoryAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //Instantiate views
        public final TextView mTitleView;
        public final TextView mDateView;
        public final TextView mSourceView;
        public final ImageButton mBookmarked;

        public StoryAdapterViewHolder(View view) {
            super(view);
        //assign views to views on layout
            mTitleView = (TextView) view.findViewById(R.id.card_story_title);
            mDateView = (TextView) view.findViewById(R.id.card_list_date);
            mSourceView = (TextView) view.findViewById(R.id.card_story_source);
            mBookmarked = (ImageButton) view.findViewById(R.id.list_item_bookmark_icon);
        }

        //Handle onClick ic_add interface clickHandler

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            mOnClickHandler.onClick(this);
            mICM.onClick(this);
        }

    }

    public interface StoryAdapterOnClickHandler {
        void onClick(StoryAdapterViewHolder viewHolder);
    }

    public StoryAdapter(Context context, StoryAdapterOnClickHandler onClickHandler, View emptyView, int choiceMode) {
        mContext = context;
        mOnClickHandler = onClickHandler;
        mEmptyView = emptyView;

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

        mCursor.moveToPosition(position);

        String title = mCursor.getString(CardFragment.COL_STORY_TITLE);
        storyAdapterViewHolder.mTitleView.setText(title);

        String source = mCursor.getString(CardFragment.COL_STORY_SOURCE);
        storyAdapterViewHolder.mSourceView.setText(source);

        String date = mCursor.getString(CardFragment.COL_STORY_DATE);
        storyAdapterViewHolder.mDateView.setText(date);

        int bookmarked = mCursor.getInt(CardFragment.COL_STORY_BOOKMARKED);
        int drawableId = Utility.bookmarkedOrNot(bookmarked);

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
        if(null == mCursor) return 0;
        return mCursor.getCount();
    }

    public void swapCursor (Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public Cursor getCursor() {return mCursor;}


}
