package com.abhijith.note;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;

import com.abhijith.note.events.NoteEditedEvent;
import com.abhijith.note.util.BusProvider;
import com.squareup.otto.Subscribe;

public class NoteDetailActivity extends Activity implements
		FragmentManager.OnBackStackChangedListener {

	private Handler mHandler = new Handler();
	private boolean mShowingEditMode = false;
	private FrameLayout mLeftActionButton;
	private FrameLayout mRightActionButton;
	private Animation leftActionAnimation;
	private Animation rightActionAnimation;
	private long mNoteId;
	private FrameLayout root;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().setBackgroundDrawableResource(R.color.background_grey);
		setContentView(R.layout.activity_note_detail);

		root = (FrameLayout) findViewById(R.id.container);
		// mLeftActionButton = (FrameLayout)
		// findViewById(R.id.action_button_left);
		// mRightActionButton = (FrameLayout)
		// findViewById(R.id.action_button_right);

		mNoteId = getIntent().getLongExtra("note_id", 0);

		// startAnimations();

		NoteViewFragment viewFragment = new NoteViewFragment();
		viewFragment.setArguments(getIntent().getExtras());

		if (savedInstanceState == null) {

			getFragmentManager().beginTransaction()
					.add(R.id.container, viewFragment).commit();
		} else {
			mShowingEditMode = (getFragmentManager().getBackStackEntryCount() > 0);
		}

		getFragmentManager().addOnBackStackChangedListener(this);
	}

	private void startAnimations() {

		leftActionAnimation = AnimationUtils.loadAnimation(this,
				R.anim.slide_in_left);
		leftActionAnimation.setInterpolator(new BounceInterpolator());
		leftActionAnimation.setDuration(700);

		rightActionAnimation = AnimationUtils.loadAnimation(this,
				R.anim.slide_in_right);
		rightActionAnimation.setInterpolator(new BounceInterpolator());
		rightActionAnimation.setDuration(700);

		mLeftActionButton.startAnimation(leftActionAnimation);
		mRightActionButton.startAnimation(rightActionAnimation);

	}

	private void flipCard() {
		if (mShowingEditMode) {
			getFragmentManager().popBackStack();
			mShowingEditMode = false;
			return;
		}

		// Flip to the edit mode.
		mShowingEditMode = true;

		// Create and commit a new fragment transaction that adds the fragment
		// for the back of
		// the card, uses custom animations, and is part of the fragment
		// manager's back stack.

		NoteEditFragment editFragment = new NoteEditFragment();
		editFragment.setArguments(getIntent().getExtras());

		getFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.card_flip_right_in,
						R.anim.card_flip_right_out, R.anim.card_flip_left_in,
						R.anim.card_flip_left_out)
				.replace(R.id.container, editFragment).addToBackStack(null)
				.commit();

		mHandler.post(new Runnable() {
			@Override
			public void run() {
				invalidateOptionsMenu();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// Add either a "photo" or "finish" button to the action bar, depending
		// on which page
		// is currently selected.
		MenuItem item = menu.add(Menu.NONE, R.id.action_flip, Menu.NONE,
				mShowingEditMode ? "Done Editing" : "Edit");
		item.setIcon(mShowingEditMode ? R.drawable.action_cancel
				: R.drawable.action_edit);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// Navigate "up" the demo structure to the launchpad activity.
			// See http://developer.android.com/design/patterns/navigation.html
			// for more.
			NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
			return true;

		case R.id.action_flip:
			flipCard();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackStackChanged() {
		mShowingEditMode = (getFragmentManager().getBackStackEntryCount() > 0);
		invalidateOptionsMenu();

	}

	@Subscribe
	public void onNoteEdited(NoteEditedEvent event) {
		Log.d("NoteDetail", "In subscriber");
		flipCard();
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
