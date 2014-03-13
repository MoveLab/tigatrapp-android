package ceab.movlab.tigerapp;

import ceab.movlab.tigerapp.ContentProviderContractReports.Reports;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ViewReportsNotesTab extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TextView textview = new TextView(this);
		textview.setText("No note attached.");
		setContentView(textview);

		Intent incoming = getIntent();
		Bundle b = incoming.getExtras();

		if (b != null && b.containsKey(Reports.KEY_NOTE)) {

			textview.setText(b.getString(Reports.KEY_NOTE));

		}
	}
}