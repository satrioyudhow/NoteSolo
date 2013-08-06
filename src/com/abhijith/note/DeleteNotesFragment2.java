package com.abhijith.note;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.app.ActionBar;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.abhijith.note.model.Note;
import com.abhijith.note.model.dao.NoteDao;
import com.haarman.listviewanimations.itemmanipulation.contextualundo.ContextualUndoAdapter;
import com.haarman.listviewanimations.itemmanipulation.contextualundo.ContextualUndoAdapter.DeleteItemCallback;

public class DeleteNotesFragment2 extends ListFragment {

	private NoteAdapter mAdapter;
	private Context mContext;
	private Stack<Note> mDeletedNoteStack;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		mDeletedNoteStack = new Stack<Note>();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_delete_note, null,
				false);

		// Inflate a "Done/Discard" custom action bar view.
		LayoutInflater abInflater = (LayoutInflater) getActivity()
				.getActionBar().getThemedContext()
				.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
		final View customActionBarView = abInflater.inflate(
				R.layout.actionbar_custom_view_done_discard, null);

		customActionBarView.findViewById(R.id.actionbar_done)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// "Done"
						getActivity().finish(); // TODO: don't just finish()!
					}
				});
		customActionBarView.findViewById(R.id.actionbar_discard)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// "Discard"
						getActivity().finish(); // TODO: don't just finish()!
					}
				});

		// Show the custom action bar view and hide the normal Home icon and
		// title.
		final ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
				ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
						| ActionBar.DISPLAY_SHOW_TITLE);
		actionBar.setCustomView(customActionBarView,
				new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT));
		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		NoteDao dao = new NoteDao(getActivity());
		List<Note> allNotes = dao.listAll();
		mAdapter = new NoteAdapter(mContext, allNotes);

		ContextualUndoAdapter contextualUndoAdapter = new ContextualUndoAdapter(
				mAdapter, R.layout.undo_row, R.id.undo_row_undobutton);
		contextualUndoAdapter.setListView(getListView());
		getListView().setAdapter(contextualUndoAdapter);
		contextualUndoAdapter.setDeleteItemCallback(new MyDeleteItemCallback());

	}

	private class MyDeleteItemCallback implements DeleteItemCallback {

		@Override
		public void deleteItem(int position) {
			mAdapter.remove(position);
			mAdapter.notifyDataSetChanged();
		}
	}

	public class NoteAdapter extends BaseAdapter {

		private ArrayList<Note> mNotes;
		private LayoutInflater inflater;

		public NoteAdapter(Context context, List<Note> notes) {
			super();
			this.inflater = LayoutInflater.from(context);
			this.mNotes = (ArrayList<Note>) notes;
		}

		public void remove(Object item) {
			mNotes.remove(item);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mNotes.size();
		}

		@Override
		public Object getItem(int position) {
			return mNotes.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.listitem_note, null,
						false);
			}
			TextView noteText = (TextView) convertView
					.findViewById(R.id.textview_note);
			TextView noteTime = (TextView) convertView
					.findViewById(R.id.textview_note_date);
			noteText.setText(mNotes.get(position).getNote());
			noteTime.setText(mNotes.get(position).getNoteTime()
					.toLocaleString());
			return convertView;
		}

	}

}
