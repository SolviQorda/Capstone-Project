package qorda_projects.tracktive;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sorengoard on 10/01/2017.
 */

public class Story implements Parcelable {

    private String mTitle;
    private String mContent;
    private String mDate;
    private String mSource;
    private String mUrl;
    private String mBookmarked;
    private String mKeywords;
    private int mTabNumber;
    private int mDbId;


    public Story(String title, String content, String date, String source, String url, String bookmarked, String keywords, int tabNumber, int dbId) {
        mTitle = title;
        mContent = content;
        mDate = date;
        mSource = source;
        mUrl = url;
        mBookmarked = bookmarked;
        mKeywords = keywords;
        mTabNumber = tabNumber;
        mDbId = dbId;
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

    public String getBookmarked() {
        return mBookmarked;
    }

    public void setBookmarked(String bookmarked){
        this.mBookmarked = bookmarked;
    }

    public String getKeywords() {return mKeywords;}

    public void setKeywords(String keywords) {this.mKeywords = keywords;}

    public int getTabNumber() {return mTabNumber;}

    public void setTabNumber(int tabNumber) {this.mTabNumber = tabNumber;}

    public int getDbId() {
        return mDbId;
    }

    public void setDbId(int dbId) {
        mDbId = dbId;
    }


    //make this parcelable.

    protected Story(Parcel in) {

        mTitle = in.readString();
        mContent = in.readString();
        mDate = in.readString();
        mSource = in.readString();
        mUrl = in.readString();
        mBookmarked = in.readString();
        mKeywords = in.readString();
        mTabNumber = in.readInt();
        mDbId = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mContent);
        dest.writeString(mDate);
        dest.writeString(mSource);
        dest.writeString(mUrl);
        dest.writeString(mBookmarked);
        dest.writeString(mKeywords);
        dest.writeInt(mTabNumber);
        dest.writeInt(mDbId);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Story> CREATOR = new Parcelable.Creator<Story>() {
        @Override
        public Story createFromParcel(Parcel in) {
            return new Story(in);
        }

        @Override
        public Story[] newArray(int size) {
            return new Story[size];
        }
    };
}
