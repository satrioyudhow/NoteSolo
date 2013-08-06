package com.abhijith.note;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.abhijith.note.model.dao.NoteDao;

public class SettingsFragment extends Fragment {
	private ListView mList;
	private Context mContext;

	public static final String[] services = { "Dropbox",
			"Evernote","Clear Data","NoteDetail" };

	public static final int DROPBOX = 0;
	public static final int GOOGLE_DRIVE = 1;
	public static final int EVERNOTE = 2;

	public static Fragment newInstance() {
		return new SettingsFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_settings, null);
		mList = (ListView) layout.findViewById(R.id.list);
		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mList.setAdapter(new ArrayAdapter<String>(mContext,
				android.R.layout.simple_list_item_1, services));
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				if(position==0){
					Intent intenet = new Intent(getActivity(),DropBoxActivity.class);
					startActivity(intenet);
				}else if(position == 1){
					Intent intenet = new Intent(getActivity(),EvernoteActivity.class);
					startActivity(intenet);
				}else if(position == 2){
					NoteDao dao = new NoteDao(getActivity());
					dao.deleteAll();
				}else if(position == 3){
					Intent intenet = new Intent(getActivity(),NoteDetailActivity.class);
					startActivity(intenet);
				}
				
			}
		});
	}
}
