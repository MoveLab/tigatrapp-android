package ceab.movlab.tigerapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;
import ceab.movelab.tigerapp.R;

public class Settings extends Activity {
	final Context context = this;
	Resources res;
	ToggleButton tb;
	Boolean on;
	TextView tv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!PropertyHolder.isInit())
			PropertyHolder.init(context);

		res = getResources();
		Util.setDisplayLanguage(res);

		setContentView(R.layout.settings);

		on = PropertyHolder.isServiceOn();

		tb = (ToggleButton) findViewById(R.id.service_button);
		tv = (TextView) findViewById(R.id.service_message);

		tb.setChecked(on);
		tv.setText(on ? getResources().getString(R.string.sampling_is_on)
				: getResources().getString(R.string.sampling_is_off));
		tb.setOnClickListener(new ToggleButton.OnClickListener() {
			public void onClick(View view) {
				on = !on;
				tb.setChecked(on);
				if (on) {
					long lastScheduleTime = PropertyHolder
							.lastSampleSchedleMade();
					if (System.currentTimeMillis() - lastScheduleTime > 1000 * 60 * 60 * 24) {
						Intent scheduleService = new Intent(
								TigerBroadcastReceiver.START_SAMPLING_MESSAGE);
						sendBroadcast(scheduleService);
					}
				} else {
					Intent scheduleService = new Intent(
							TigerBroadcastReceiver.STOP_SAMPLING_MESSAGE);
					sendBroadcast(scheduleService);

				}

			}

		});

	}
}