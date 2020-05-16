package com.androidcorpo.lindapp.resources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.androidcorpo.lindapp.model.MyKey;

public class LindAppDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "lindapp.db";
    private static final int DATABASE_VERSION = 1;
    private static LindAppDbHelper sInstance;


    public LindAppDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized LindAppDbHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new LindAppDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_KEYS_TABLE = "CREATE TABLE " + KeysEntry.TABLE_NAME + " (" +
                KeysEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KeysEntry.COLUMN_KEY_CONTACT + " TEXT NOT NULL, " +
                KeysEntry.COLUMN_KEY_PUBLIC + " BLOB NOT NULL, " +
                KeysEntry.COLUMN_KEY_PRIVATE + " BLOB, " +
                KeysEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";

        db.execSQL(SQL_CREATE_KEYS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + KeysEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public static final class KeysEntry implements BaseColumns {

        public final static String TABLE_NAME = "keys";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_KEY_CONTACT = "contact";
        public final static String COLUMN_KEY_PUBLIC = "public_key";
        public final static String COLUMN_KEY_PRIVATE = "private_key";
        public final static String COLUMN_TIMESTAMP = "timestamp";
    }

    public void saveKey(MyKey dto) {

        MyKey saveKey = findByContact(dto.getContact());
        if (saveKey != null) {
            updateKey(saveKey, dto);
        } else {
            createKey(dto);
        }
    }

    private void createKey(MyKey key) {


        SQLiteDatabase db = sInstance.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(KeysEntry.COLUMN_KEY_PUBLIC, key.getPublicKey());
        cv.put(KeysEntry.COLUMN_KEY_PRIVATE, key.getPrivateKey());
        cv.put(KeysEntry.COLUMN_KEY_CONTACT, key.getContact());

        db.beginTransaction();
        try {
            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            long id = db.insertOrThrow(KeysEntry.TABLE_NAME, null, cv);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("Keys ENTRY", "Error while trying to add key to database");
        } finally {
            db.endTransaction();
        }
    }

    private void updateKey(MyKey myKey, MyKey dto) {

        SQLiteDatabase db = sInstance.getWritableDatabase();
        long ID = -1;

        ContentValues cv = new ContentValues();
        cv.put(KeysEntry.COLUMN_KEY_PUBLIC, dto.getPublicKey());
        cv.put(KeysEntry.COLUMN_KEY_PRIVATE, dto.getPublicKey());

        db.beginTransaction();
        try {
            // This assumes userNames are unique
            int rows = db.update(KeysEntry.TABLE_NAME, cv, KeysEntry._ID + " = " + myKey.getID(), null);

        } catch (Exception e) {
            Log.d("User Entry ", "Error while trying to update user");
        } finally {
            db.endTransaction();
        }

    }

    public MyKey findByContact(String contact) {

        SQLiteDatabase db = getReadableDatabase();

        MyKey myKey = null;

        db.beginTransaction();
        String usersSelectQuery = String.format("SELECT * FROM %s WHERE %s = ?",
                KeysEntry.TABLE_NAME, KeysEntry.COLUMN_KEY_CONTACT);
        Cursor cursor = db.rawQuery(usersSelectQuery, new String[]{String.valueOf(contact)});
        try {
            if (cursor.moveToFirst()) {
                myKey = new MyKey();
                myKey.setID(cursor.getLong(cursor.getColumnIndex(KeysEntry._ID)));
                myKey.setContact(cursor.getString(cursor.getColumnIndex(KeysEntry.COLUMN_KEY_CONTACT)));
                myKey.setPrivateKey(cursor.getString(cursor.getColumnIndex(KeysEntry.COLUMN_KEY_PRIVATE)));
                myKey.setPublicKey(cursor.getString(cursor.getColumnIndex(KeysEntry.COLUMN_KEY_PUBLIC)));
                db.setTransactionSuccessful();
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.endTransaction();
        }

        return myKey;
    }

}