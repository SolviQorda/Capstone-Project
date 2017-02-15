package qorda_projects.tracktive;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import qorda_projects.tracktive.data.CardsContract;
import qorda_projects.tracktive.sync.TracktiveSyncAdapter;

public class MainActivity extends AppCompatActivity implements KeywordsEntryDialog.keywordsDialogListener, CardFragment.Callback {

    public final String DIALOG_TAG = "new card dialog";
    private final String LOG_TAG = MainActivity.class.getSimpleName().toString();
    private ViewPager mViewPager;
    public PagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    private SharedPreferences mSharedPreferences;
    public ArrayList<Card> mTitlesAndKeywords;
    public List<CardFragment> mFragments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((MyApplication) getApplication()).startTracking();

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Uri cardUri = getIntent() != null ? getIntent().getData() : null;

        getCardFragments();


        if (mFragments == null) {
            //If no pre-existing data then need to open up the dialog.
            mFragments = new ArrayList<CardFragment>();
            DialogFragment newCardDialog = new KeywordsEntryDialog();
            FragmentManager manager = this.getSupportFragmentManager();
            newCardDialog.show(manager, DIALOG_TAG);


        }


        //handle whether to go to 2-pane mode or not here
//        if (savedInstanceState == null) {
//            CardFragment cardFragment = new CardFragment();
//            if (cardUri != null) {
//                Bundle uriArgs = new Bundle();
//                uriArgs.putParcelable(CardFragment.CARD_URI, cardUri);
//                cardFragment.setArguments(uriArgs);
//            }
//            getSupportFragmentManager().beginTransaction()
//        }
//        }
//        CardFragment cardFragment = ((CardFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.stories_fragment));

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);

        mViewPager = (ViewPager) findViewById(R.id.main_pager);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        mPagerAdapter = new CardPagerAdapter(
                getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mPagerAdapter);


        //cycle through card titles (for tabs).

if (mTitlesAndKeywords != null ) {
    for (int i = 0; i < mTitlesAndKeywords.size(); i++) {
        Card card = mTitlesAndKeywords.get(i);
        String tabTitle = card.getTitle();
        mTabLayout.addTab(mTabLayout.newTab().setText(tabTitle));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.setContentDescription(tabTitle);
//        Bundle titlePosition = new Bundle();
//        titlePosition.putInt("cardPosition", i);
//        Log.v(LOG_TAG, "bundle: " + titlePosition);
//                cardFragment.onCreateLoader(0, titlePosition);
    }

}


        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });


        if (mFragments != null) {
            TracktiveSyncAdapter.initializeSyncAdapter(this);

            TracktiveSyncAdapter.syncImmediately(this);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onItemSelected(Uri contentUri) {
//        if(mTwoPane) {
//            Bundle args = new Bundle();
//
//            args.putParcelable("URI", contentUri);
//
//            DetailFragment fragment = new DetailFragment();
//            fragment.setArguments(args);
//
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
//                    .commit();
//        } else {
            Intent intent = new Intent(this, StoryDetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
//        }
    }




    public void addCard(ArrayList<Card> cardDetailsArrayList, Uri cardUri) {

        Bundle args = new Bundle();
        args.putParcelable("cardUri", cardUri);
        Log.v(LOG_TAG, "Card uri when added:" + cardUri);

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mViewPager = (ViewPager) findViewById(R.id.main_pager);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        int cardPosition = mTabLayout.getSelectedTabPosition();
        Log.v(LOG_TAG, "cardPosition" + cardPosition);
//        args.putInt("position", cardPosition + 1);

        mTitlesAndKeywords = cardDetailsArrayList;

//        if (mTitleArrayList == null) {
//            mTitleArrayList = new ArrayList<String>();
//        }
//
        int newCardPosition = mFragments.size();
        Log.v(LOG_TAG, "cardDetailsArrayList in AddCard is: " + mTitlesAndKeywords);

        Card card = mTitlesAndKeywords.get(newCardPosition);
        String cardKeywords = card.getKeywords();
        String tabTitle = card.getTitle();
        Log.v(LOG_TAG, "new card Position for " + tabTitle + " is " + newCardPosition);

        mPagerAdapter.notifyDataSetChanged();
        mTabLayout.addTab(mTabLayout.newTab().setText(tabTitle), true);
        mPagerAdapter.notifyDataSetChanged();

        CardFragment cardFragment = CardFragment.newInstance(tabTitle, cardKeywords);

        cardFragment.setArguments(args);

        mFragments.add(cardFragment);

        mPagerAdapter.notifyDataSetChanged();


        Toast.makeText(this, "Card made successfully!", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getCardFragments() {
        List<CardFragment> cardFragmentList = new ArrayList<CardFragment>();

        mTitlesAndKeywords = getExistingCardDetails(this);
        if (mTitlesAndKeywords != null) {
            Log.v(LOG_TAG, "cardDetailsArray contains: " + mTitlesAndKeywords);
            for (int i = 0; i < mTitlesAndKeywords.size(); i++) {
                Card card = mTitlesAndKeywords.get(i);
                String cardTitle = card.getTitle();
                //refactor into
                String cardKeywords = card.getKeywords();
                Log.v(LOG_TAG, "at position " + i + " cardTitle: " + cardTitle);
                // add cardFragment to list of fragments
                CardFragment cardFragment = CardFragment.newInstance(cardTitle, cardKeywords);
                cardFragmentList.add(cardFragment);

                mFragments = cardFragmentList;
            }
//            return cardFragmentList;
//        } else {
//            return null;
        }

    }

    public static ArrayList<Card> getExistingCardDetails(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String existingCardJson = sharedPreferences.getString(context.getString(R.string.pref_card_titles_key), null);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        //Convert JSON to Cards and add new card

        ArrayList<Card> mTitlesAndKeywords = (ArrayList<Card>) gson.fromJson(existingCardJson, new TypeToken<ArrayList<Card>>(){}.getType());
        return mTitlesAndKeywords;
    }

    public static Uri getcardUriFromFragmentPosition(int position, Context context) {

        ArrayList<Card> titlesAndKeywords = getExistingCardDetails(context);
        Card card = titlesAndKeywords.get(position);
        String keywords = card.getKeywords();
        Uri uriFromKeywords = CardsContract.CardEntry.buildSingleCardUri(keywords);
        Log.v("utility", "Uri From fragment in position " + position + " is " + uriFromKeywords);
        return uriFromKeywords;
    }


}