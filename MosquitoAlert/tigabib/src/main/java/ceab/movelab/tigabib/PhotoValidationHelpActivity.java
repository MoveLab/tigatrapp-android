/*
 * Tigatrapp
 * Copyright (C) 2013, 2014  John R.B. Palmer, Aitana Oltra, Joan Garriga, and Frederic Bartumeus 
 * Contact: info@atrapaeltigre.com
 * 
 * This file is part of Tigatrapp.
 * 
 * Tigatrapp is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or (at 
 * your option) any later version.
 * 
 * Tigatrapp is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along 
 * with Tigatrapp.  If not, see <http://www.gnu.org/licenses/>.
 **/

package ceab.movelab.tigabib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ViewFlipper;

/**
 * Displays the Pybossa photo validation help screen.
 * 
 * @author Màrius Garcia
 * 
 */
public class PhotoValidationHelpActivity extends Activity {

	private ViewFlipper mHelpViewflipper;
	private Button mCloseButton;
	private Button mHelp21Button;
	private Button mHelp22Button;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent myIntent = getIntent(); // gets the previously created intent
		if ( myIntent == null ) this.finish();

		int helpValue = myIntent.getIntExtra(PhotoValidationActivity.HELP_PARAM, 0);
		if ( helpValue == 0 ) this.finish();

		switch ( helpValue ) {
			case 1: setContentView(R.layout.valid_1_help_layout);
				break;
			case 2: setContentView(R.layout.valid_2_help_layout);
				mHelpViewflipper = (ViewFlipper) findViewById(R.id.viewflipper);
				mHelp21Button = (Button) findViewById(R.id.validHelp21Button);
				mHelp22Button = (Button) findViewById(R.id.validHelp22Button);
				setHelp2OnClickListeners();
				break;
		}
		overridePendingTransition(R.anim.help_go_in, R.anim.help_go_out);

		mCloseButton = (Button) findViewById(R.id.validHelpCloseButton);
		setOnClickListeners();
	}

	private void setOnClickListeners() {
		mCloseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finishMyHelpActivity();
			}
		});
	}

	private void setHelp2OnClickListeners() {
		mHelp21Button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showFlipperNext();
			}
		});
		mHelp22Button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showFlipperNext();
			}
		});
	}


	private void showFlipperNext() {
		// Next screen comes in from right.
		mHelpViewflipper.setInAnimation(this, R.anim.slide_in_from_right);
		// Current screen goes out from left.
		mHelpViewflipper.setOutAnimation(this, R.anim.slide_out_to_left);
		mHelpViewflipper.showNext();
	}
	private void showFlipperPrev() {
		// Next screen comes in from left.
		mHelpViewflipper.setInAnimation(this, R.anim.slide_in_from_left);
		// Current screen goes out from right.
		mHelpViewflipper.setOutAnimation(this, R.anim.slide_out_to_right);
		mHelpViewflipper.showPrevious();
	}

	@Override
	public void onBackPressed() {
		if ( mHelpViewflipper != null && mHelpViewflipper.getDisplayedChild() > 0 )
			showFlipperPrev();
		else
			finishMyHelpActivity();
	}

	public void finishMyHelpActivity() {
		this.finish();
		overridePendingTransition(R.anim.help_back_in, R.anim.help_back_out);
	}

}
