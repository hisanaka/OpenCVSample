package jp.or.ixqsware.opencvsample.asynctask;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import jp.or.ixqsware.opencvsample.GestureInformation;
import jp.or.ixqsware.opencvsample.db.RingDatabaseHelper;

/**
 * Created by hnakadate on 15/04/19.
 */
public class GetAllGesturesTask extends AsyncTaskLoader<ArrayList<GestureInformation>> {
    private Context context;
    private SQLiteDatabase db = null;

    public GetAllGesturesTask(Context context_) {
        super(context_);
        this.context = context_;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
        super.onStartLoading();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
        super.onStopLoading();
    }

    @Override
    protected void onReset() {
        cancelLoad();
        super.onReset();
    }

    @Override
    public ArrayList<GestureInformation> loadInBackground() {
        RingDatabaseHelper helper = RingDatabaseHelper.getInstance(this.context);
        if (db == null || !db.isOpen()) db = helper.getWritableDatabase();
        ArrayList<GestureInformation> arrGestures = helper.getGestures(db);
        return arrGestures;
    }
}
