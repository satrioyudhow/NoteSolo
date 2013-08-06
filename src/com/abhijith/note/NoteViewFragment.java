package com.abhijith.note;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.abhijith.note.events.NoteEditedEvent;
import com.abhijith.note.model.Note;
import com.abhijith.note.model.dao.NoteDao;
import com.abhijith.note.util.BusProvider;
import com.squareup.otto.Subscribe;

public class NoteViewFragment extends Fragment {

	private long mNoteId;
	private Note mNote;
	private TextView mNoteTextView;
	private Handler mHandler = new Handler();

	public NoteViewFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		mNoteId = getArguments().getLong("note_id");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_note_view, container,
				false);
		mNoteTextView = (TextView) layout.findViewById(R.id.note_text_view);
		return layout;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		NoteDao dao = new NoteDao(getActivity());
		mNote = dao.get(mNoteId);
		mNoteTextView.setText(mNote.getNote());
	}

	@Subscribe
	public void onNoteEdited(NoteEditedEvent event) {
		Log.d("NoteEditedFragment", "In subscriber" + event.getNoteId());
		if (mNoteId == event.getNoteId()) {
			NoteDao dao = new NoteDao(getActivity());
			mNote = dao.get(event.getNoteId());
			mNoteTextView.setText(mNote.getNote());
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				((InputMethodManager) getActivity().getSystemService(
						Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
						mNoteTextView.getWindowToken(), 0);
				Log.e("VIEWFrag", "InHandler");
			}
		};
		mHandler.postDelayed(runnable, 250);
		BusProvider.getInstance().register(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		BusProvider.getInstance().unregister(this);
	}

}
