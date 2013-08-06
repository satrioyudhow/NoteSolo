package com.abhijith.note.widget.expandlist;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

public class WrappedListAdapter extends BaseAdapter implements WrapperListAdapter{
	
	protected ListAdapter mWrappedAdapter;
	
	public WrappedListAdapter(ListAdapter mWrappedAdapter) {
		this.mWrappedAdapter = mWrappedAdapter;
	}

	public ListAdapter getWrappedAdapter() {
		return mWrappedAdapter;
	}

	public void setWrappedAdapter(ListAdapter mWrappedAdapter) {
		this.mWrappedAdapter = mWrappedAdapter;
	}

	@Override
	public int getCount() {
		return mWrappedAdapter.getCount();
	}

	@Override
	public Object getItem(int position) {
		return mWrappedAdapter.getItem(position);
	}

	@Override
	public long getItemId(int position) {
		return mWrappedAdapter.getItemId(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return mWrappedAdapter.getView(position, convertView, parent);
	}
	
	

}
