package com.abhijith.note.model;

import android.content.Context;

import com.turbomanage.storm.DatabaseHelper;
import com.turbomanage.storm.api.Database;
import com.turbomanage.storm.api.DatabaseFactory;

@Database(name = "noteDb", version = 1)
public class NoteDatabaseHelper extends DatabaseHelper {

	public NoteDatabaseHelper(Context ctx, DatabaseFactory dbFactory) {
		super(ctx, dbFactory);
	}

	@Override
	protected UpgradeStrategy getUpgradeStrategy() {
		return UpgradeStrategy.DROP_CREATE;
	}

}
