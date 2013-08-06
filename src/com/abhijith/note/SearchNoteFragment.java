package com.abhijith.note;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.abhijith.note.model.Note;
import com.abhijith.note.model.dao.NoteDao;

public class SearchNoteFragment extends ListFragment {

	private EditText mSearchText;
	private NoteAdapter mAdapter;
	private Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_search_note, null);
		mSearchText = (EditText) layout.findViewById(R.id.search_text);
		mSearchText.addTextChangedListener(new SearchTextWatcher());

		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		NoteDao dao = new NoteDao(getActivity());
		List<Note> allNotes = dao.listAll();
		mAdapter = new NoteAdapter(mContext, allNotes);
		setListAdapter(mAdapter);

		AnimationSet set = new AnimationSet(true);
		Animation animation = AnimationUtils.loadAnimation(mContext,
				R.anim.title_upin);
		animation.setDuration(300);
		set.addAnimation(animation);
		LayoutAnimationController controller = new LayoutAnimationController(
				set, 0.5f);
		getListView().setLayoutAnimation(controller);

		// mSearchText.requestFocus();

	}

	public class NoteAdapter extends BaseAdapter implements Filterable {

		private ArrayList<Note> mNotes;
		private ArrayList<Note> mOriginalNotes;
		private LayoutInflater inflater;
		private Filter mFilter;
		private final Object mLock = new Object();

		public NoteAdapter(Context context, List<Note> notes) {
			super();
			this.inflater = LayoutInflater.from(context);
			this.mNotes = (ArrayList<Note>) notes;
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

		@Override
		public Filter getFilter() {
			if (mFilter == null) {
				mFilter = new NoteFilter();
			}
			return mFilter;
		}

		private class NoteFilter extends Filter {

			@SuppressLint("DefaultLocale")
			@Override
			protected FilterResults performFiltering(CharSequence prefix) {
				FilterResults results = new FilterResults();

				if (mOriginalNotes == null) {
					synchronized (mLock) {
						mOriginalNotes = new ArrayList<Note>(mNotes);
					}
				}

				if (prefix == null || prefix.length() == 0) {
					ArrayList<Note> list;
					synchronized (mLock) {
						list = new ArrayList<Note>(mOriginalNotes);
					}
					results.values = list;
					results.count = list.size();
				} else {
					String prefixString = prefix.toString().toLowerCase();

					ArrayList<Note> values;
					synchronized (mLock) {
						values = new ArrayList<Note>(mOriginalNotes);
					}

					final int count = values.size();
					final ArrayList<Note> newValues = new ArrayList<Note>();

					for (int i = 0; i < count; i++) {
						final Note value = values.get(i);
						final String valueText = value.toString().toLowerCase();

						// First match against the whole, non-splitted value
						if (valueText.startsWith(prefixString)) {
							newValues.add(value);
						} else {
							final String[] words = valueText.split(" ");
							final int wordCount = words.length;

							// Start at index 0, in case valueText starts with
							// space(s)
							for (int k = 0; k < wordCount; k++) {
								if (words[k].startsWith(prefixString)) {
									newValues.add(value);
									break;
								}
							}
						}
					}

					results.values = newValues;
					results.count = newValues.size();
				}

				return results;
			}

			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				mNotes = (ArrayList<Note>) results.values;
				if (results.count > 0) {
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}

			}

		}
	}

	private class SearchTextWatcher implements TextWatcher {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			mAdapter.getFilter().filter(s);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	}

}
