package qorda_projects.tracktive;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import java.util.ArrayList;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

import static android.util.Log.v;

/**
 * Created by sorengoard on 09/01/2017.
 */

public class CardFragment extends Fragment{

    private static final String LOG_TAG = CardFragment.class.getSimpleName().toString();
    public final String DIALOG_TAG = "new card dialog";
    static final String CARD_URI = "cardUri";
    public static Uri mUri;
    private Uri mCardSpecificUri;
    public static int mCardPosition;

    private StoryAdapter mStoryAdapter;
    private RecyclerView mCardRecyclerView;
    private int mChoiceMode;
    private ArrayList<Story> mStories;






    public static final CardFragment newInstance(String title, String keywords) {

        CardFragment cardFragment = new CardFragment();

        return cardFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = this.getArguments();
        if(mStories == null) {
            if(b != null) {
                mStories = b.getParcelableArrayList("cardStoriesArrayList");
                Log.v(LOG_TAG, "mStories taken from SIS bundle " + mStories);

            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        RecyclerView mCardRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_cards);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mCardRecyclerView.setLayoutManager(llm);

        //Testing to see if bundle is passed from MainActivity

        //empty view
        View emptyView = rootView.findViewById(R.id.recyclerview_stories_empty);

        mCardRecyclerView.setHasFixedSize(true);

        mStoryAdapter =  new StoryAdapter(getActivity(), new StoryAdapter.StoryAdapterOnClickHandler() {
            @Override
            public void onClick(StoryAdapter.StoryAdapterViewHolder viewHolder) {


//                ((Callback) getActivity()).onItemSelected();

            }
        }, emptyView, AbsListView.CHOICE_MODE_NONE, mStories);

        mCardRecyclerView.setAdapter(mStoryAdapter);

        if (savedInstanceState!= null) {
            mStoryAdapter.onRestoreInstanceState(savedInstanceState);
        }

        if(mStories != null) {
            emptyView.setVisibility(View.INVISIBLE);
        }

        FabSpeedDial makeCardFab = (FabSpeedDial) rootView.findViewById(R.id.make_card_fab);
        makeCardFab.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();

                if(id == R.id.new_card){
                    v(LOG_TAG, "add card called");
                    openNewCardDialog();
                    return true;
                }
                else if (id == R.id.diary_view) {
                    Intent diaryIntent = new Intent(getContext(), DiaryActivity.class);
                    getContext().startActivity(diaryIntent);
                    return true;
                }
                else if (id == R.id.new_diary_entry) {
//                    openNewDiaryEntryDialog
                            return true;
                }
                return super.onMenuItemSelected(menuItem);
            }

        });

        makeCardFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        return  rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
//        mStoryAdapter.onSaveInstanceState(outState);
//        outState.putParcelable("localCardUri", mCardSpecificUri);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        Bundle args = new Bundle();
//        args.putParcelable("localCardUri", mCardSpecificUri);
//        onSaveInstanceState(args);
        if(null != mCardRecyclerView) {
            mCardRecyclerView.clearOnScrollListeners();
        }
    }

    public void openNewCardDialog() {
        DialogFragment newCardDialog = new KeywordsEntryDialog();
        FragmentManager manager = getActivity().getSupportFragmentManager();
        newCardDialog.show(manager, DIALOG_TAG);

    }

}
