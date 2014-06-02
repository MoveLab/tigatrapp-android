package ceab.movelab.tigabib;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import ceab.movelab.tigabib.R;
import ceab.movelab.tigabib.ContProvContractMissions.Tasks;

public class MissionModel {

	private static String TAG = "TaskModel";
	public static final String KEY_TITLE = "title";
	public static final String KEY_TITLE_CATALAN = "title_catalan";
	public static final String KEY_TITLE_SPANISH = "title_spanish";
	public static final String KEY_TITLE_ENGLISH = "title_english";

	public static final String KEY_LONG_DESCRIPTION = "long_description";
	public static final String KEY_LONG_DESCRIPTION_CATALAN = "long_description_catalan";
	public static final String KEY_LONG_DESCRIPTION_SPANISH = "long_description_spanish";
	public static final String KEY_LONG_DESCRIPTION_ENGLISH = "long_description_english";

	public static final String KEY_HELP_TEXT = "help_text";
	public static final String KEY_HELP_TEXT_CATALAN = "help_text_catalan";
	public static final String KEY_HELP_TEXT_SPANISH = "help_text_spanish";
	public static final String KEY_HELP_TEXT_ENGLISH = "help_text_english";

	public static final String KEY_ITEMS = "items";
	public static final String KEY_PRESET_CONFIGURATION = "preset_configuation";

	// This is the key that will be fed from current api. I will then map it
	// always to left button in TaskActivity
	public static final String KEY_URL = "url";

	// TO DO: implement this in the task activity
	public static final String KEY_PHOTO_MISSION = "photo_mission";

	public static final String KEY_TASK_BUTTON_LEFT_VISIBLE = "mission_button_left_visible";
	public static final String KEY_TASK_BUTTON_MIDDLE_VISIBLE = "mission_button_middle_visible";
	public static final String KEY_TASK_BUTTON_RIGHT_VISIBLE = "mission_button_right_visible";
	public static final String KEY_TASK_BUTTON_LEFT_TEXT = "mission_button_left_text";
	public static final String KEY_TASK_BUTTON_LEFT_ACTION = "mission_button_left_action";
	public static final String KEY_TASK_BUTTON_LEFT_URL = "mission_button_left_url";
	public static final String KEY_TASK_BUTTON_MIDDLE_TEXT = "mission_button_middle_text";
	public static final String KEY_TASK_BUTTON_MIDDLE_ACTION = "mission_button_middle_action";
	public static final String KEY_TASK_BUTTON_MIDDLE_URL = "mission_button_middle_url";
	public static final String KEY_TASK_BUTTON_RIGHT_TEXT = "mission_button_right_text";
	public static final String KEY_TASK_BUTTON_RIGHT_ACTION = "mission_button_right_action";
	public static final String KEY_TASK_BUTTON_RIGHT_URL = "mission_button_right_url";

	public static final String KEY_TASK_TRIGGER_LON_LOWERBOUND = "lon_lower_bound";
	public static final String KEY_TASK_TRIGGER_LON_UPPERBOUND = "lon_upper_bound";
	public static final String KEY_TASK_TRIGGER_LAT_LOWERBOUND = "lat_lower_bound";
	public static final String KEY_TASK_TRIGGER_LAT_UPPERBOUND = "lat_upper_bound";
	public static final String KEY_TASK_TRIGGER_TIME_LOWERBOUND = "time_lowerbound";
	public static final String KEY_TASK_TRIGGER_TIME_UPPERBOUND = "time_upperbound";

	// TODO map these to localize strings when reading task
	public static final int BUTTONTEXT_URL_TASK = 0;
	public static final int BUTTONTEXT_SURVEY_TASK = 1;
	public static final int BUTTONTEXT_MARK_COMPLETE = 2;
	public static final int BUTTONTEXT_DO_TASK_LATER = 3;
	public static final int BUTTONTEXT_DELETE_TASK = 4;

