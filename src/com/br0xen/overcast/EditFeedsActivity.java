package com.br0xen.overcast;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class EditFeedsActivity extends ListActivity {
	private DatabaseHelper db;
	private FeedAdapter feed_adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_feeds);
		db = new DatabaseHelper(this.getApplicationContext());
		feed_adapter = new FeedAdapter();
		setListAdapter(feed_adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_edit_feeds, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.menu_add_new:
				buildAddDialog();
				break;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		Intent going_back = new Intent(this, FeedListActivity.class);
		startActivity(going_back);
		finish();
	}

	public void buildAddDialog() {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Add New Feed");
			alert.setMessage("New Feed URL:");
			final EditText input = new EditText(this);
			alert.setView(input);

			alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String value = input.getText().toString();
					db.addNewFeed(value);
					db.all_feeds.add(new Feed(value));
					feed_adapter.notifyDataSetChanged();
				}
			});
			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
				}
			});
			alert.show();
	}
	public void buildEditDialog(String url) {
			final String f_url = url;
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Edit Feed");
			alert.setMessage("Feed URL:");
			final EditText input = new EditText(this);
			input.setText(f_url);
			alert.setView(input);

			alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String value = input.getText().toString();
					updateFeed(f_url, value);
					feed_adapter.notifyDataSetChanged();
				}
			});
			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
				}
			});
			alert.show();
	}

	public void updateFeed(String old_url, String new_url) {
//		db = new DatabaseHelper(this.getApplicationContext());
		db.updateFeed(old_url, new_url);
		for(Feed f : db.all_feeds) {
			if(f.url.equals(old_url)) {
				f.setURL(new_url);
			}
		}
	}

	public void buildDeleteAlert(String url) {
			final String f_url = url;
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Delete Feed?");
			alert.setMessage("To delete this feed, click 'OK'");
			alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Delete the Feed
					deleteFeed(f_url);
					feed_adapter.notifyDataSetChanged();
				}
			});
			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
				}
			});
			alert.show();
	}

	public void deleteFeed(String del_url) {
		db.deleteFeed(del_url);
		Feed wrk = null;
		for(Feed f : db.all_feeds) {
			if(f.url.equals(del_url)) {
				wrk = f;
			}
		}
		if(wrk!=null) {
			db.all_feeds.remove(wrk);
		}
	}

	class FeedAdapter extends ArrayAdapter<Feed> {
		FeedAdapter() {
			super(EditFeedsActivity.this, R.layout.edit_feed_row, R.id.feed_url, db.all_feeds);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row=super.getView(position, convertView, parent);
			ImageView del_icon=(ImageView)row.findViewById(R.id.delete_feed);
			del_icon.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					String url = ((TextView)((View)v.getParent()).findViewById(R.id.feed_url)).getText().toString();
					buildDeleteAlert(url);
				}
			});
			ImageView edit_icon=(ImageView)row.findViewById(R.id.edit_feed);
			edit_icon.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					String url = ((TextView)((View)v.getParent()).findViewById(R.id.feed_url)).getText().toString();
					buildEditDialog(url);
				}
			});
			return row;
		}
	}
}
