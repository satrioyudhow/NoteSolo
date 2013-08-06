package com.abhijith.note;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.abhijith.note.events.NewNoteClickedEvent;
import com.abhijith.note.util.BusProvider;
import com.squareup.otto.Subscribe;

public class MainActivity extends FragmentActivity {

	private static final int NOTE_LIST_FRAGMENT = 0;
	private static final int NOTE_FRAGMENT = 1;

	ViewPager mPager;
	NotePagerAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getWindow().setBackgroundDrawableResource(R.color.background_grey);
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setOffscreenPageLimit(2);
		
		/*
		NoteDao dao = new NoteDao(this);
		dao.deleteAll();
		*/

		//mPager.setPageTransformer(true, new TabletTransformer());
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {

				if (state == ViewPager.SCROLL_STATE_IDLE) {
					if (mPager.getCurrentItem() != NOTE_FRAGMENT) {
						// Hide the keyboard.
						((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
								.hideSoftInputFromWindow(
										mPager.getWindowToken(), 0);
					}

				}

			}
		});

		mAdapter = new NotePagerAdapter(getSupportFragmentManager(), this);
		mPager.setAdapter(mAdapter);
		mPager.setCurrentItem(NOTE_FRAGMENT);
	}
	
	

	public class NotePagerAdapter extends FragmentPagerAdapter {

		private String[] frags = { NoteListFragment.class.getName(),
				NoteFragment.class.getName() };

		private Context mContext;

		public NotePagerAdapter(FragmentManager fm, Context context) {
			super(fm);
			mContext = context;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case NOTE_LIST_FRAGMENT:
				return NoteListFragment.newInstance();

			case NOTE_FRAGMENT:
				return NoteFragment.newInstance();
			}
			return null;
		}

		@Override
		public int getCount() {
			return frags.length;
		}

	}
	
	@Subscribe
	public void onNewNoteClicked(NewNoteClickedEvent event) {
		mPager.setCurrentItem(NOTE_FRAGMENT, true);
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

	public class CardTransformer implements PageTransformer {
		private static final float SCALE_MAX = 0.7f;
		
		@Override
		public void transformPage(View page, float position) {

			// animating right
			if (position >= 0) {
				final int w = page.getMeasuredWidth();
				float scaleFactor = 1 - SCALE_MAX * position;
				page.setAlpha(1 - position);
				page.setScaleX(scaleFactor);
				page.setScaleY(scaleFactor);
				page.setTranslationX(w * (1 - position) - w);
			}
		}
	}

	public class TabletTransformer implements PageTransformer {

		private float mTrans;
		private float mRot;
		private Matrix mMatrix = new Matrix();
		private Camera mCamera = new Camera();
		private float[] mTempFloat2 = new float[2];

		@Override
		public void transformPage(View page, float pos) {

			if (pos <= 0) {
				mRot = 30.0f * Math.abs(pos);
				mTrans = getOffsetXForRotation(mRot, page.getMeasuredWidth(),
						page.getMeasuredHeight());
				page.setPivotX(page.getMeasuredWidth() / 2);
				page.setPivotY(page.getMeasuredHeight() / 2);
				page.setTranslationX(mTrans);
				page.setRotationY(mRot);
			} else {
				mRot = -30.0f * Math.abs(pos);
				mTrans = getOffsetXForRotation(mRot, page.getMeasuredWidth(),
						page.getMeasuredHeight());
				page.setPivotX(page.getMeasuredWidth() / 2);
				page.setPivotY(page.getMeasuredHeight() / 2);
				page.setTranslationX(mTrans);
				page.setRotationY(mRot);

			}
		}

		private float getOffsetXForRotation(float degrees, int width, int height) {
			mMatrix.reset();
			mCamera.save();
			mCamera.rotateY(Math.abs(degrees));
			mCamera.getMatrix(mMatrix);
			mCamera.restore();

			mMatrix.preTranslate(-width * 0.5f, -height * 0.5f);
			mMatrix.postTranslate(width * 0.5f, height * 0.5f);
			mTempFloat2[0] = width;
			mTempFloat2[1] = height;
			mMatrix.mapPoints(mTempFloat2);
			return (width - mTempFloat2[0]) * (degrees > 0.0f ? 1.0f : -1.0f);
		}

	}

}
