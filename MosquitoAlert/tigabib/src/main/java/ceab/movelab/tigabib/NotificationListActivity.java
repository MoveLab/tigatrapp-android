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

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.TabHost;

import ceab.movelab.tigabib.fragments.FragmentList;
import ceab.movelab.tigabib.model.RealmHelper;
import io.realm.Realm;

// code based on http://android.codeandmagic.org/android-tabs-with-fragments/
public class NotificationListActivity extends FragmentActivity implements TabHost.OnTabChangeListener {

	private static final String TAG = "FragmentTabs";
	public static final String TAB_NEW = "new";
	public static final String TAB_OLD = "old";

	private String lang;

	private Realm mRealm;

	private TabHost mTabHost;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!PropertyHolder.isInit())
			PropertyHolder.init(this);

		lang = Util.setDisplayLanguage(getResources());

		setContentView(R.layout.notifications_list);

		/*viewPager = (ViewPager) findViewById(R.id.viewpager);
		setupViewPager(viewPager);

		tabLayout = (TabLayout) findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(viewPager);*/

		mRealm = RealmHelper.getInstance().getRealm(this);

		mTabHost = (TabHost) findViewById(android.R.id.tabhost);

		mTabHost.setup();
		TabHost.TabSpec tabSpec1 = mTabHost.newTabSpec(TAB_NEW);
		tabSpec1.setIndicator(getString(R.string.notifications_new));
		tabSpec1.setContent(R.id.tab_1);
		mTabHost.addTab(tabSpec1);

		TabHost.TabSpec tabSpec2 = mTabHost.newTabSpec(TAB_OLD);
		tabSpec2.setIndicator(getString(R.string.notifications_old));
		tabSpec2.setContent(R.id.tab_2);
		mTabHost.addTab(tabSpec2);

		//setRetainInstance(true);

		mTabHost.setOnTabChangedListener(this);
		mTabHost.setCurrentTab(0);
	}


	@Override
	public void onTabChanged(String tabId) {
		Util.logInfo(TAG, "onTabChanged(): tabId=" + tabId);
		if ( TAB_NEW.equals(tabId) ) {
			//updateTab(tabId, R.id.tab_1, false);
			//mCurrentTab = 0;
			return;
		}
		if ( TAB_OLD.equals(tabId) ) {
			//updateTab(tabId, R.id.tab_2, true);
			//mCurrentTab = 1;
			return;
		}
	}

	private void updateTab(String tabId, int placeholder, boolean done) {
		Util.logInfo("UPDATE TAB", "isDone = " + done);
		FragmentManager fm = getSupportFragmentManager();
		fm.beginTransaction()
				.replace(placeholder, FragmentList.newInstance(done), tabId)
				.commit();
	}

	@Override
	protected void onResume() {
		if ( !Util.setDisplayLanguage(getResources()).equals(lang) ) {
			finish();
			startActivity(getIntent());
		}
		super.onResume();

		updateTab(TAB_NEW, R.id.tab_1, false);
		updateTab(TAB_OLD, R.id.tab_2, true);
	}

	@Override
	protected void onDestroy() {
		if ( mRealm != null ) mRealm.close(); // Remember to close Realm when done.
		super.onDestroy();
	}

}