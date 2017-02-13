package qorda_projects.tracktive;

/**
 * Created by sorengoard on 09/02/2017.
 */

public class Card {

    private String mTitle;
    private String mKeywords;


    public Card(String title, String keywords) {
        mTitle = title;
        mKeywords = keywords;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getKeywords() {
        return mKeywords;
    }

    public void setKeywords(String keywords) {
        mKeywords = keywords;
    }
}