	public static final int BUTTONACTIONS_DO_TASK = 0;
	// This button marks task complete, uploads any responses, goes to url (if
	// there is a url for this button), and removes notification if there are no
	// other pending tasks.
	public static final int BUTTONACTIONS_DO_TASK_LATER = 1;
	// This button dismisses the task but keeps it in the task list and keeps
	// notification up.

	public static final int BUTTONACTIONS_DELETE_TASK = 2;
	// This button deletes the task from phone and removes notification if there
	// are no other pending tasks.

	public static final int PRECONFIRUATION_SITES = 0;
	public static final int PRECONFIRUATION_ADULTS = 1;

	public static JSONObject makeSiteConfirmation(Context context) {

		JSONObject task = new JSONObject();
		JSONObject taskJson = new JSONObject();
		JSONArray items = new JSONArray();
		JSONObject item1 = new JSONObject();
		JSONObject item2 = new JSONObject();
		JSONObject item3 = new JSONObject();

		try {

			task.put(Tasks.KEY_ID, "breedingSiteChecklist");
			task.put(Tasks.KEY_TITLE, "");
			task.put(Tasks.KEY_SHORT_DESCRIPTION,
					"Checklist for confirming breeding site");
			task.put(Tasks.KEY_CREATION_TIME, -1);
			task.put(Tasks.KEY_EXPIRATION_TIME, -1);

			taskJson.put(
					KEY_TITLE,
					context.getResources().getString(
							R.string.report_checklist_title_site));
			taskJson.put(KEY_PRESET_CONFIGURATION, PRECONFIRUATION_SITES);

			item1.put(MissionItemModel.KEY_ITEM_ID, "Item1");
			item1.put(MissionItemModel.KEY_PREPOSITIONED_IMAGE_REFERENCE,
					R.drawable.checklist_image_sites_1);

			item1.put(MissionItemModel.KEY_QUESTION, context.getResources()
					.getString(R.string.site_report_q1));
			item1.put(MissionItemModel.KEY_HELP_TEXT, context.getResources()
					.getString(R.string.site_report_item_help_1));
			item1.put(MissionItemModel.KEY_ANSWER_CHOICES, new JSONArray("['"
					+ context.getResources().getString(R.string.embornals)
					+ "', '" + context.getResources().getString(R.string.fonts)
					+ "', '"
					+ context.getResources().getString(R.string.basses)
					+ "', '"
					+ context.getResources().getString(R.string.bidons)
					+ "', '" + context.getResources().getString(R.string.pous)
					+ "', '"
					+ context.getResources().getString(R.string.altres) + "']"));

			item2.put(MissionItemModel.KEY_ITEM_ID, "Item2");
			item2.put(MissionItemModel.KEY_PREPOSITIONED_IMAGE_REFERENCE,
					R.drawable.checklist_image_sites_2);

			item2.put(MissionItemModel.KEY_QUESTION, context.getResources()
					.getString(R.string.site_report_q2));
			item2.put(MissionItemModel.KEY_HELP_TEXT, context.getResources()
					.getString(R.string.site_report_item_help_2));
			item2.put(MissionItemModel.KEY_ANSWER_CHOICES, new JSONArray("['"
					+ context.getResources().getString(R.string.yes) + "','"
					+ context.getResources().getString(R.string.no) + "']"));

			item3.put(MissionItemModel.KEY_ITEM_ID, "Item3");
			item3.put(MissionItemModel.KEY_PREPOSITIONED_IMAGE_REFERENCE,

			R.drawable.checklist_image_sites_3);
			item3.put(MissionItemModel.KEY_QUESTION, context.getResources()
					.getString(R.string.site_report_q3));
			item3.put(MissionItemModel.KEY_HELP_TEXT, context.getResources()
					.getString(R.string.site_report_item_help_3));
			item3.put(MissionItemModel.KEY_ANSWER_CHOICES, new JSONArray("['"
					+ context.getResources().getString(R.string.yes) + "','"
					+ context.getResources().getString(R.string.no) + "','"
					+ context.getResources().getString(R.string.dontknow)
					+ "']"));

			items.put(item1);
			items.put(item2);
			items.put(item3);

			taskJson.put(KEY_ITEMS, items);

			task.put(Tasks.KEY_TASK_JSON, taskJson);

		} catch (JSONException e) {
			Util.logError(context, TAG, "error: " + e);
		}

		return task;
	}

