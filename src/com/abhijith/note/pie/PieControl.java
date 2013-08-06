package com.abhijith.note.pie;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

import com.abhijith.note.R;

public class PieControl implements OnClickListener {

	public interface PieMenuController {
		void onPieItemSelected(View view);
	}

	public static final String PIE_ITEM_CLEAR = "##clear##";
	public static final String PIE_ITEM_COPY = "##copy##";
	public static final String PIE_ITEM_SHARE = "##share##";
	public static final String PIE_ITEM_MAIL = "##mail##";
	public static final String PIE_BUTTON_FILLER = "#filler";

	protected Context mContext;
	protected PieMenuController mController;
	protected PieMenu mPie;
	protected int mItemSize;
	protected TextView mTabsCount;

	private PieItem mCamera;
	private PieItem mCloud;
	private PieItem mSocial;
	private PieItem mScreen;

	public PieControl(Context context, PieMenuController controller, FrameLayout container) {		
		mContext = context;
		mController = controller;
		attachToContainer(container);
		mItemSize = (int) context.getResources().getDimension(
				R.dimen.pie_item_size);
	}

	private void attachToContainer(FrameLayout container) {
		if (mPie == null) {
			mPie = new PieMenu(mContext);
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			mPie.setLayoutParams(lp);
			populateMenu();
		}
		container.addView(mPie);
	}

	protected void removeFromContainer(FrameLayout container) {
		container.removeView(mPie);
	}

	protected void forceToTop(FrameLayout container) {
		if (mPie.getParent() != null) {
			container.removeView(mPie);
			container.addView(mPie);
		}
	}

	protected void setClickListener(OnClickListener listener, PieItem... items) {
		for (PieItem item : items) {
			item.getView().setOnClickListener(listener);
		}
	}

	public boolean onTouchEvent(MotionEvent event) {
		return mPie.onTouchEvent(event);
	}

	protected void populateMenu() {
		mCamera = makeItem(R.drawable.action_clear, 1, PIE_ITEM_CLEAR);
		mCloud = makeItem(R.drawable.action_copy, 1, PIE_ITEM_COPY);
		mSocial = makeItem(R.drawable.action_share, 1, PIE_ITEM_SHARE);
		mScreen = makeItem(R.drawable.action_mail, 1, PIE_ITEM_MAIL);

		// level 1
		mPie.addItem(mCamera);
		mPie.addItem(mCloud);
		mPie.addItem(mSocial);
		mPie.addItem(mScreen);

	}

	@Override
	public void onClick(View v) {
		mController.onPieItemSelected(v);
	}

	public PieItem makeItem(int image, int l, String name) {
		ImageView view = new ImageView(mContext);
		view.setImageResource(image);
		view.setMinimumWidth(mItemSize);
		view.setMinimumHeight(mItemSize);
		view.setScaleType(ScaleType.CENTER);
		LayoutParams lp = new LayoutParams(mItemSize, mItemSize);
		view.setLayoutParams(lp);
		view.setOnClickListener(this);
		return new PieItem(view, l, name);
	}

	
	protected PieItem makeFiller() {
		return new PieItem(null, 1, PIE_BUTTON_FILLER);
	}

}
