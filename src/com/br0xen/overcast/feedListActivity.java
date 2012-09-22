package com.br0xen.overcast;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class feedListActivity extends FragmentActivity
					implements feedListFragment.Callbacks {
	private DatabaseHelper db;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_list);
		this.db = new DatabaseHelper(this.getApplicationContext());
    }

	@Override
	public void onResume() {
		super.onResume();
		this.db.updateAllFeeds();
	}

	@Override
	public void onBackPressed() { finish(); }

    public void onItemSelected(String id) {}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.feeds_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.menu_edit_feeds:
				// Open Edit Feeds Activity
				Intent edit_feeds_activity = new Intent(this, EditFeedsActivity.class);
				startActivity(edit_feeds_activity);
				finish();
				break;
		}
		return true;
	}

	public DatabaseHelper getDB() {
		return this.db;
	}
}
