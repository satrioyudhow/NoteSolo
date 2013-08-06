package com.abhijith.note;

import android.app.Activity;

public class EvernoteActivity extends Activity {
	
	/*

	private static final String EVERNOTE_CONSUMER_KEY = "abhijith-ss";
	private static final String EVERNOTE_CONSUMER_SECRET = "570b96025f33bd2f";

	// Initial development is done on Evernote's testing service, the sandbox.
	// Change to HOST_PRODUCTION to use the Evernote production service
	// once your code is complete, or HOST_CHINA to use the Yinxiang Biji
	// (Evernote China) production service.
	private static final String EVERNOTE_HOST = EvernoteSession.HOST_SANDBOX;
	private EvernoteSession mEvernoteSession;

	TextView text;
	Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dropbox);
		text = (TextView) findViewById(R.id.textView1);
		button = (Button) findViewById(R.id.button1);
		setupSession();
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mEvernoteSession.isLoggedIn()) {
					mEvernoteSession.logOut(getApplicationContext());
				} else {
					mEvernoteSession.authenticate(EvernoteActivity.this);
				}
				// updateUi();
			}
		});

	}

	private void setupSession() {
		// Retrieve persisted authentication information
		mEvernoteSession = EvernoteSession.init(this, EVERNOTE_CONSUMER_KEY,
				EVERNOTE_CONSUMER_SECRET, EVERNOTE_HOST, null);
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateUi();
	}

	void updateUi() {
		if (mEvernoteSession.isLoggedIn()) {
			button.setText("logout");
			text.setText("Logged into evernote");
		} else {
			button.setText("login");
			text.setText("Login to Evernote");
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		// Update UI when oauth activity returns result
		case EvernoteSession.REQUEST_CODE_OAUTH:
			if (resultCode == Activity.RESULT_OK) {
				updateUi();
			}
			break;
		}
	}
	*/

}
