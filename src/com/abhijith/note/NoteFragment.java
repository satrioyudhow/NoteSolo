package com.abhijith.note;

import java.util.Date;

import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.abhijith.note.events.NoteAddedEvent;
import com.abhijith.note.model.Note;
import com.abhijith.note.model.dao.NoteDao;
import com.abhijith.note.pie.PieControl;
import com.abhijith.note.pie.PieControl.PieMenuController;
import com.abhijith.note.util.BusProvider;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

public class NoteFragment extends Fragment implements PieMenuController {

	public static Fragment newInstance() {
		return new NoteFragment();
	}

	private Context mContext;
	private EditText mNoteText;
	private PullToRefreshScrollView mPtrScrollView;
	private FrameLayout mFrame;
	private PieControl mPieControl;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View layout = inflater.inflate(R.layout.fragment_note, null);
		mFrame = (FrameLayout) layout.findViewById(R.id.frame);
		mNoteText = (EditText) layout.findViewById(R.id.note_text);
		mPtrScrollView = (PullToRefreshScrollView) layout
				.findViewById(R.id.ptr_scrollview);
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
						new SaveDataTask().execute();
					}
				});
		mPieControl = new PieControl(mContext, this, mFrame);
		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ClipboardManager clipboard = (ClipboardManager)
		        mContext.getSystemService(Context.CLIPBOARD_SERVICE);

		((InputMethodManager) mContext
				.getSystemService(mContext.INPUT_METHOD_SERVICE))
				.showSoftInput(mNoteText, InputMethodManager.SHOW_IMPLICIT);

	}

	private class SaveDataTask extends AsyncTask<Void, Void, Boolean> {

		private Note mNote;

		@Override
		protected Boolean doInBackground(Void... params) {
			// Simulates a background job.
			String noteBody = mNoteText.getText().toString().trim();
			Log.d("NoteBody", noteBody);
			if (noteBody.equals("")) {
				Log.d("Note:nothing", noteBody);
				return false;
			}
			
			NoteDao dao = new NoteDao(getActivity());
			mNote = new Note();
			mNote.setNote(noteBody);
			mNote.setNoteTime(new Date(System.currentTimeMillis()));

			dao.insert(mNote);
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (!result) {
				Toast toast = Toast.makeText(getActivity(),
						"Enter Something Bugger", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0,
						0);
				toast.show();
			} else {
				Toast toast = Toast.makeText(getActivity(), "Note Saved",
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0,
						0);
				toast.show();
				mNoteText.setText("");
				BusProvider.getInstance().post(new NoteAddedEvent(mNote));
			}
			mPtrScrollView.onRefreshComplete();

			super.onPostExecute(result);
		}
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


	@Override
	public void onPieItemSelected(View v) {
		
		if (v.getTag().equals(PieControl.PIE_ITEM_CLEAR)) {
			mNoteText.setText("");
			
		} else if (v.getTag().equals(PieControl.PIE_ITEM_COPY)) {
			
			ClipboardManager clipboard = (ClipboardManager)
			        mContext.getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData data = ClipData.newPlainText("NoteSolo", mNoteText.getText());
			clipboard.setPrimaryClip(data);
			Toast.makeText(mContext, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
			
		} else if (v.getTag().equals(PieControl.PIE_ITEM_SHARE)) {
			
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
			sendIntent.setType("text/plain");
			startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.share_with)));
			
		} else if (v.getTag().equals(PieControl.PIE_ITEM_MAIL)) {
			
			Intent mailIntent = new Intent(Intent.ACTION_SEND);
			mailIntent.setType("message/rfc822");
			mailIntent.putExtra(Intent.EXTRA_TEXT, mNoteText.getText());
			Intent mailer = Intent.createChooser(mailIntent, null);
			startActivity(mailer);
			
		}
	}

}
