package qorda_projects.tracktive;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import qorda_projects.tracktive.data.CardsContract;
import qorda_projects.tracktive.sync.TracktiveSyncAdapter;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by sorengoard on 24/01/2017.
 */

public class KeywordsEntryDialog extends DialogFragment {

    //interface to communicate with host activity

    private final String LOG_TAG = KeywordsEntryDialog.class.getSimpleName().toString();

    public interface keywordsDialogListener {

        public void addCard(ArrayList<String> titlesArrayList, ArrayList<String> keywordsArrayList, Uri cardUri);

    }

    public  EditText mCardTitle;
    public  EditText mKeywordOne;
    public  EditText mKeywordTwo;
    public  EditText mKeywordThree;


    // use this instance of interface to deliver action events
    keywordsDialogListener mListener;

    //Override onAttach to instantiate the Listener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        //verify that host activity implements the callback interface
        try {
            //instantiate the listender so that we can send events to the host
            mListener = (keywordsDialogListener) activity;
        } catch (ClassCastException e) {
            // if this happens the activity doesn't implement the interface so throw an exception
            throw new ClassCastException(activity.toString() + " must implement keywordsDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();


        builder.setView(inflater.inflate(R.layout.dialog_keywordentry, null))
                .setPositiveButton(R.string.make_card, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Dialog dialogBox = (Dialog) dialogInterface;
                        mCardTitle = (EditText) dialogBox.findViewById(R.id.dialog_name_card);
                        mKeywordOne = (EditText) dialogBox.findViewById(R.id.dialog_keyword_1);
                        mKeywordTwo = (EditText) dialogBox.findViewById(R.id.dialog_keyword_2);
                        mKeywordThree = (EditText) dialogBox.findViewById(R.id.dialog_keyword_3);


                        //get cardTitle string
                        String cardTitle = mCardTitle.getText().toString();

                        SharedPreferences settings = getDefaultSharedPreferences(getContext());

                        SharedPreferences.Editor settingsEditor = settings.edit();
                        Set<String> titlesSetOut = settings.getStringSet(getString(R.string.pref_card_titles_key), new LinkedHashSet<String>());
                        LinkedHashSet<String> titlesSetIn = new LinkedHashSet<String>(titlesSetOut);
                        Log.v(LOG_TAG, "card titles before editing" + titlesSetOut);

                        titlesSetIn.add(cardTitle);
                        settingsEditor.remove(getString(R.string.pref_card_titles_key));
//                        settingsEditor.commit();


//                        settingsEditor.commit();

                        //get keywords here.
                        ArrayList<String> keywordsList = new ArrayList<String>();

                        //TODO: think about handling edge cases here.

                        keywordsList.add(mKeywordOne.getText().toString());
                        keywordsList.add(mKeywordTwo.getText().toString());
                        keywordsList.add(mKeywordThree.getText().toString());

                        String keywordsFromUser = Utility.keywordsArrayToString(keywordsList);

                        Set<String> keywordsSetOut = settings.getStringSet(getString(R.string.pref_keywords_key), new LinkedHashSet<String>());
                        LinkedHashSet<String> keywordsSetIn = new LinkedHashSet<String>(keywordsSetOut);
                        Log.v(LOG_TAG, "keywords before editing" + keywordsSetOut);
                        keywordsSetIn.add(keywordsFromUser);
                        settingsEditor.remove(getString(R.string.pref_keywords_key));

                        settingsEditor.putStringSet(getString(R.string.pref_card_titles_key), titlesSetIn);
                        Log.v(LOG_TAG, "card titles after editing" + titlesSetIn);

                        settingsEditor.putStringSet(getString(R.string.pref_keywords_key), keywordsSetIn);
                        settingsEditor.commit();

//                        settingsEditor.apply();
                        Log.v(LOG_TAG, "keywords after editing" + keywordsSetIn);

                        //now you have new keywords you need to make an api call
                        TracktiveSyncAdapter.syncImmediately(getContext());

                        int cardPosition = keywordsSetIn.size() - 1;
                        Log.v(LOG_TAG, "new card dialog count" + cardPosition);
                        Log.v(LOG_TAG, "string taken from keywods in dialog: " + keywordsFromUser);

                        Uri cardForKeywordUri = CardsContract.CardEntry.buildSingleCardUri(keywordsFromUser);
                        Log.v(LOG_TAG, "cards Uri:" + cardForKeywordUri);

                        ArrayList<String> titlesArrayList = new ArrayList<String>();
                        ArrayList<String> keywordsArrayList = new ArrayList<String>();
                        titlesArrayList.addAll(titlesSetIn);
                        keywordsArrayList.addAll(keywordsSetIn);

                        mListener.addCard(titlesArrayList, keywordsArrayList, cardForKeywordUri);

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        return builder.create();
    }
}