	public static JSONObject makeAdultConfirmation(Context context) {
		JSONObject task = new JSONObject();
		JSONObject taskJson = new JSONObject();
		JSONArray items = new JSONArray();
		JSONObject item1 = new JSONObject();
		JSONObject item2 = new JSONObject();
		JSONObject item3 = new JSONObject();
		try {
			task.put(Tasks.KEY_ID, "adultChecklist");
			task.put(Tasks.KEY_TITLE, "");
			task.put(Tasks.KEY_SHORT_DESCRIPTION,
					"Checklist for confirming adult mosquito");
			task.put(Tasks.KEY_CREATION_TIME, -1);
			task.put(Tasks.KEY_EXPIRATION_TIME, -1);

			taskJson.put(
					KEY_TITLE,
					context.getResources().getString(
							R.string.report_checklist_title_adult));
			taskJson.put(KEY_PRESET_CONFIGURATION, PRECONFIRUATION_ADULTS);

			item1.put(MissionItemModel.KEY_ITEM_ID, "Item1");
			item1.put(MissionItemModel.KEY_QUESTION, context.getResources()
					.getString(R.string.confirmation_q1_adult_sizecolor));
			item1.put(MissionItemModel.KEY_HELP_TEXT, context.getResources()
					.getString(R.string.q1_sizecolor_text));
			item1.put(MissionItemModel.KEY_PREPOSITIONED_IMAGE_REFERENCE,
					R.drawable.checklist_image_adult_1);
			item1.put(MissionItemModel.KEY_ANSWER_CHOICES, new JSONArray("['"
					+ context.getResources().getString(R.string.yes) + "','"
					+ context.getResources().getString(R.string.no) + "','"
					+ context.getResources().getString(R.string.dontknow)
					+ "']"));

			item2.put(MissionItemModel.KEY_ITEM_ID, "item2");
			item2.put(MissionItemModel.KEY_QUESTION, context.getResources()
					.getString(R.string.confirmation_q2_adult_headthorax));
			item2.put(MissionItemModel.KEY_HELP_TEXT, context.getResources()
					.getString(R.string.q3_headthorax_text));
			item2.put(MissionItemModel.KEY_PREPOSITIONED_IMAGE_REFERENCE,
					R.drawable.checklist_image_adult_2);
			item2.put(MissionItemModel.KEY_ANSWER_CHOICES, new JSONArray("['"
					+ context.getResources().getString(R.string.yes) + "','"
					+ context.getResources().getString(R.string.no) + "','"
					+ context.getResources().getString(R.string.dontknow)
					+ "']"));

			item3.put(MissionItemModel.KEY_ITEM_ID, "item3");
			item3.put(MissionItemModel.KEY_QUESTION, context.getResources()
					.getString(R.string.adult_report_q3));
			item3.put(MissionItemModel.KEY_HELP_TEXT, context.getResources()
					.getString(R.string.q2_abdomenlegs_text));
			item3.put(MissionItemModel.KEY_PREPOSITIONED_IMAGE_REFERENCE,
					R.drawable.checklist_image_adult_3);
			item3.put(MissionItemModel.KEY_ANSWER_CHOICES, new JSONArray("['"
					+ context.getResources().getString(R.string.yes) + "','"
					+ context.getResources().getString(R.string.no) + "','"
					+ context.getResources().getString(R.string.dontknow)
					+ "']"));

			items.put(item1);
			items.put(item2);
			items.put(item3);

			taskJson.put(KEY_ITEMS, items);

			task.put(Tasks.KEY_TASK_JSON, taskJson);

		} catch (JSONException e) {
			Util.logError(context, TAG, "error: " + e);
		}

		return task;
	}

	public static void storeTask(Context context, String task) {

		ContentResolver cr = context.getContentResolver();
		cr.insert(Util.getMissionsUri(context),
				ContProvValuesMissions.createTask(task));

	}

}
