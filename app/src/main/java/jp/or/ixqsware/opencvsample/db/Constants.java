package jp.or.ixqsware.opencvsample.db;

import static android.provider.BaseColumns._ID;

/**
 * Created by hnakadate on 15/04/16.
 */
public class Constants {
    public static final String DATABASE_NAME = "gestures";
    public static final int DATABASE_VERSION = 1;

    public static final String COLUMN_HASH = "hash";
    public static final String COLUMN_POINTS = "points";

    public static final String SQL_DELETE = "DROP TABLE IF EXISTS " + DATABASE_NAME + ";";

    public static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS " + DATABASE_NAME
            + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_HASH + " TEXT, "
            + COLUMN_POINTS + " TEXT);";

    public static final String SQL_REGISTER = "INSERT INTO " + DATABASE_NAME
            + " (" + COLUMN_HASH + ", " + COLUMN_POINTS + ")"
            + " values (?, ?);";

    public static final String SQL_REMOVE = "DELETE FROM " + DATABASE_NAME
            + " WHERE " + _ID + " = ?;";

    public static final String SQL_GET_ALL = "SELECT * FROM " + DATABASE_NAME + ";";

    public static final String SQL_SELECT_BY_ID = "SELECT * FROM " + DATABASE_NAME
            + " WHERE " + _ID + " = ?;";
}
