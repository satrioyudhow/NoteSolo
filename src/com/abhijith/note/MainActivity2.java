package com.abhijith.note;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v4.widget.SlidingPaneLayout.PanelSlideListener;
import android.view.View;

public class MainActivity2 extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_sliding_layout);
		getWindow().setBackgroundDrawableResource(R.color.background_grey);

		SlidingPaneLayout slidingPane = (SlidingPaneLayout) findViewById(R.id.slidingpanelayout);
		slidingPane.setPanelSlideListener(new PanelSlideListener() {

			@Override
			public void onPanelSlide(View view, float arg1) {
			}

			@Override
			public void onPanelOpened(View view) {

			}

			@Override
			public void onPanelClosed(View view) {

			}
		});

	}

}
