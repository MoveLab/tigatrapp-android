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

package ceab.movelab.tigabib;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioGroup;

/**
 * Displays the IRB consent form and allows users to consent or decline.
 * 
 * @author John R.B. Palmer
 * 
 */
public class LanguageSelectorActivity extends Activity {


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.language_selector);

		final RadioGroup languageRadioGroup = (RadioGroup) findViewById(R.id.languageRadioGroup);

		String currentLang = PropertyHolder.getLanguage();
		if ( !TextUtils.isEmpty(currentLang)) {
		switch ( currentLang ) {
			case "ca":
				languageRadioGroup.check(R.id.caButton);
				break;
			case "es":
				languageRadioGroup.check(R.id.esButton);
				break;
			case "en":
				languageRadioGroup.check(R.id.enButton);
				break;
			case "zh":
				languageRadioGroup.check(R.id.zhButton);
				break;
			}
		}

		Button positive = (Button) findViewById(R.id.languageOK);
		positive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Obtain the FirebaseAnalytics instance.
				//FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(LanguageSelectorActivity.this);

				if (languageRadioGroup.getCheckedRadioButtonId() == R.id.caButton) {
					PropertyHolder.setLanguage("ca");
					// [START user_property]
					//mFirebaseAnalytics.setUserProperty("ma_language", "Catalan");
					// [END user_property]
					finish();
				} else if (languageRadioGroup.getCheckedRadioButtonId() == R.id.esButton) {
					PropertyHolder.setLanguage("es");
					// [START user_property]
					//mFirebaseAnalytics.setUserProperty("ma_language", "Spanish");
					// [END user_property]
					finish();
				} else if (languageRadioGroup.getCheckedRadioButtonId() == R.id.enButton) {
					PropertyHolder.setLanguage("en");
					// [START user_property]
					//mFirebaseAnalytics.setUserProperty("ma_language", "English");
					// [END user_property]
					finish();
				} else if (languageRadioGroup.getCheckedRadioButtonId() == R.id.zhButton) {
					PropertyHolder.setLanguage("zh");
					// [START user_property]
					//mFirebaseAnalytics.setUserProperty("ma_language", "Chinese");
					// [END user_property]
					finish();
				/*} else {
					// do nothing*/
				}
			}
		});

	}

}
