package com.abhijith.note;

import android.app.Activity;
import android.os.Bundle;

public class SearchActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawableResource(R.color.background_grey);
		setContentView(R.layout.activity_search);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new SearchNoteFragment()).commit();
		}
	}

}
