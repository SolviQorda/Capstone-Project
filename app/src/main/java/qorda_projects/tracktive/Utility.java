package qorda_projects.tracktive;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by sorengoard on 12/01/2017.
 */

public class Utility {

    public static ArrayList<String> keywordStringToArray(String keywordString) {
        ArrayList<String> keywordsList = new ArrayList<String>();

        for(int i = 0; i <=keywordString.length();i++) {
            if(keywordString.substring(i).equals(",")) {
                String word = keywordString.substring((i-i), (i-1));
                keywordsList.add(word);
            }
        }

        return keywordsList;
    }

    public static String keywordsArrayToString(ArrayList<String> keywordsList) {
        String keywordsString = "";
        for (int i = 0; i <keywordsList.size(); i++) {
            keywordsString += keywordsList.get(i) + ",";
        }
        return keywordsString;
    }

    public static int bookmarkedOrNot(int bookmarked){
        int iconResource = 0;
        if (bookmarked == 1) {
            return R.drawable.ic_bookmark_green;
        } else {
            return R.drawable.ic_bookmark_black_48dp;
        }

    }

    public static String getKeywordsFromElementNumber(int elementNumber, Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> keywordsSet = sharedPrefs.getStringSet(context.getString(R.string.pref_keywords_key), null);
        ArrayList<String> keywordsArray = new ArrayList<String>();
        keywordsArray.addAll(keywordsSet);
        String keywordsNeeded = keywordsArray.get(elementNumber);
        return keywordsNeeded;

    }
}
