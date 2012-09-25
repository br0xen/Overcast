package com.br0xen.overcast;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FeedDetailFragment extends Fragment {
	public static final String ARG_FEED = "the_feed";
	Feed mItem;

	public FeedDetailFragment() { }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments().containsKey(ARG_FEED)) {
			// Get the feed from the Bundle
			mItem = getArguments().getParcelable(ARG_FEED);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_feed_detail, container, false);
		if(mItem != null) {
			// Update the display with mItem's Content
			((TextView)rootView.findViewById(R.id.feed_title)).setText(mItem.feed_title);
			((TextView)rootView.findViewById(R.id.feed_url)).setText(mItem.url);
			((TextView)rootView.findViewById(R.id.episode_title)).setText(mItem.title);
			((TextView)rootView.findViewById(R.id.episode_file)).setText(mItem.full_file_name);
			NumberFormat nf = new DecimalFormat("###,###,###,###");
			((TextView)rootView.findViewById(R.id.episode_file_sz)).setText(nf.format(mItem.file_length));
			((TextView)rootView.findViewById(R.id.episode_file_tp)).setText(mItem.file_type);
			((TextView)rootView.findViewById(R.id.episode_pub_dt)).setText(mItem.pub_date+"");
		}
		return rootView;
	}
}
