package com.abhijith.note;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

import com.abhijith.note.events.NewNoteClickedEvent;
import com.abhijith.note.events.NoteAddedEvent;
import com.abhijith.note.model.Note;
import com.abhijith.note.model.dao.NoteDao;
import com.abhijith.note.util.BusProvider;
import com.squareup.otto.Subscribe;

public class NoteListFragment extends ListFragment {
	private Context mContext;

	private ListView mList;
	private NoteAdapter mAdapter;
	private ImageButton mMenuButton;
	private ImageButton mSearchButton;
	private Button mNewNoteButton;

	private PopupMenu popupMenu;

	public static Fragment newInstance() {
		return new NoteListFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_note_list, null);
		mList = (ListView) layout.findViewById(android.R.id.list);
		mNewNoteButton = (Button) layout.findViewById(android.R.id.empty);
		mNewNoteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				BusProvider.getInstance().post(new NewNoteClickedEvent());
			}
		});

		mMenuButton = (ImageButton) layout
				.findViewById(R.id.overflow_menu_button);
		popupMenu = new PopupMenu(mContext, mMenuButton);
		popupMenu.getMenuInflater().inflate(R.menu.main_menu,
				popupMenu.getMenu());

		mMenuButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupMenu.show();
			}
		});

		popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {

				switch (item.getItemId()) {
				case R.id.menu_delete:
					Intent intent = new Intent(getActivity(),
							DeleteNotesActivity.class);
					startActivity(intent);
					return true;
				}
				return true;
			}
		});

		mSearchButton = (ImageButton) layout.findViewById(R.id.search_action);
		mSearchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), SearchActivity.class);
				Bundle animOptionsBundle = ActivityOptions
						.makeScaleUpAnimation(v, v.getLeft(), v.getTop(),
								v.getWidth(), v.getHeight()).toBundle();
				mContext.startActivity(intent, animOptionsBundle);

			}
		});

		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		NoteDao dao = new NoteDao(getActivity());
		List<Note> allNotes = dao.listAll();
		mAdapter = new NoteAdapter(mContext, allNotes);
		mList.setAdapter(mAdapter);

		mList.setOnItemClickListener(new OnItemClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Note selectedNote = (Note) mList.getItemAtPosition(position);
				Intent intent = new Intent(mContext, NoteDetailActivity.class);
				intent.putExtra("note_id", selectedNote.getId());
				Bundle animOptionsBundle = ActivityOptions
						.makeScaleUpAnimation(view, view.getTop(),
								view.getBottom(), view.getWidth(),
								view.getHeight()).toBundle();
				mContext.startActivity(intent, animOptionsBundle);

			}
		});

	}

	private static class ViewHolder {
		public TextView noteText;
		public TextView timestamp;
	}

	public class NoteAdapter extends BaseAdapter {

		private ArrayList<Note> mNotes;
		private LayoutInflater inflater;

		public NoteAdapter(Context context, List<Note> notes) {
			super();
			this.inflater = LayoutInflater.from(context);
			this.mNotes = (ArrayList<Note>) notes;
			Collections.reverse(mNotes);
		}

		@Override
		public int getCount() {
			return mNotes.size();
		}

		@Override
		public Note getItem(int position) {
			return mNotes.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressWarnings("deprecation")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder;
			Note item = (Note) getItem(position);

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.listitem_note, null);
				holder = new ViewHolder();
				holder.noteText = (TextView) convertView
						.findViewById(R.id.textview_note);
				holder.timestamp = (TextView) convertView
						.findViewById(R.id.textview_note_date);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.noteText.setText(item.getNote());
			holder.timestamp.setText(item.getNoteTime().toLocaleString());
			return convertView;
		}

		public void addNote(Note note) {
			this.mNotes.add(0, note);
			notifyDataSetChanged();
		}
	}

	@Subscribe
	public void onNoteAdded(NoteAddedEvent event) {
		mAdapter.addNote(event.getAddedNote());
	}

	@Override
	public void onPause() {
		super.onPause();
		BusProvider.getInstance().unregister(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		BusProvider.getInstance().register(this);
	}

}
