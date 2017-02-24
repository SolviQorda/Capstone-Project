package qorda_projects.tracktive.widget;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import qorda_projects.tracktive.R;
import qorda_projects.tracktive.data.CardsContract;

/**
 * Created by sorengoard on 23/02/2017.
 */

public class CardWidgetRemoteViewsService extends RemoteViewsService{
    public final String LOG_TAG = CardWidgetRemoteViewsService.class.getSimpleName().toString();

    private static final String[] STORY_COLUMNS = {
            CardsContract.CardEntry.TABLE_NAME + "." + CardsContract.CardEntry._ID,
            CardsContract.CardEntry.COLUMN_TITLE,
    };

    static final int COL_CARD_ID = 0;
    static final int COL_CARD_TITLE = 1;

    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;
            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if (data!=null){
                    data.close();
                }
                final long idtoken = Binder.clearCallingIdentity();

                data = getContentResolver().query(CardsContract.CardEntry.CONTENT_URI,
                        STORY_COLUMNS,
                        null,
                        null,
                        null);
                Binder.restoreCallingIdentity(idtoken);

            }

            @Override
            public void onDestroy() {
                if(data!=null){
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {

                if(position == AdapterView.INVALID_POSITION || data == null || !data.moveToPosition(position)) {
                    return null;
                }

                //loop through the stories and add them to the list - not restricted by card for this MVP
                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget_list_item);
                Log.v(LOG_TAG, "remoteViews status:" + remoteViews);
                String title = data.getString(data.getColumnIndex(CardsContract.CardEntry.COLUMN_TITLE));
                remoteViews.setTextViewText(R.id.widget_list_title_text, title);

                //intent for detail view
                int idInt = data.getInt(data.getColumnIndex(CardsContract.CardEntry._ID));
                Uri storyDetailUri = CardsContract.CardEntry.buildSingleStoryUri(idInt);
                Intent fillIntent = new Intent();
                fillIntent.setData(storyDetailUri);

                remoteViews.setOnClickFillInIntent(R.id.widget_list_item, fillIntent);

                return remoteViews;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.card_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if(data.moveToPosition(position)) {
                    return data.getInt(COL_CARD_ID);
                }
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
