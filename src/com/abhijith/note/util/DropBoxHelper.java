package com.abhijith.note.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Account;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;

public class DropBoxHelper {

	// Use sharedprefs you dum'ass!!!
	final static private String APP_KEY = "ci40o20vlir9lzb";
	final static private String APP_SECRET = "qszoj0o9gananqu";

	final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;

	final static private String DROPBOX_ACCOUNT_PREFS_NAME = "prefs";
	final static private String DROPBOX_ACCESS_KEY_NAME = "DROPBOX_ACCESS_KEY";
	final static private String DROPBOX_ACCESS_SECRET_NAME = "DROPBOX_ACCESS_SECRET";

	private final String PHOTO_DIR = "/NoteWorthy/";

	DropboxAPI<AndroidAuthSession> mApi;
	private Context mContext;

	private boolean mLoggedIn;

	public DropBoxHelper(Context context) {
		mContext = context;
		AndroidAuthSession session = buildSession();
		mApi = new DropboxAPI<AndroidAuthSession>(session);
		setLoggedIn(mApi.getSession().isLinked());
	}
	
	

	public DropboxAPI<AndroidAuthSession> getApi() {
		return mApi;
	}



	public void setApi(DropboxAPI<AndroidAuthSession> mApi) {
		this.mApi = mApi;
	}



	private AndroidAuthSession buildSession() {
		AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session;

		String[] stored = getKeys();
		if (stored != null) {
			AccessTokenPair accessToken = new AccessTokenPair(stored[0],
					stored[1]);
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE,
					accessToken);
		} else {
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
		}

		return session;
	}

	public void login() {
		if (!mLoggedIn)
			mApi.getSession().startAuthentication(mContext);
	}

	/**
	 * Shows keeping the access keys returned from Trusted Authenticator in a
	 * local store, rather than storing user name & password, and
	 * re-authenticating each time (which is not to be done, ever).
	 * 
	 * @return Array of [access_key, access_secret], or null if none stored
	 */
	private String[] getKeys() {
		SharedPreferences prefs = mContext.getSharedPreferences(
				DROPBOX_ACCOUNT_PREFS_NAME, 0);
		String key = prefs.getString(DROPBOX_ACCESS_KEY_NAME, null);
		String secret = prefs.getString(DROPBOX_ACCESS_SECRET_NAME, null);
		if (key != null && secret != null) {
			String[] ret = new String[2];
			ret[0] = key;
			ret[1] = secret;
			return ret;
		} else {
			return null;
		}
	}

	public void logOut() {
		// Remove credentials from the session
		mApi.getSession().unlink();

		// Clear our stored keys
		clearKeys();
		// Change UI state to display logged out version
		setLoggedIn(false);
	}

	/**
	 * Convenience function to change UI state based on being logged in
	 */
	public void setLoggedIn(boolean loggedIn) {
		mLoggedIn = loggedIn;
	}

	public boolean isLoggedIn() {
		return mLoggedIn;
	}

	/**
	 * Shows keeping the access keys returned from Trusted Authenticator in a
	 * local store, rather than storing user name & password, and
	 * re-authenticating each time (which is not to be done, ever).
	 */
	public void storeKeys(String key, String secret) {
		// Save the access key for later
		SharedPreferences prefs = mContext.getSharedPreferences(
				DROPBOX_ACCOUNT_PREFS_NAME, 0);
		Editor edit = prefs.edit();
		edit.putString(DROPBOX_ACCESS_KEY_NAME, key);
		edit.putString(DROPBOX_ACCESS_SECRET_NAME, secret);
		edit.commit();
	}

	private void clearKeys() {
		SharedPreferences prefs = mContext.getSharedPreferences(
				DROPBOX_ACCOUNT_PREFS_NAME, 0);
		Editor edit = prefs.edit();
		edit.clear();
		edit.commit();
	}

	public AndroidAuthSession getSession() {
		return mApi.getSession();
	}

	public void finishAuthentication() {
		AndroidAuthSession session = mApi.getSession();
		TokenPair tokens = session.getAccessTokenPair();
        storeKeys(tokens.key, tokens.secret);
        setLoggedIn(true);		
	}
	
	public Account getUser(){
		if(mLoggedIn){
			try {
				return mApi.accountInfo();
			} catch (DropboxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

}
