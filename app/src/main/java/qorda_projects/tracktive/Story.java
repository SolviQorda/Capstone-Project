package qorda_projects.tracktive;

/**
 * Created by sorengoard on 10/01/2017.
 */

public class Story  {

    private String mTitle;
    private String mContent;
    private String mDate;
    private String mSource;
    private String mUrl;
    private int mCardInt;
    private int mBookmarked;
    private String[] mKeywords;

    public Story(String title, String content, String date, String source, String url, int cardInt, int bookmarked, String[] keywords) {
        mTitle = title;
        mContent = content;
        mDate = date;
        mSource = source;
        mUrl = url;
        mCardInt = cardInt;
        mBookmarked = bookmarked;
        mKeywords = keywords;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title){
        this.mTitle = title;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content){
        this.mContent = content;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date){
        this.mDate = date;
    }

    public String getSource() {
        return mSource;
    }

    public void setSource(String source){
        this.mSource = source;
    }

    public String getUrl() {return mUrl;}

    public void setUrl(String url) {this.mUrl = url;}

    public int getCardInt() {
        return mCardInt;
    }

    public void setCardInt(int cardInt) {
        this.mCardInt = cardInt;
    }

    public int getBookmarked() {
        return mBookmarked;
    }

    public void setBookmarked(int bookmarked){
        this.mBookmarked = bookmarked;
    }

    public String[] getKeywords() {return mKeywords;}

    public void setKeywords(String[] keywords) {this.mKeywords = keywords;}
}
