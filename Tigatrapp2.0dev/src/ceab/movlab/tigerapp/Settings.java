package ceab.movlab.tigerapp;

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
import android.widget.TextView;
import android.widget.ToggleButton;
import ceab.movelab.tigerapp.R;

public class Settings extends Activity {

	private static String TAG = "Settings";

	final Context context = this;
	Resources res;
	String lang;

	ToggleButton tb;
	Boolean on;
	TextView tv;

	TextView sampleView;

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
				}

			}

		});

		if (Util.debugMode(context)) {
			sampleView = (TextView) findViewById(R.id.sampleView);
			sampleView.setVisibility(View.VISIBLE);
			sampleView.setText(PropertyHolder.getCurrentFixTimes());
		}
	}

	@Override
	protected void onResume() {

		if (!Util.setDisplayLanguage(res).equals(lang)) {
			finish();
			startActivity(getIntent());
		}

		IntentFilter newSamplesFilter;
		newSamplesFilter = new IntentFilter(Messages.newSamplesReadyAction(context));
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