package com.br0xen.overcast;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FeedListFragment extends ListFragment {
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private int mActivatedPosition = ListView.INVALID_POSITION;
	private DatabaseHelper db; // Need passed from Activity
    private Handler mHandler = new Handler(); 
	private FeedAdapter feed_adapter;
    public interface Callbacks { public void onItemSelected(String id); }
    private Callbacks sFeedCallbacks = new Callbacks() { public void onItemSelected(String id) {} };
    private Callbacks mCallbacks = sFeedCallbacks;
    public FeedListFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		db = ((FeedListActivity)getActivity()).getDB();
        mHandler.postDelayed(mUpdateViewTask, 100);
    }

	public void updateList() {
		if(feed_adapter == null) {
			feed_adapter = new FeedAdapter();
			setListAdapter(feed_adapter);
		}
		feed_adapter.notifyDataSetChanged();
	}

	class FeedAdapter extends ArrayAdapter<Feed> {
		FeedAdapter() {
			super(getActivity(), R.layout.item_list_row, R.id.entry_title, db.all_feeds);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row=super.getView(position, convertView, parent);
			Feed wrk = db.all_feeds.get(position);
			TextView txt_entry_title=(TextView)row.findViewById(R.id.entry_title);
			TextView txt_entry_date=(TextView)row.findViewById(R.id.entry_date);
			TextView txt_entry_filename=(TextView)row.findViewById(R.id.entry_filename);
			TextView txt_entry_url=(TextView)row.findViewById(R.id.feed_url);
			if(!wrk.updated) {
				txt_entry_title.setText(wrk.url);
				txt_entry_date.setText("Loading... Please Wait.");
			} else {
				txt_entry_title.setText(wrk.title);
				if(wrk.title.equals("Error Loading Feed!") || (wrk.title.equals("") && wrk.file_name.equals(""))) {
					txt_entry_title.setText("Error Loading Feed!");
					txt_entry_date.setText(wrk.url);
				} else {
					txt_entry_date.setText(wrk.pub_date+"");
					txt_entry_filename.setText("File Name: "+wrk.file_name);
					txt_entry_url.setText(wrk.url);
				}
			}
			return row;
		}
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
		if (savedInstanceState != null && savedInstanceState
			.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sFeedCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        mCallbacks.onItemSelected(position+"");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    public void setActivateOnItemClick(boolean activateOnItemClick) {
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    public void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

	/* Update View Runnable */
    private Runnable mUpdateViewTask = new Runnable() {
        public void run() {
			try {
				updateList();
			} catch(NullPointerException e) { 
			} finally {
				mHandler.postDelayed(this, 500);
			}
        }
    };
}
