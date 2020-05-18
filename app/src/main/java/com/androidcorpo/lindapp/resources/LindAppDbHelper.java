package com.androidcorpo.lindapp.resources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.androidcorpo.lindapp.model.MyKey;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;

import static com.androidcorpo.lindapp.LindAppUtils.deSerializePrivateKey;
import static com.androidcorpo.lindapp.LindAppUtils.deSerializePublicKey;
import static com.androidcorpo.lindapp.LindAppUtils.privateKeyToStream;
import static com.androidcorpo.lindapp.LindAppUtils.publicKeyToStream;

public class LindAppDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "lindapp.db";
    private static final int DATABASE_VERSION = 3;
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

    public PublicKey getPublicKey(String cleanAdress) throws IOException {
        MyKey byContact = findByContact(cleanAdress);
        if (byContact != null)
            return byContact.getPublicKey();
        else
            return null;
    }

    public PrivateKey getPrivateKey(String cleanAdress) throws IOException {
        MyKey byContact = findByContact(cleanAdress);
        return byContact.getPrivateKey();
    }

    private static final class KeysEntry implements BaseColumns {

        private final static String TABLE_NAME = "keys";
        private final static String _ID = BaseColumns._ID;
        private final static String COLUMN_KEY_CONTACT = "contact";
        private final static String COLUMN_KEY_PUBLIC = "public_key";
        private final static String COLUMN_KEY_PRIVATE = "private_key";
        private final static String COLUMN_TIMESTAMP = "timestamp";
    }

    public void saveKey(MyKey dto) throws IOException {
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
        cv.put(KeysEntry.COLUMN_KEY_CONTACT, key.getContact());
        byte[] serialPuK = publicKeyToStream(key.getPublicKey());
        cv.put(KeysEntry.COLUMN_KEY_PUBLIC, serialPuK);
        byte[] serialPrK = privateKeyToStream(key.getPrivateKey());
        cv.put(KeysEntry.COLUMN_KEY_PRIVATE, serialPrK);

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

        byte[] serialPuK = publicKeyToStream(dto.getPublicKey());
        cv.put(KeysEntry.COLUMN_KEY_PUBLIC, serialPuK);
        byte[] serialPrK = privateKeyToStream(dto.getPrivateKey());
        cv.put(KeysEntry.COLUMN_KEY_PRIVATE, serialPrK);

        db.beginTransaction();
        try {
            int rows = db.update(KeysEntry.TABLE_NAME, cv, KeysEntry._ID + " = " + myKey.getID(), null);

        } catch (Exception e) {
            Log.d("Key Entry ", "Error while trying to update key");
        } finally {
            db.endTransaction();
        }

    }

    public MyKey findByContact(String contact) throws IOException {

        SQLiteDatabase db = getReadableDatabase();

        MyKey myKey = null;

        db.beginTransaction();
        String keysSelectQuery = String.format("SELECT * FROM %s WHERE %s = ?",
                KeysEntry.TABLE_NAME, KeysEntry.COLUMN_KEY_CONTACT);
        Cursor cursor = db.rawQuery(keysSelectQuery, new String[]{String.valueOf(contact)});
        try {
            if (cursor.moveToFirst()) {
                myKey = new MyKey();
                myKey.setID(cursor.getLong(cursor.getColumnIndex(KeysEntry._ID)));
                myKey.setContact(cursor.getString(cursor.getColumnIndex(KeysEntry.COLUMN_KEY_CONTACT)));
                byte[] blob = cursor.getBlob(cursor.getColumnIndex(KeysEntry.COLUMN_KEY_PRIVATE));
                myKey.setPrivateKey((deSerializePrivateKey(blob)));
                byte[] publicKey = cursor.getBlob(cursor.getColumnIndex(KeysEntry.COLUMN_KEY_PUBLIC));
                myKey.setPublicKey(deSerializePublicKey(publicKey));
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
