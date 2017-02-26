package qorda_projects.tracktive;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import qorda_projects.tracktive.sync.TracktiveSyncAdapter;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by sorengoard on 24/01/2017.
 */

public class KeywordsEntryDialog extends DialogFragment {

    //interface to communicate with host activity

    private final String LOG_TAG = KeywordsEntryDialog.class.getSimpleName().toString();

    public interface keywordsDialogListener {

        void initLoaderForNewCard();
    }

    public  EditText mCardTitle;
    public  EditText mKeywordOne;
    public  EditText mKeywordTwo;
    public  EditText mKeywordThree;


    // use this instance of interface to deliver action events
    private keywordsDialogListener mKeywordsDialogListener;

    //Override onAttach to instantiate the Listener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        //verify that host activity implements the callback interface
        try {
            //instantiate the listender so that we can send events to the host
            mKeywordsDialogListener = (keywordsDialogListener) activity;
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

                        //get keywords here.
                        ArrayList<String> keywordsList = new ArrayList<String>();

                        //TODO: think about handling edge cases here.

                        keywordsList.add(mKeywordOne.getText().toString());
                        keywordsList.add(mKeywordTwo.getText().toString());
                        keywordsList.add(mKeywordThree.getText().toString());

                        String keywordsFromUser = Utility.keywordsArrayToString(keywordsList);

                        //create a card object

                        Card newCardDetails = new Card(cardTitle, keywordsFromUser);
                        Log.v(LOG_TAG, "data from user input" + newCardDetails);

                        //SharedPrefs get any existing data

                        SharedPreferences settings = getDefaultSharedPreferences(getContext());

                        SharedPreferences.Editor settingsEditor = settings.edit();
                        String existingTitlesAndKeywordsJson = settings.getString(getString(R.string.pref_card_titles_key), null);
                        Log.v(LOG_TAG, "pre-existing json" + existingTitlesAndKeywordsJson);

                        // GSON builder

                        GsonBuilder builder = new GsonBuilder();
                        Gson gson = builder.create();

                        //Convert JSON to Cards and add new card

                        ArrayList<Card> titlesAndKeywords = new ArrayList<Card>();
                        if (existingTitlesAndKeywordsJson != null) {
                            titlesAndKeywords = (ArrayList<Card>) gson.fromJson(existingTitlesAndKeywordsJson, new TypeToken<ArrayList<Card>>() {}.getType());
                        }
                        titlesAndKeywords.add(newCardDetails);

                        //arraylist --> GSON

                        String titlesAndKeywordsJson = gson.toJson(titlesAndKeywords);
                        Log.v(LOG_TAG, "t&v json:" + titlesAndKeywordsJson);

                        settingsEditor.putString(getString(R.string.pref_card_titles_key), titlesAndKeywordsJson);

                        settingsEditor.commit();

                        //now you have new keywords you need to make an api call
                        TracktiveSyncAdapter.syncImmediately(getContext());


                        mKeywordsDialogListener.initLoaderForNewCard();

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
