package com.abhijith.note;

import android.app.Activity;
import android.os.Bundle;

public class DeleteNotesActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawableResource(R.color.background_grey);
		setContentView(R.layout.activity_delete_notes);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new DeleteNotesFragment2()).commit();
		}
		
	}

}
