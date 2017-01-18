package qorda_projects.tracktive;

/**
 * Created by sorengoard on 11/01/2017.
 */

public class DiaryEntry {

    private String mTitle;
    private String mDate;
    private String mContent;
    private int mCardNumber;
    private String[] mKeywords;

    public DiaryEntry(String title, String date, String content, int cardNumber, String[] keywords){
        mTitle = title;
        mDate = date;
        mContent = content;
        mCardNumber = cardNumber;
        mKeywords = keywords;
    }

    //gettrs and setters

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public String getContent() {
        return  mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public int getCardNumber() {
        return mCardNumber;
    }

    public void setCardNumber(int cardNumber) {
        this.mCardNumber = cardNumber;
    }

    public String[] getKeywords() {return mKeywords;}

    public void setKeywords(String[] keywords) {this.mKeywords = keywords;}



}
