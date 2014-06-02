package ceab.movelab.tigerapp;

import ceab.movelab.tigabib.ContProvMissions;

public class TigademoMissionsCP extends ContProvMissions {

	private static final String AUTHORITY = "ceab.movelab.tigademo.cp.missions";

	@Override
	protected String getAuthority() {
		return AUTHORITY;
	}

}
