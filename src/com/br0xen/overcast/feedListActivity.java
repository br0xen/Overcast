package com.br0xen.overcast;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class FeedListActivity extends FragmentActivity
					implements FeedListFragment.Callbacks {
	public static final String ARG_DB = "the_db";
	private boolean m_two_pane;
	private DatabaseHelper db;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_list);
		if(savedInstanceState == null || !savedInstanceState.containsKey(ARG_DB)) {
			this.db = new DatabaseHelper(this.getApplicationContext());
		}
		if(findViewById(R.id.feed_detail_container) != null) {
			m_two_pane = true;
			((FeedListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.feed_list))
					.setActivateOnItemClick(true);
		}
    }

	@Override
	public void onResume() {
		super.onResume();
		for(Feed f : db.all_feeds) {
			if(!f.updated) {
				f.updateFeed();
			}
		}
	}

	@Override	
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		ArrayList<Feed> af = ((ArrayList)db.all_feeds);
		savedInstanceState.putParcelableArrayList(ARG_DB, af);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if(db == null) { db = new DatabaseHelper(this.getApplicationContext(), true); }
		if(savedInstanceState != null && savedInstanceState.containsKey(ARG_DB)) {
			ArrayList<Feed> fl = savedInstanceState.getParcelableArrayList(ARG_DB);
			db.loadFeedArray(fl);
			// Now update the ones that need it.
			for(Feed f : db.all_feeds) {
				if(!f.updated) {
					f.updateFeed();
				}
			}
		}
	}

	@Override
	public void onBackPressed() { finish(); }

    public void onItemSelected(String id) {
		Feed sel_feed = db.all_feeds.get(Integer.parseInt(id));
		if(sel_feed.updated) {
			if(m_two_pane) {
				Bundle arguments = new Bundle();
				arguments.putParcelable(FeedDetailFragment.ARG_FEED, sel_feed);
				FeedDetailFragment fragment = new FeedDetailFragment();
				fragment.setArguments(arguments);
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.feed_detail_container, fragment)
						.commit();
			} else {
				Intent detailIntent = new Intent(this, FeedDetailActivity.class);
				detailIntent.putExtra(FeedDetailFragment.ARG_FEED, db.all_feeds.get(Integer.parseInt(id)));
				startActivity(detailIntent);
			}
		} else {
			// Alert to wait for feed to update...
		}
	}

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
