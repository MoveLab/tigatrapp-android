package ceab.movelab.tigerapp;

import ceab.movelab.tigabib.ContProvTracks;

public class TigademoTracksCP extends ContProvTracks {

	private static final String AUTHORITY = "ceab.movelab.tigademo.cp.tracks";

	@Override
	protected String getAuthority() {
		return AUTHORITY;
	}

}
