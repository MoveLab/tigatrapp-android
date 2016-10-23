package ceab.movelab.tigabib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import ceab.movelab.tigabib.ContProvContractReports.Reports;
import ceab.movelab.tigabib.adapters.ViewResponsesAdapter;

public class ViewReportsChecklistTab extends Activity {

	private static String TAG = "ViewReportsCheckListTab";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Context context = this;

		Util.setDisplayLanguage(getResources());

		ArrayList<MissionItemModel> taskData = new ArrayList<>();
		setContentView(R.layout.view_responses_list);

		ListView lv = (ListView) findViewById(R.id.listview);

		Intent incoming = getIntent();
		Bundle b = incoming.getExtras();

		if (b != null && b.containsKey(Reports.KEY_CONFIRMATION)) {

			JSONObject responses;
			try {
				responses = new JSONObject(b.getString(Reports.KEY_CONFIRMATION));

				Iterator<String> iter = responses.keys();
				while (iter.hasNext()) {
					String key = iter.next();
					try {
						JSONObject thisItem = responses.getJSONObject(key);
						taskData.add(new MissionItemModel(context, thisItem));
					} catch (JSONException e) {
						Util.logError(TAG, "error: " + e);
					}
				}
			} catch (JSONException e1) {
				Util.logError(TAG, "error: " + e1);
			}

			if (taskData != null)
				Collections.sort(taskData, new CustomComparator());

			ViewResponsesAdapter vra = new ViewResponsesAdapter(this, R.layout.view_responses_item, taskData);
			lv.setAdapter(vra);

			setContentView(lv);
		}
	}

	public class CustomComparator implements Comparator<MissionItemModel> {
		@Override
		public int compare(MissionItemModel o1, MissionItemModel o2) {
			int result = -1;
			String id1 = o1.getItemId();
			String id2 = o2.getItemId();
			if (id1 != null && id2 != null)
				result = id1.compareTo(id2);
			return result;
		}
	}

}