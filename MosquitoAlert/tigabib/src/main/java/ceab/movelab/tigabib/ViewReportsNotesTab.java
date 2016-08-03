package ceab.movelab.tigabib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;

import ceab.movelab.tigabib.ContProvContractReports.Reports;

public class ViewReportsNotesTab extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Context context = this;
		Resources res = getResources();
		Util.setDisplayLanguage(res);

		TextView textview = new TextView(this);
		textview.setText(getResources().getString(R.string.no_note_attached));
		setContentView(textview);

		Intent incoming = getIntent();
		Bundle b = incoming.getExtras();

		if (b != null && b.containsKey(Reports.KEY_NOTE)) {

			textview.setText(b.getString(Reports.KEY_NOTE));

		}
	}
}