package com.abhijith.note;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.abhijith.note.util.DropBoxHelper;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.TokenPair;

public class DropBoxActivity extends Activity {

	DropBoxHelper dropbox;
	TextView text;
	Button button;
	private boolean loggedIn;
	DropboxAPI<AndroidAuthSession> dbApi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dropbox);
		text = (TextView) findViewById(R.id.textView1);
		button = (Button) findViewById(R.id.button1);

		dropbox = new DropBoxHelper(DropBoxActivity.this);
		loggedIn = dropbox.isLoggedIn();
		dbApi = dropbox.getApi();

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (loggedIn) {
					dropbox.logOut();
					button.setText("Login");
					text.setText("Please login to DropBox");
					loggedIn = false;
				} else {
					dbApi.getSession().startAuthentication(DropBoxActivity.this);
				}

			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		AndroidAuthSession session = dbApi.getSession();

		if (session.authenticationSuccessful()) {
			try {
				// Mandatory call to complete the auth
				session.finishAuthentication();
				TokenPair tokens = session.getAccessTokenPair();
				dropbox.setApi(dbApi);
                dropbox.storeKeys(tokens.key, tokens.secret);
                dropbox.setLoggedIn(true);
                loggedIn = true;

			} catch (IllegalStateException e) {
				Toast.makeText(
						this,
						"Couldn't authenticate with Dropbox:"
								+ e.getLocalizedMessage(), Toast.LENGTH_SHORT)
						.show();
				Log.i("DBActivity", "Error authenticating", e);
			}
		}

		if (!(loggedIn = dropbox.isLoggedIn())) {
			button.setText("Login");
			text.setText("Please login to DropBox");
		} else {
			button.setText("Logout");
			//Account account = dropbox.getUser();
			text.setText("Signed in to dropbox");

		}

	}

}
