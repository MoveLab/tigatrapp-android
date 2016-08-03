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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ceab.movelab.tigabib.adapters.NotificationAdapter;
import ceab.movelab.tigabib.model.Notification;
import ceab.movelab.tigabib.model.RealmHelper;

public class NotificationListActivity extends FragmentActivity {

	private ListView listView;
	private NotificationAdapter adapter;

	private ArrayList<Notification> notifData = new ArrayList<>();

	String lang;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!PropertyHolder.isInit())
			PropertyHolder.init(this);

		lang = Util.setDisplayLanguage(getResources());

		setContentView(R.layout.notifications_list);

		listView = (ListView) findViewById(R.id.listview);
		adapter = new NotificationAdapter(this, R.layout.notifications_list, notifData);
		listView.setAdapter(adapter);

		listView.setFastScrollEnabled(true);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// When clicked, show a toast with the TextView text
				Toast.makeText(getApplicationContext(), notifData.get(position).getId(), Toast.LENGTH_SHORT).show();
			}
		});

		loadNotifications();
	}

	@Override
	protected void onResume() {
		if (!Util.setDisplayLanguage(getResources()).equals(lang)) {
			finish();
			startActivity(getIntent());
		}

		super.onResume();
	}

	private void loadNotifications() {
		RealmHelper.getInstance().getRealm();
		List<Notification> notificationList = RealmHelper.getInstance().getAllNotifications();
		for (Notification notif : notificationList )
			notifData.add(notif);

		adapter.notifyDataSetChanged();
	}



}