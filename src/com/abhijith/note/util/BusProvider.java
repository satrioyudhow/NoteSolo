package com.abhijith.note.util;

import com.squareup.otto.Bus;

public final class BusProvider {
	public static final Bus BUS = new Bus();

	public static Bus getInstance() {
		return BUS;

	}

	public BusProvider() {
		// Nothing to see here...
	}

}
