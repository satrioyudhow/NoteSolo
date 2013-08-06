package com.abhijith.note.widget.expandlist;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

public abstract class AbstractSlideListAdapter extends WrappedListAdapter {

	// Keeping track of the last open ListViewItem
	private View lastOpenView = null;
	private int lastOPenPosition = -1;

	private int animationDuration = 300;

	public AbstractSlideListAdapter(ListAdapter mWrappedAdapter) {
		super(mWrappedAdapter);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = mWrappedAdapter.getView(position, convertView, parent);
		enableFor(convertView, position);
		return convertView;
	} 

	private void enableFor(View parent, int position) {
		View more = getExpandToggleButton(parent);
		View itemToolbar = getExpandableView(parent);
		itemToolbar.measure(parent.getWidth(), parent.getHeight());
		enableFor(more, itemToolbar, position);
		
	}

	private void enableFor(View more, View itemToolbar, int position) {
		// TODO Auto-generated method stub
		
	}

	private View getExpandableView(View parent) {
		// TODO Auto-generated method stub
		return null;
	}

	private View getExpandToggleButton(View parent) {
		// TODO Auto-generated method stub
		return null;
	}

}
