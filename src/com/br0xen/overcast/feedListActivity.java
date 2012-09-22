package com.br0xen.overcast;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class feedListActivity extends FragmentActivity
					implements feedListFragment.Callbacks {

//    private boolean mTwoPane;

	private DatabaseHelper db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_list);
		this.db = new DatabaseHelper(this.getApplicationContext());
/*
        if (findViewById(R.id.feed_detail_container) != null) {
            mTwoPane = true;
            ((feedListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.feed_list))
                    .setActivateOnItemClick(true);
        }
*/
    }

	@Override
	public void onResume() {
		super.onResume();
		this.db.updateAllFeeds();
	}

	@Override
	public void onBackPressed() {
		finish();
	}

    public void onItemSelected(String id) {
/* Disabled For Now
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(feedDetailFragment.ARG_ITEM_ID, id);
            feedDetailFragment fragment = new feedDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.feed_detail_container, fragment)
                    .commit();

        } else {
            Intent detailIntent = new Intent(this, feedDetailActivity.class);
            detailIntent.putExtra(feedDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
*/
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
