/*
 * Tigatrapp
 * Copyright (C) 2012, 2013 John R.B. Palmer
 * Contact: jrpalmer@princeton.edu
 * 
 * This file is part of Space Mapper.
 * 
 * Space Mapper is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or (at 
 * your option) any later version.
 * 
 * Space Mapper is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along 
 * with Space Mapper.  If not, see <http://www.gnu.org/licenses/>.
 */

package ceab.movlab.tigerapp;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import ceab.movelab.tigerapp.R;

/**
 * Displays the IRB consent form and allows users to consent or decline.
 * 
 * @author John R.B. Palmer
 * 
 */
public class Consent extends Activity {

	Context context;
	Resources res;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = getApplicationContext();

		if (!PropertyHolder.isInit())
			PropertyHolder.init(context);		
		if (PropertyHolder.getLanguage() == null) {
			Intent i2sb = new Intent(Consent.this, Switchboard.class);
			startActivity(i2sb);
			finish();
		} else {
			res = getResources();
			Util.setDisplayLanguage(res);
		}

		setContentView(R.layout.consent);

		TextView consent = (TextView) findViewById(R.id.consenttext);
		consent.setText(Html.fromHtml(getString(R.string.consent_html), null, new TigaTagHandler()));
		consent.setTextColor(Color.WHITE);
		consent.setTextSize(getResources()
				.getDimension(R.dimen.textsize_normal));

		final Button consentButton = (Button) findViewById(R.id.consent_button);
		consentButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Date now = new Date(System.currentTimeMillis());
				String consentTime = Util.userDate(now);
				PropertyHolder.setConsentTime(consentTime);
				PropertyHolder.setConsent(true);

				Intent i = new Intent(Consent.this, Switchboard.class);
				startActivity(i);
				finish();
				return;
			}
		});

		final Button consentDeclineButton = (Button) findViewById(R.id.consent_decline_button);
		consentDeclineButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
				return;
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		res = getResources();
		Util.setDisplayLanguage(res);
	}

	
}
