package com.example.lostandfound;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME    = "LostFoundDB";
    private static final int    DB_VERSION = 2;

    // ── Tables ──────────────────────────────────────────────────────────────
    private static final String TABLE_USERS = "users";
    private static final String TABLE_ITEMS = "items";

    // ── Users columns ────────────────────────────────────────────────────────
    public static final String COL_USER_ID       = "id";
    public static final String COL_USER_NAME     = "full_name";
    public static final String COL_USER_EMAIL    = "email";
    public static final String COL_USER_PASSWORD = "password";

    // ── Items columns ────────────────────────────────────────────────────────
    public static final String COL_ITEM_ID          = "id";
    public static final String COL_ITEM_TYPE        = "type";          // "lost" | "found"
    public static final String COL_ITEM_NAME        = "item_name";
    public static final String COL_ITEM_CATEGORY    = "category";
    public static final String COL_ITEM_PERSON      = "person_name";
    public static final String COL_ITEM_PHONE       = "phone";
    public static final String COL_ITEM_ADDRESS     = "address";
    public static final String COL_ITEM_DESCRIPTION = "description";
    public static final String COL_ITEM_IMAGE       = "image";
    public static final String COL_ITEM_DATE        = "date_reported";
    public static final String COL_ITEM_STATUS      = "status";        // "open" | "resolved"

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID       + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_NAME     + " TEXT, " +
                COL_USER_EMAIL    + " TEXT UNIQUE, " +
                COL_USER_PASSWORD + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_ITEMS + " (" +
                COL_ITEM_ID          + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ITEM_TYPE        + " TEXT DEFAULT 'lost', " +
                COL_ITEM_NAME        + " TEXT NOT NULL, " +
                COL_ITEM_CATEGORY    + " TEXT DEFAULT 'Other', " +
                COL_ITEM_PERSON      + " TEXT, " +
                COL_ITEM_PHONE       + " TEXT, " +
                COL_ITEM_ADDRESS     + " TEXT, " +
                COL_ITEM_DESCRIPTION + " TEXT, " +
                COL_ITEM_IMAGE       + " TEXT, " +
                COL_ITEM_DATE        + " TEXT, " +
                COL_ITEM_STATUS      + " TEXT DEFAULT 'open')");

        // Seed a demo user
        ContentValues demo = new ContentValues();
        demo.put(COL_USER_NAME,     "Demo User");
        demo.put(COL_USER_EMAIL,    "demo@test.com");
        demo.put(COL_USER_PASSWORD, "demo123");
        db.insert(TABLE_USERS, null, demo);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    // ── Auth ─────────────────────────────────────────────────────────────────

    public boolean registerUser(String fullName, String email, String password) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues v = new ContentValues();
            v.put(COL_USER_NAME,     fullName);
            v.put(COL_USER_EMAIL,    email);
            v.put(COL_USER_PASSWORD, password);
            long result = db.insertOrThrow(TABLE_USERS, null, v);
            return result != -1;
        } catch (Exception e) {
            return false; // duplicate email
        }
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT id FROM " + TABLE_USERS +
                " WHERE email=? AND password=?",
                new String[]{email, password});
        boolean found = c.getCount() > 0;
        c.close();
        return found;
    }

    public String getUserName(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT " + COL_USER_NAME + " FROM " + TABLE_USERS + " WHERE email=?",
                new String[]{email});
        String name = "User";
        if (c.moveToFirst()) name = c.getString(0);
        c.close();
        return name;
    }

    // ── Items ─────────────────────────────────────────────────────────────────

    public boolean addItem(String type, String itemName, String category,
                           String personName, String phone, String address,
                           String description, String imageUri, String date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_ITEM_TYPE,        type);
        v.put(COL_ITEM_NAME,        itemName);
        v.put(COL_ITEM_CATEGORY,    category);
        v.put(COL_ITEM_PERSON,      personName);
        v.put(COL_ITEM_PHONE,       phone);
        v.put(COL_ITEM_ADDRESS,     address);
        v.put(COL_ITEM_DESCRIPTION, description);
        v.put(COL_ITEM_IMAGE,       imageUri);
        v.put(COL_ITEM_DATE,        date);
        v.put(COL_ITEM_STATUS,      "open");
        return db.insert(TABLE_ITEMS, null, v) != -1;
    }

    public boolean deleteItem(int itemId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_ITEMS, COL_ITEM_ID + "=?",
                new String[]{String.valueOf(itemId)}) > 0;
    }

    public boolean markResolved(int itemId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_ITEM_STATUS, "resolved");
        return db.update(TABLE_ITEMS, v, COL_ITEM_ID + "=?",
                new String[]{String.valueOf(itemId)}) > 0;
    }

    /** Returns all items ordered by newest first */
    public Cursor getAllItems() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT id AS _id, type, item_name, category, person_name, " +
                "phone, address, description, image, date_reported, status " +
                "FROM items ORDER BY _id DESC", null);
    }

    /** Filter by type: "lost" | "found" | "all" */
    public Cursor getItemsByType(String type) {
        SQLiteDatabase db = getReadableDatabase();
        if (type.equals("all")) return getAllItems();
        return db.rawQuery(
                "SELECT id AS _id, type, item_name, category, person_name, " +
                "phone, address, description, image, date_reported, status " +
                "FROM items WHERE type=? ORDER BY _id DESC",
                new String[]{type});
    }

    /** Search by name or address */
    public Cursor searchItems(String query) {
        SQLiteDatabase db = getReadableDatabase();
        String like = "%" + query + "%";
        return db.rawQuery(
                "SELECT id AS _id, type, item_name, category, person_name, " +
                "phone, address, description, image, date_reported, status " +
                "FROM items WHERE item_name LIKE ? OR address LIKE ? ORDER BY _id DESC",
                new String[]{like, like});
    }

    public int getTotalCount()   { return getCount("SELECT COUNT(*) FROM items"); }
    public int getLostCount()    { return getCount("SELECT COUNT(*) FROM items WHERE type='lost'"); }
    public int getFoundCount()   { return getCount("SELECT COUNT(*) FROM items WHERE type='found'"); }

    private int getCount(String query) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }
}
