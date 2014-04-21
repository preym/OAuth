package com.ehc.OAuth;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

  private static final String DATABASE_NAME = "oauth.db";
  private static final int DATABASE_VERSION = 1;

  private static final String DATABASE_CREATE = "CREATE TABLE user(" + "ID INTEGER  PRIMARY KEY," +
      "USERNAME  TEXT     NOT NULL," +
      "FIRSTNAME TEXT     NOT NULL," +
      "LASTNAME  TEXT     NOT NULL," +
      "EMAIL     TEXT     NOT NULL," +
      "ADDRESS   TEXT     NOT NULL," +
      "PASSWORD  TEXT     NOT NULL," +
      "PHONENUMBER INTEGER NOT NULL" + ")";

  public DatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase database) {
    database.execSQL(DATABASE_CREATE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.w(DatabaseHelper.class.getName(),
        "Upgrading database from version " + oldVersion + " to "
            + newVersion + ", which will destroy all old data");
    db.execSQL("DROP TABLE IF EXISTS " + "user");
    onCreate(db);
  }


}
