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
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import ceab.movelab.tigabib.ui.ViewPagerCustomDuration;

/**
 * Displays the guide for the app usage
 * 
 * @author Màrius Garcia
 *
 * https://www.androidhive.info/2016/05/android-build-intro-slider-app/
 * 
 */
public class TutorialActivity extends Activity {

	private String lang;
	private ViewFlipper mViewFlipper;

	private ViewPagerCustomDuration viewPager;
	private MyViewPagerAdapter myViewPagerAdapter;
	private LinearLayout dotsLayout;
	private TextView[] dots;
	private int[] layouts;
	private ImageView imgPrev, imgNext;

	private Button mStartButton, mEndButton;
	private TextView mSkipText1, mSkipText2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if ( !PropertyHolder.isInit() )
			PropertyHolder.init(this);

		lang = Util.setDisplayLanguage(getResources());

		setContentView(R.layout.tutorial_home_layout);

		mViewFlipper = findViewById(R.id.turorialViewFlipper);
		mStartButton = findViewById(R.id.tutorialStartBtn);
		mEndButton = findViewById(R.id.tutorialEndBtn);
		mSkipText1 = findViewById(R.id.skipText1);
		mSkipText2 = findViewById(R.id.skipText2);

		mStartButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showNext();
			}
		});
		mSkipText1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TutorialActivity.this.finish();
			}
		});
		mSkipText2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TutorialActivity.this.finish();
			}
		});
		mEndButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TutorialActivity.this.finish();
			}
		});

// layouts of all welcome sliders
		// add few more layouts if you want
		layouts = new int[]{
				R.layout.tutorial_2_1_layout,
				R.layout.tutorial_2_2_layout,
				R.layout.tutorial_2_3_layout,
				R.layout.tutorial_2_1_layout,
				R.layout.tutorial_2_1_layout,
				R.layout.tutorial_2_1_layout};
		viewPager = findViewById(R.id.tutorialViewPager);
		dotsLayout =  findViewById(R.id.layoutDots);
		// adding bottom dots
		updateBottomDotsLayout(0);

		// making notification bar transparent
		changeStatusBarColor();

		myViewPagerAdapter = new MyViewPagerAdapter();
		viewPager.setAdapter(myViewPagerAdapter);
		viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
		//viewPager.setPageTransformer(true, new DrawFromBackTransformer());

/*		// adding bottom dots
		updateBottomDotsLayout(0);

		// making notification bar transparent
		changeStatusBarColor();*/

		imgPrev = findViewById(R.id.tutorialPrevImageView);
		imgNext = findViewById(R.id.tutorialNextImageView);
		imgPrev.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// checking for first page
				int current = getItem(0);
				if ( current > 0 ) {
					// move to next screen
					viewPager.setCurrentItem(current-1);
				} else {
					showPrev();
				}
			}
		});
		imgNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// checking for last page
				// if last page home screen will be launched
				int current = getItem(+1);
				if ( current < layouts.length ) {
					// move to next screen
					viewPager.setCurrentItem(current);
				} else {
					showNext();
				}
			}
		});

		PropertyHolder.setTutorial(true); // Mark tutorial as seen at least for the first time
	}

	@Override
	public void onBackPressed() {
		if ( mViewFlipper.getCurrentView().getId() == R.id.vfLayout1 )
			super.onBackPressed();
		else {
			int current = getItem(0);
			if ( mViewFlipper.getCurrentView().getId() == R.id.vfLayout2 && current > 0 )
				viewPager.setCurrentItem(current - 1);
			else
				showPrev();
		}
	}

	private void showNext() {
		// Next screen comes in from right.
		mViewFlipper.setInAnimation(this, R.anim.slide_in_from_right);
		// Current screen goes out from left.
		mViewFlipper.setOutAnimation(this, R.anim.slide_out_to_left);
		mViewFlipper.showNext();
	}

	private void showPrev() {
		// Next screen comes in from left.
		mViewFlipper.setInAnimation(this, R.anim.slide_in_from_left);
		// Current screen goes out from right.
		mViewFlipper.setOutAnimation(this, R.anim.slide_out_to_right);
		mViewFlipper.showPrevious();
	}


	@Override
	protected void onResume() {
		super.onResume();
		if ( !Util.setDisplayLanguage(getResources()).equals(lang) ) {
			finish();
			startActivity(getIntent());
		}
	}

	private void updateBottomDotsLayout(int currentPage) {
		dotsLayout.removeAllViews();

		dots = new TextView[layouts.length];
		for ( int i = 0; i < dots.length; i++ ) {
			dots[i] = new TextView(this);
			dots[i].setText(Html.fromHtml("&#8226;"));
			dots[i].setPadding(6,0,6,0);
			dots[i].setTextSize(50);
			dots[i].setTextColor(getResources().getColor(R.color.dot_light));
			dotsLayout.addView(dots[i]);
		}

		if ( dots.length > 0 )
			dots[currentPage].setTextColor(getResources().getColor(R.color.dot_dark));
	}

	private int getItem(int i) {
		return viewPager.getCurrentItem() + i;
	}

	//  viewpager change listener
	ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

		@Override
		public void onPageSelected(int position) {
			updateBottomDotsLayout(position);

			// changing the next button text 'NEXT' / 'GOT IT'
//			if (position == layouts.length - 1) {
//				// last page. make button text to GOT IT
//				//btnNext.setText(getString(R.string.start));
//				//btnSkip.setVisibility(View.GONE);
//			} else {
//				// still pages are left
//				//btnNext.setText(getString(R.string.next));
//				//btnSkip.setVisibility(View.VISIBLE);
//			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};

	private void changeStatusBarColor() {
		if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.TRANSPARENT);
		}
	}

	/**
	 * View pager adapter
	 */
	public class MyViewPagerAdapter extends PagerAdapter {
		private LayoutInflater layoutInflater;

		private MyViewPagerAdapter() { }

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View view = layoutInflater.inflate(layouts[position], container, false);
			container.addView(view);

			return view;
		}

		@Override
		public int getCount() {
			return layouts.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return view == obj;
		}


		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			View view = (View) object;
			container.removeView(view);
		}
	}
}
