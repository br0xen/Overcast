package com.br0xen.overcast;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static List<Feed> all_feeds = new ArrayList<Feed>();
    public static Map<String, Feed> feed_map = new HashMap<String, Feed>();

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "howell_feeds";
	private static final String TABLE_FEEDS = "feeds";
	private static final String FEEDS_KEY_ID = "feed_id";
	private static final String FEEDS_KEY_URL = "feed_url";

	private static final int DB_MODE_CLOSED = 0;
	private static final int DB_MODE_READ = 1;
//	private static final int DB_MODE_WRITE = 2;

	private SQLiteDatabase sql_db;
	private int db_mode = 0;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		sql_db = this.getReadableDatabase();
		db_mode = DB_MODE_READ;
		all_feeds = new ArrayList<Feed>();
		loadAllFeeds();
		sql_db.close();
		db_mode = DB_MODE_CLOSED;;	
	}

	public void loadAllFeeds() {
		all_feeds = new ArrayList<Feed>();
		String selectQuery = "SELECT * FROM "+TABLE_FEEDS;
		if(db_mode == DB_MODE_CLOSED) { 
			// Is the database already opened? If not, open it.
			sql_db = this.getReadableDatabase(); 
		}
		Cursor cursor = sql_db.rawQuery(selectQuery, null);
		// Looping through all rows and adding to list
		if(cursor.moveToFirst()) {
			do {
				Feed f = new Feed(cursor.getString(1));
				addFeed(f);
			} while(cursor.moveToNext());
		}
		if(db_mode == DB_MODE_CLOSED) {
			// If the database was closed, reclose it.
			sql_db.close();
		}
	}

	public void updateAllFeeds() {
		for(Feed f : this.all_feeds) {
			f.updateFeed();
		}
	}

    private void addFeed(Feed a_feed) {
        all_feeds.add(a_feed);
        feed_map.put(Integer.toString(a_feed.id), a_feed);
    }

	public void addNewFeed(String url) {
		// First, Check if this is a valid URL
		// Quick check for protocol (http or https)
		if(!url.substring(0,4).equals("http")) {
			url="http://"+url;
		}
		try {
			URL u = new URL(url);
			url = u.toURI().toString();
		} catch(Exception e) {
			// Invalid URL, do not add.
			return; 
		}
		if(!feedIsInDB(url)) {
			if(db_mode == DB_MODE_READ) {
				sql_db.close();
				sql_db = this.getWritableDatabase();
			}
			if(db_mode == DB_MODE_CLOSED) { sql_db = this.getWritableDatabase(); }

			
			ContentValues values = new ContentValues();
			values.put(FEEDS_KEY_URL, url);
			sql_db.insert(TABLE_FEEDS, null, values);

			if(db_mode == DB_MODE_CLOSED) { sql_db.close(); }
			if(db_mode == DB_MODE_READ) {
				sql_db.close();
				sql_db = this.getReadableDatabase();
			}
		}
	}

	public void updateFeed(String url, String newurl) {
		if(db_mode == DB_MODE_READ) {
			sql_db.close();
			sql_db = this.getWritableDatabase();
		}
		if(db_mode == DB_MODE_CLOSED) { sql_db = this.getWritableDatabase(); }

		ContentValues values = new ContentValues();
		values.put(FEEDS_KEY_URL, newurl);
		sql_db.update(TABLE_FEEDS, values, FEEDS_KEY_URL+" = ?", new String[] { url });

		if(db_mode == DB_MODE_CLOSED) { sql_db.close(); }
		if(db_mode == DB_MODE_READ) {
			sql_db.close();
			sql_db = this.getReadableDatabase();
		}
	}

	public void deleteFeed(String url) {
		if(db_mode == DB_MODE_READ) {
			sql_db.close();
			sql_db = this.getWritableDatabase();
		}
		if(db_mode == DB_MODE_CLOSED) { sql_db = this.getWritableDatabase(); }

		sql_db.delete(TABLE_FEEDS, FEEDS_KEY_URL+" = ?", new String[] { url } );
		if(db_mode == DB_MODE_CLOSED) { sql_db.close(); }
		if(db_mode == DB_MODE_READ) {
			sql_db.close();
			sql_db = this.getReadableDatabase();
		}
	}

	public boolean feedIsInDB(String url) {
		String select_feed_q = "SELECT * FROM "+TABLE_FEEDS
								+" WHERE "+FEEDS_KEY_URL+" = '"+url+"'";
		if(db_mode == DB_MODE_CLOSED) { sql_db = this.getReadableDatabase(); }
		Cursor c = sql_db.rawQuery(select_feed_q, null);
		boolean feed_exists = false;
		if(c.moveToFirst()) {
			do {
				String u_url = c.getString(1);
				if(u_url.equals(url)) {
					feed_exists = true;
				}
			} while(c.moveToNext());
		}
		if(db_mode == DB_MODE_CLOSED) { sql_db.close(); }
		return feed_exists;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Create Feeds Table
		String CREATE_FEEDS_TABLE = "CREATE TABLE "+TABLE_FEEDS
				+"("+FEEDS_KEY_ID+" INTEGER PRIMARY KEY,"
				+FEEDS_KEY_URL+" TEXT)";
		db.execSQL(CREATE_FEEDS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int old_version, int new_version) {
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_FEEDS);
		onCreate(db);
	}
}
