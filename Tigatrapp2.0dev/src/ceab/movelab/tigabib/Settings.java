package ceab.movelab.tigabib;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import ceab.movelab.tigabib.R;

public class Settings extends Activity {

	private static String TAG = "Settings";

	final Context context = this;
	Resources res;
	String lang;

	ToggleButton tb;
	Boolean on;
	TextView tv;

	LinearLayout debugView;
	TextView sampleView;
	Button fixButton;

	Button syncButton;
	Button languageButton;

	NewSamplesReceiver newSamplesReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!PropertyHolder.isInit())
			PropertyHolder.init(context);

		res = getResources();
		lang = Util.setDisplayLanguage(res);

		setContentView(R.layout.settings);

		on = PropertyHolder.isServiceOn();

		languageButton = (Button) findViewById(R.id.languageButton);
		languageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent i = new Intent(Settings.this, LanguageSelector.class);
				startActivity(i);

			}

		});

		syncButton = (Button) findViewById(R.id.syncButton);
		syncButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Util.logInfo(context, TAG, "sync button clicked");
				Util.internalBroadcast(context, Messages.START_DAILY_SYNC);
				Util.toast(context,
						getResources().getString(R.string.settings_syncing));

			}

		});

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
						Util.internalBroadcast(context,
								Messages.START_DAILY_SAMPLING);
					}
				} else {
					Util.internalBroadcast(context,
							Messages.STOP_DAILY_SAMPLING);
					// I am setting this here even though it is also set by the
					// broadcast receiver because in the demo version the
					// broadcast receiver never makes it to that stage
					PropertyHolder.setServiceOn(false);
				}

			}

		});

		debugView = (LinearLayout) findViewById(R.id.debugView);
		if (Util.debugMode(context)) {
			debugView.setVisibility(View.VISIBLE);
			sampleView = (TextView) findViewById(R.id.sampleView);
			sampleView.setText(PropertyHolder.getCurrentFixTimes());
			fixButton = (Button) findViewById(R.id.fixButton);
			fixButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Util.internalBroadcast(context, Messages.START_TASK_FIX);
				}
			});
		} else
			debugView.setVisibility(View.GONE);

	}

	@Override
	protected void onResume() {

		if (!Util.setDisplayLanguage(res).equals(lang)) {
			finish();
			startActivity(getIntent());
		}

		IntentFilter newSamplesFilter;
		newSamplesFilter = new IntentFilter(
				Messages.newSamplesReadyAction(context));
		newSamplesReceiver = new NewSamplesReceiver();
		registerReceiver(newSamplesReceiver, newSamplesFilter);

		super.onResume();

	}

	@Override
	protected void onPause() {
		try {
			unregisterReceiver(newSamplesReceiver);
		} catch (Exception e) {
			Util.logError(context, TAG,
					"error unregistering newSamplesReceiver");
		}
		super.onResume();

	}

	public class NewSamplesReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			sampleView.setText(PropertyHolder.getCurrentFixTimes());

		}

	}

}