package ceab.movelab.tigerapp;

import ceab.movelab.tigabib.ContProvReports;

public class TigademoReportsCP extends ContProvReports {

	private static final String AUTHORITY = "ceab.movelab.tigademo.cp.reports";

	@Override
	protected String getAuthority() {
		return AUTHORITY;
	}

}
