package com.example.testchatfragment;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "put.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_NAME = "student";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_NUMBER = "number";
    public static final String COLUMN_MAJOR = "major";

    public static final String TABLE_STORY = "stories";
    public static final String COLUMN_STORY_ID = "id";
    public static final String COLUMN_IMAGE = "image"; // Tipe data sebagai String untuk menyimpan path gambar
    public static final String COLUMN_DESCRIPTION = "description";

    // SQL query to create student table
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_USERNAME + " TEXT, " +
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_NUMBER + " TEXT, " +
                    COLUMN_MAJOR + " TEXT);";

    // SQL query to create stories table
    private static final String TABLE_STORY_CREATE =
            "CREATE TABLE " + TABLE_STORY + " (" +
                    COLUMN_STORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_IMAGE + " TEXT, " + // Menyimpan path gambar
                    COLUMN_DESCRIPTION + " TEXT);";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_STORY_CREATE);

        // Insert default data into the student table
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if they exis
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STORY);
        // Recreate tables
        onCreate(db);
    }

    // Method to save a story
    public void addStory(String image, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IMAGE, image); // Store the image path
        values.put(COLUMN_DESCRIPTION, description);
        db.insert(TABLE_STORY, null, values);
        db.close();
    }

    // Method to get all stories
    @SuppressLint("Range")
    public List<Story> getAllStories() {
        List<Story> storyList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_STORY, null);

        if (cursor.moveToFirst()) {
            do {
                Story story = new Story();
                story.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_STORY_ID)));
                story.setImage(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE))); // Get the image path
                story.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                storyList.add(story);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return storyList;
    }
}
