package net.easynaps.easyfiles.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.easynaps.easyfiles.database.models.Tab;

import java.util.ArrayList;
import java.util.List;

public class TabHandler extends SQLiteOpenHelper {

    protected static final int DATABASE_VERSION = 5;
    protected static final String DATABASE_NAME = "explorer.db";
    protected static final String TABLE_TAB = "tab";

    protected static final String COLUMN_TAB_NO = "tab_no";
    protected static final String COLUMN_LABEL = "label";
    protected static final String COLUMN_PATH = "path";
    protected static final String COLUMN_HOME = "home";

    protected static final String TABLE_ENCRYPTED = "encrypted";

    protected static final String COLUMN_ENCRYPTED_ID = "_id";
    protected static final String COLUMN_ENCRYPTED_PATH = "path";
    protected static final String COLUMN_ENCRYPTED_PASSWORD = "password";

    private Context context;

    public TabHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TAB_TABLE = "CREATE TABLE " + TABLE_TAB + "("
                + COLUMN_TAB_NO + " INTEGER PRIMARY KEY,"
                + COLUMN_PATH + " TEXT,"
                + COLUMN_HOME + " TEXT" +
                ")";

        String CREATE_TABLE_ENCRYPTED = "CREATE TABLE " + TABLE_ENCRYPTED + "("
                + COLUMN_ENCRYPTED_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_ENCRYPTED_PATH + " TEXT,"
                + COLUMN_ENCRYPTED_PASSWORD + " TEXT"
                + ")";

        String CREATE_TABLE_CLOUD = "CREATE TABLE " + CloudHandler.TABLE_CLOUD_PERSIST + "("
                + CloudHandler.COLUMN_CLOUD_ID
                + " INTEGER PRIMARY KEY,"
                + CloudHandler.COLUMN_CLOUD_SERVICE + " INTEGER,"
                + CloudHandler.COLUMN_CLOUD_PERSIST + " TEXT" + ")";

        db.execSQL(CREATE_TAB_TABLE);
        db.execSQL(CREATE_TABLE_ENCRYPTED);
        db.execSQL(CREATE_TABLE_CLOUD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TAB);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ENCRYPTED);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CloudHandler.TABLE_CLOUD_PERSIST);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addTab(Tab tab) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TAB_NO, tab.tabNumber);
        contentValues.put(COLUMN_PATH, tab.path);
        contentValues.put(COLUMN_HOME, tab.home);
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.insert(TABLE_TAB, null, contentValues);
    }

    public void clear() {
        try {
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();
            sqLiteDatabase.delete(TABLE_TAB, COLUMN_TAB_NO + " = ?", new String[]{"" + 1});
            sqLiteDatabase.delete(TABLE_TAB, COLUMN_TAB_NO + " = ?", new String[]{"" + 2});
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public Tab findTab(int tabNo) {
        String query = "Select * FROM " + TABLE_TAB + " WHERE " + COLUMN_TAB_NO + "= \"" + tabNo + "\"";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        Tab tab;
        if (cursor.moveToFirst()) {
            tab = new Tab(cursor.getInt(0), cursor.getString(1),
                    cursor.getString(2));
            cursor.close();
        } else {
            tab = null;
        }
        return tab;
    }

    public List<Tab> getAllTabs() {

        List<Tab> tabList = new ArrayList<>();
        // Select all query
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(TABLE_TAB, null, null, null, null, null, null);
        boolean hasNext = cursor.moveToFirst();

        // Looping through all rows and adding them to list
        while (hasNext) {
            Tab tab = new Tab(cursor.getInt(0), cursor.getString(1),
                    cursor.getString(2));
            //Adding them to list
            tabList.add(tab);
            hasNext = cursor.moveToNext();
        }
        cursor.close();

        return tabList;
    }
}
