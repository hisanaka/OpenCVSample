package jp.or.ixqsware.opencvsample.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import jp.or.ixqsware.opencvsample.GestureInformation;
import jp.or.ixqsware.opencvsample.LevenshteinDistance;

import static android.provider.BaseColumns._ID;
import static jp.or.ixqsware.opencvsample.db.Constants.COLUMN_HASH;
import static jp.or.ixqsware.opencvsample.db.Constants.COLUMN_POINTS;
import static jp.or.ixqsware.opencvsample.db.Constants.DATABASE_NAME;
import static jp.or.ixqsware.opencvsample.db.Constants.DATABASE_VERSION;
import static jp.or.ixqsware.opencvsample.db.Constants.SQL_CREATE;
import static jp.or.ixqsware.opencvsample.db.Constants.SQL_DELETE;
import static jp.or.ixqsware.opencvsample.db.Constants.SQL_GET_ALL;
import static jp.or.ixqsware.opencvsample.db.Constants.SQL_REGISTER;
import static jp.or.ixqsware.opencvsample.db.Constants.SQL_REMOVE;
import static jp.or.ixqsware.opencvsample.db.Constants.SQL_SELECT_BY_ID;

/**
 * Created by hnakadate on 15/04/16.
 */
public class RingDatabaseHelper extends SQLiteOpenHelper {
    private static RingDatabaseHelper instance;

    private RingDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized RingDatabaseHelper getInstance(Context context) {
        if (instance == null) instance = new RingDatabaseHelper(context);
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createEmptyDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
    }

    private void createEmptyDatabase(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE);
        db.execSQL(SQL_CREATE);
    }

    public long registerGesture(SQLiteDatabase db, String hash, String points) {
        db.beginTransaction();
        long id = -1;
        try {
            SQLiteStatement statement = db.compileStatement(SQL_REGISTER);
            statement.bindString(1, hash);
            statement.bindString(2, points);
            id = statement.executeInsert();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return id;
    }

    public ArrayList<GestureInformation> getGestures(SQLiteDatabase db) {
        Cursor mCursor = db.rawQuery(SQL_GET_ALL, null);
        ArrayList<GestureInformation> arrGestures = new ArrayList<>();
        if (mCursor.moveToFirst()) {
            int colId = mCursor.getColumnIndex(_ID);
            int colHash = mCursor.getColumnIndex(COLUMN_HASH);
            int colPoints = mCursor.getColumnIndex(COLUMN_POINTS);
            do {
                GestureInformation gesture = new GestureInformation();
                gesture.setId(mCursor.getInt(colId));
                gesture.setHash(mCursor.getString(colHash));
                gesture.setPoints(mCursor.getString(colPoints));
                gesture.setDifferenceRate(0.0f);
                arrGestures.add(gesture);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        Collections.sort(arrGestures, new GestureInfoComparator());
        return arrGestures;
    }

    public ArrayList<GestureInformation> getAllGestures(SQLiteDatabase db, String mHash, int width, int height) {
        Cursor mCursor = db.rawQuery(SQL_GET_ALL, null);
        ArrayList<GestureInformation> arrGestures = new ArrayList<>();
        LevenshteinDistance ld = new LevenshteinDistance();
        if (mCursor.moveToFirst()) {
            int colId = mCursor.getColumnIndex(_ID);
            int colHash = mCursor.getColumnIndex(COLUMN_HASH);
            int colPoints = mCursor.getColumnIndex(COLUMN_POINTS);
            do {
                GestureInformation gesture = new GestureInformation();
                gesture.setId(mCursor.getInt(colId));
                gesture.setHash(mCursor.getString(colHash));
                gesture.setPoints(mCursor.getString(colPoints));
                gesture.setBitmap(mCursor.getString(colPoints), width, height);

                int distance = ld.calculateDistance(mHash, mCursor.getString(colHash));
                float rate = (float) distance * 100 / (float) mHash.length();
                gesture.setDifferenceRate(rate);

                arrGestures.add(gesture);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        Collections.sort(arrGestures, new GestureInfoComparator());
        return arrGestures;
    }

    public boolean removeGesture(SQLiteDatabase db, long id) {
        boolean result = true;
        db.beginTransaction();
        try {
            SQLiteStatement statement = db.compileStatement(SQL_REMOVE);
            statement.bindString(1, Long.toString(id));
            statement.executeUpdateDelete();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            result = false;
        } finally {
            db.endTransaction();
        }
        return result;
    }

    public class GestureInfoComparator implements Comparator<GestureInformation> {
        @Override
        public int compare(GestureInformation lhs, GestureInformation rhs) {
            return lhs.getDifferenceRate() < rhs.getDifferenceRate() ? -1 : 0;
        }
    }
}
