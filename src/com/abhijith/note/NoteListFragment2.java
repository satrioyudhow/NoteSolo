package com.abhijith.note;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Animator.AnimatorListener;
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
import android.view.ViewPropertyAnimator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

import com.abhijith.note.events.NoteAddedEvent;
import com.abhijith.note.model.Note;
import com.abhijith.note.model.dao.NoteDao;
import com.abhijith.note.util.BusProvider;
import com.abhijith.note.widget.VelocityListView;
import com.abhijith.note.widget.VelocityListView.OnVelocityListViewListener;
import com.squareup.otto.Subscribe;

public class NoteListFragment2 extends ListFragment {

	private static final int VELOCITY_ABSOLUTE_THRESHOLD = 5500;

	private static final int BIT_VISIBILITY = 0x01;
	private static final int BIT_ANIMATION = 0x02;

	private static final int SCROLL_TO_TOP_HIDDEN = 0;
	private static final int SCROLL_TO_TOP_HIDING = BIT_ANIMATION;
	private static final int SCROLL_TO_TOP_SHOWN = BIT_VISIBILITY;
	private static final int SCROLL_TO_TOP_SHOWING = BIT_ANIMATION
			| BIT_VISIBILITY;

	private Context mContext;
	private VelocityListView mList;
	private NoteAdapter mAdapter;
	private Button mQuickReturnView;
	private View mHeader;
	private ImageButton mMenuButton;
	private PopupMenu popupMenu;

	private ViewPropertyAnimator mAnimator;

	private int mVelocityAbsoluteThreshold;
	private int mScrollToTopState = SCROLL_TO_TOP_HIDDEN;

	public static Fragment newInstance() {
		return new NoteListFragment2();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View layout = inflater.inflate(R.layout.fragment_note_list2, null);
		mHeader = inflater.inflate(R.layout.header, null);

		mMenuButton = (ImageButton) mHeader.findViewById(R.id.menu_button);
		popupMenu = new PopupMenu(mContext, mMenuButton);
		popupMenu.getMenuInflater().inflate(R.menu.main_menu,
				popupMenu.getMenu());

		mQuickReturnView = (Button) layout.findViewById(R.id.sticky);

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

		mQuickReturnView.setOnClickListener(new OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), SearchActivity.class);
				Bundle animOptionsBundle = ActivityOptions
						.makeScaleUpAnimation(mHeader, mHeader.getTop(),
								mHeader.getBottom(), mHeader.getWidth(),
								mHeader.getHeight()).toBundle();
				mContext.startActivity(intent, animOptionsBundle);
			}
		});

		// mPlaceHolder = mHeader.findViewById(R.id.placeholder);
		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mVelocityAbsoluteThreshold = (int) (VELOCITY_ABSOLUTE_THRESHOLD
				* getResources().getDisplayMetrics().density + 0.5f);

		mAnimator = mQuickReturnView.animate();
		mList = (VelocityListView) getListView();
		mList.addHeaderView(mHeader, null, false);
		mList.setOnVelocityListener(mOnVelocityListener);

		NoteDao dao = new NoteDao(getActivity());
		List<Note> allNotes = dao.listAll();
		mAdapter = new NoteAdapter(mContext, allNotes);
		setListAdapter(mAdapter);
	}

	private OnVelocityListViewListener mOnVelocityListener = new OnVelocityListViewListener() {
		@Override
		public void onVelocityChanged(int velocity) {
			if (velocity >= 0 && !mHeader.isShown()) {
				if (Math.abs(velocity) > mVelocityAbsoluteThreshold) {
					if ((mScrollToTopState & BIT_VISIBILITY) == 0) {
						mAnimator.translationY(0).setListener(mOnShownListener);
						mScrollToTopState = SCROLL_TO_TOP_SHOWING;
					}
				}
			} else {
				if ((mScrollToTopState & BIT_VISIBILITY) == BIT_VISIBILITY) {
					mAnimator.translationY(-mQuickReturnView.getHeight())
							.setListener(mOnHiddenListener);
					mScrollToTopState = SCROLL_TO_TOP_HIDING;
				}
			}
		}
	};

	private final AnimatorListener mOnHiddenListener = new AnimatorListenerAdapter() {
		public void onAnimationEnd(Animator animation) {
			mScrollToTopState = SCROLL_TO_TOP_HIDDEN;
		};
	};

	private final AnimatorListener mOnShownListener = new AnimatorListenerAdapter() {
		public void onAnimationEnd(Animator animation) {
			mScrollToTopState = SCROLL_TO_TOP_SHOWN;
		};
	};

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
			this.mNotes.add(note);
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
