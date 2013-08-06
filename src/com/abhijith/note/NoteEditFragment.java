package com.abhijith.note;

import java.util.Date;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;

import com.abhijith.note.events.NoteEditedEvent;
import com.abhijith.note.model.Note;
import com.abhijith.note.model.dao.NoteDao;
import com.abhijith.note.util.BusProvider;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

public class NoteEditFragment extends Fragment {

	private long mNoteId;
	private Note mNote;
	private EditText mNoteTextEdit;
	private PullToRefreshScrollView mPtrScrollView;
	private Context mContext;
	private Handler mHandler = new Handler();

	public NoteEditFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		mContext = getActivity();
		mNoteId = getArguments().getLong("note_id");
		NoteDao dao = new NoteDao(getActivity());
		mNote = dao.get(mNoteId);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_note_edit, container,
				false);
		mNoteTextEdit = (EditText) layout.findViewById(R.id.note_text_edit);

		mPtrScrollView = (PullToRefreshScrollView) layout
				.findViewById(R.id.ptr_scrollview_edit_note);

		mPtrScrollView.getLoadingLayoutProxy().setReleaseLabel(
				"Release to Save...");
		mPtrScrollView.getLoadingLayoutProxy().setPullLabel("Pull to Save...");
		mPtrScrollView.getLoadingLayoutProxy().setRefreshingLabel(
				"Saving Note...");

		mPtrScrollView
				.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						((InputMethodManager) getActivity().getSystemService(
								Context.INPUT_METHOD_SERVICE))
								.hideSoftInputFromWindow(
										mNoteTextEdit.getWindowToken(), 0);
						BusProvider.getInstance().register(this);
						new SaveDataTask().execute();
					}
				});

		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mNoteTextEdit.setText(mNote.getNote());

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				mNoteTextEdit.requestFocus();
				mNoteTextEdit.setSelection(mNoteTextEdit.getText().length());
				InputMethodManager mgr = (InputMethodManager) mContext
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				mgr.showSoftInput(mNoteTextEdit,
						InputMethodManager.SHOW_IMPLICIT);
			}
		};
		mHandler.postDelayed(runnable, 300);
	}

	private class SaveDataTask extends AsyncTask<Void, Void, Boolean> {

		private Note mNote;

		@Override
		protected Boolean doInBackground(Void... params) {
			// Simulates a background job.
			String noteBody = mNoteTextEdit.getText().toString().trim();
			Log.d("NoteBody", noteBody);
			if (noteBody.equals("")) {
				Log.d("Note:nothing", noteBody);
				return false;
			}

			NoteDao dao = new NoteDao(getActivity());
			Note note = new Note();
			note.setId(mNoteId);
			note.setNote(noteBody);
			note.setNoteTime(new Date(System.currentTimeMillis()));
			dao.update(note);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {

			mPtrScrollView.onRefreshComplete();
			BusProvider.getInstance().post(new NoteEditedEvent(mNoteId));
			super.onPostExecute(result);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		BusProvider.getInstance().register(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		BusProvider.getInstance().unregister(this);
	}

}
