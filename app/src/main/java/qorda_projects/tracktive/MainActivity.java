package qorda_projects.tracktive;

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

import java.util.ArrayList;
import java.util.Set;

import qorda_projects.tracktive.sync.TracktiveSyncAdapter;

public class MainActivity extends AppCompatActivity implements KeywordsEntryDialog.keywordsDialogListener{

    public final String DIALOG_TAG = "new card dialog";
    private final String LOG_TAG = MainActivity.class.getSimpleName().toString();
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    private SharedPreferences mSharedPreferences;
    private ArrayList<String> mTitleArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        Uri cardUri = getIntent() != null ? getIntent().getData() : null;

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
        CardFragment cardFragment = ((CardFragment)getSupportFragmentManager()
        .findFragmentById(R.id.stories_fragment));

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
//          TODO: implement a set for titles and in the onClick
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mViewPager = (ViewPager) findViewById(R.id.main_pager);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        Set<String> titleSet = mSharedPreferences.getStringSet(getResources().getString(R.string.pref_card_titles_key), null);

        //Cast Set to arrayList to iterate
        mTitleArrayList = new ArrayList<String>();

        if(titleSet == null) {
            //If no pre-existing data then need to open up the dialog.
            DialogFragment newCardDialog = new KeywordsEntryDialog();
            FragmentManager manager = this.getSupportFragmentManager();
            newCardDialog.show(manager, DIALOG_TAG);
        }



        //Hacked solution --. will need to refactor
        if (titleSet != null){
            mTitleArrayList.addAll(titleSet);
        } else {
            mTitleArrayList.add("");
        }


            //cycle through card titles.

            for (int i = 0; i < mTitleArrayList.size(); i++) {
                String tabTitle = mTitleArrayList.get(i).toString();
                mTabLayout.addTab(mTabLayout.newTab().setText(tabTitle));
                mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                mTabLayout.setContentDescription(tabTitle);
                Bundle titlePosition = new Bundle();
                titlePosition.putInt("cardPosition", i);
                Log.v(LOG_TAG, "bundle: " + titlePosition);
//                cardFragment.onCreateLoader(0, titlePosition);
            }

        mPagerAdapter = new CardPagerAdapter(
                getSupportFragmentManager(), mTabLayout.getTabCount());
        mViewPager.setAdapter(mPagerAdapter);


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

            TracktiveSyncAdapter.initializeSyncAdapter(this);

        if(titleSet != null){
            TracktiveSyncAdapter.syncImmediately(this);
        }

    }


    public void addCard(Uri cardUri) {

        Bundle args = new Bundle();
        args.putParcelable("URI", cardUri);
        Log.v(LOG_TAG, "Card uri when added:" + cardUri);

        CardFragment cardFragment = new CardFragment();

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mViewPager = (ViewPager) findViewById(R.id.main_pager);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        int cardPosition = mTabLayout.getSelectedTabPosition() + 1;
        mTitleArrayList.addAll(mSharedPreferences.getStringSet(getResources().getString(R.string.pref_card_titles_key), null));
        String tabTitle = mTitleArrayList.get(cardPosition);

        mTabLayout.addTab(mTabLayout.newTab().setText(tabTitle));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.stories_fragment, cardFragment).commit();


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
        if(id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    class ViewPagerAdapter extends FragmentPagerAdapter {
//
//        private final ArrayList<Fragment> fragmentArrayList = new ArrayList<Fragment>();
//        private final ArrayList<String> fragmentCardTitles = new ArrayList<String>();
//
//
//        public ViewPagerAdapter(FragmentManager fragmentManager) {
//            super(fragmentManager);
//
//            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences();
//
//
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//
//        }
//
//
//
//
//    }
}
