package ceab.movlab.tigre;

import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ListView;
import android.widget.TextView;

public class TaskActivity extends Activity {
	Context context = this;
	String lang;
	ListView lv;
	TextView taskTitle;
	TextView taskDetail;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		lang = PropertyHolder.getLanguage();
		Locale myLocale = new Locale(lang);
		Resources res = getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = myLocale;
		res.updateConfiguration(conf, dm);

		setContentView(R.layout.survey);
		Util.overrideFonts(this, findViewById(android.R.id.content));

		taskTitle = (TextView) findViewById(R.id.taskTitle);
		taskDetail = (TextView) findViewById(R.id.taskDetail);

		lv = (ListView) findViewById(R.id.listview);

		ArrayList<TaskItemModel> list = new ArrayList<TaskItemModel>();

		try {

			JSONObject thisTask = TaskModel.makeSampleTask();
			taskTitle.setText(thisTask.getString(TaskModel.KEY_TASK_TITLE));
			taskDetail.setText(thisTask.getString(TaskModel.KEY_TASK_DETAIL));

			JSONArray theseItems = thisTask
					.getJSONArray(TaskModel.KEY_TASK_ITEMS);
			for (int i = 0; i < theseItems.length(); i++) {
				list.add(new TaskItemModel(theseItems.getJSONObject(i)));
			}

			TaskAdapter adapter = new TaskAdapter(this, list, res);
			lv.setAdapter(adapter);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
