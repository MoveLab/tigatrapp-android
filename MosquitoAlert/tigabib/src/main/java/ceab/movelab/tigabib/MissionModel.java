package ceab.movelab.tigabib;

import android.content.Context;
import android.text.Html;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
	
	public static final int BUTTONACTIONS_GO_TO_URL = 3;
	// This button just goes to the URL but does not mark task as complete

	public static final int BUTTONACTIONS_MARK_COMPLETE = 4;
	// This button marks task as complete

	public static final int PRECONFIRUATION_SITES = 0;
	public static final int PRECONFIRUATION_ADULTS = 1;

	public static JSONObject makeSiteConfirmation(Context context) {

		JSONObject task = new JSONObject();
		JSONObject taskJson = new JSONObject();
		JSONArray items = new JSONArray();
		JSONObject item1 = new JSONObject();
		JSONObject item2 = new JSONObject();
		JSONObject item3 = new JSONObject();
		JSONObject item4 = new JSONObject();

		try {
			task.put(Tasks.KEY_ID, "breedingSiteChecklist");
			task.put(Tasks.KEY_TITLE, "");
			task.put(Tasks.KEY_SHORT_DESCRIPTION, "Checklist for confirming breeding site");
			task.put(Tasks.KEY_CREATION_TIME, -1);
			task.put(Tasks.KEY_EXPIRATION_TIME, -1);

			taskJson.put(KEY_TITLE, context.getResources().getString(R.string.report_checklist_title_site));
			taskJson.put(KEY_PRESET_CONFIGURATION, PRECONFIRUATION_SITES);

			item1.put(MissionItemModel.KEY_ITEM_ID, "Item1");
			item1.put(MissionItemModel.KEY_PREPOSITIONED_IMAGE_REFERENCE, R.drawable.checklist_image_sites_1);
			item1.put(MissionItemModel.KEY_QUESTION, context.getResources()
					.getString(R.string.site_report_q1));
			item1.put(MissionItemModel.KEY_HELP_TEXT, context.getResources().getString(R.string.site_report_item_help_1));
			item1.put(MissionItemModel.KEY_ANSWER_CHOICES, new JSONArray("['"
					+ context.getResources().getString(R.string.site_q1_a1_embornals)
					+ "', '"
					+ context.getResources().getString(R.string.altres) + "']"));

			item2.put(MissionItemModel.KEY_ITEM_ID, "Item2");
			item2.put(MissionItemModel.KEY_PREPOSITIONED_IMAGE_REFERENCE,-1);
			item2.put(MissionItemModel.KEY_QUESTION, context.getResources()
					.getString(R.string.site_report_q2));
			item2.put(MissionItemModel.KEY_HELP_TEXT, context.getResources()
					.getString(R.string.site_report_item_help_2));
			item2.put(MissionItemModel.KEY_ANSWER_CHOICES, new JSONArray("['"
					+ context.getResources().getString(R.string.yes) + "','"
					+ context.getResources().getString(R.string.no) 
					+ "']"));

			item3.put(MissionItemModel.KEY_ITEM_ID, "Item3");
			item3.put(MissionItemModel.KEY_PREPOSITIONED_IMAGE_REFERENCE,

			R.drawable.checklist_image_sites_3);
			item3.put(MissionItemModel.KEY_QUESTION, context.getResources()
					.getString(R.string.site_report_q3));
			item3.put(MissionItemModel.KEY_HELP_TEXT, context.getResources()
					.getString(R.string.site_report_item_help_3));
			item3.put(MissionItemModel.KEY_ANSWER_CHOICES, new JSONArray("['"
					+ context.getResources().getString(R.string.site_report_q3_response1) + "','"
					+ context.getResources().getString(R.string.site_report_q3_response2) + "','"
					+ context.getResources().getString(R.string.site_report_q3_response3) + "']"));

			item4.put(MissionItemModel.KEY_ITEM_ID, "Item4");
			item4.put(MissionItemModel.KEY_PREPOSITIONED_IMAGE_REFERENCE,-1);
			item4.put(MissionItemModel.KEY_QUESTION, context.getResources()
					.getString(R.string.site_report_q4));
			item4.put(MissionItemModel.KEY_HELP_TEXT, context.getResources()
					.getString(R.string.site_report_item_help_4));
			item4.put(MissionItemModel.KEY_ANSWER_CHOICES, new JSONArray("['"
					+ context.getResources().getString(R.string.yes) + "','"
					+ context.getResources().getString(R.string.no)
					+ "']"));

			items.put(item1);
			items.put(item2);
			items.put(item3);
			items.put(item4);

			taskJson.put(KEY_ITEMS, items);

			task.put(Tasks.KEY_TASK_JSON, taskJson);

		} catch (JSONException e) {
			Util.logError(TAG, "error: " + e);
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
			task.put(Tasks.KEY_SHORT_DESCRIPTION, "Checklist for confirming adult mosquito");
			task.put(Tasks.KEY_CREATION_TIME, -1);
			task.put(Tasks.KEY_EXPIRATION_TIME, -1);

			taskJson.put(KEY_TITLE, context.getResources().getString(R.string.report_checklist_title_adult));
			taskJson.put(KEY_PRESET_CONFIGURATION, PRECONFIRUATION_ADULTS);

			item1.put(MissionItemModel.KEY_ITEM_ID, "Item1");
			item1.put(MissionItemModel.KEY_QUESTION, context.getResources().getString(R.string.confirmation_q1_adult_sizecolor));
			item1.put(MissionItemModel.KEY_HELP_TEXT, context.getResources().getString(R.string.q1_sizecolor_text_help));
			item1.put(MissionItemModel.KEY_PREPOSITIONED_IMAGE_REFERENCE, R.drawable.checklist_image_adult_1_eng); // latin is default image
			item1.put(MissionItemModel.KEY_PREPOSITIONED_IMAGE_REFERENCE_CATALAN, R.drawable.checklist_image_adult_1_cat);
			item1.put(MissionItemModel.KEY_PREPOSITIONED_IMAGE_REFERENCE_SPANISH, R.drawable.checklist_image_adult_1_esp);
			item1.put(MissionItemModel.KEY_PREPOSITIONED_IMAGE_REFERENCE_ENGLISH, R.drawable.checklist_image_adult_1_eng);
			item1.put(MissionItemModel.KEY_ANSWER_CHOICES, new JSONArray("['"
					+ context.getResources().getString(R.string.adult_report_q1a1) + "','"
					+ context.getResources().getString(R.string.adult_report_q1a2) + "','"
					+ context.getResources().getString(R.string.adult_report_q1a3) + "','"
					+ context.getResources().getString(R.string.adult_report_q1a4)
					+ "']"));

			item2.put(MissionItemModel.KEY_ITEM_ID, "item2");
			item2.put(MissionItemModel.KEY_QUESTION, context.getResources()
					.getString(R.string.confirmation_q2_adult_headthorax));
			item2.put(MissionItemModel.KEY_HELP_TEXT, context.getResources().getString(R.string.q2_headthorax_text_help));
			item2.put(MissionItemModel.KEY_PREPOSITIONED_IMAGE_REFERENCE, R.drawable.checklist_image_adult_2_eng);	// latin is default image
			item2.put(MissionItemModel.KEY_PREPOSITIONED_IMAGE_REFERENCE_CATALAN, R.drawable.checklist_image_adult_2_cat);
			item2.put(MissionItemModel.KEY_PREPOSITIONED_IMAGE_REFERENCE_SPANISH, R.drawable.checklist_image_adult_2_esp);
			item2.put(MissionItemModel.KEY_PREPOSITIONED_IMAGE_REFERENCE_ENGLISH, R.drawable.checklist_image_adult_2_eng);
			item2.put(MissionItemModel.KEY_ANSWER_CHOICES, new JSONArray("['"
					+ context.getResources().getString(R.string.adult_report_q2a1) + "','"
					+ context.getResources().getString(R.string.adult_report_q2a2) + "','"
					+ context.getResources().getString(R.string.adult_report_q2a3) + "','"
					+ context.getResources().getString(R.string.adult_report_q2a4)
					+ "']"));

			item3.put(MissionItemModel.KEY_ITEM_ID, "item3");
			item3.put(MissionItemModel.KEY_QUESTION, context.getResources()
					.getString(R.string.adult_report_q3));
			item3.put(MissionItemModel.KEY_HELP_TEXT, context.getResources().getString(R.string.q3_abdomenlegs_text));
			item3.put(MissionItemModel.KEY_PREPOSITIONED_IMAGE_REFERENCE, R.drawable.checklist_image_adult_3_eng); // latin is default image
			item3.put(MissionItemModel.KEY_PREPOSITIONED_IMAGE_REFERENCE_CATALAN, R.drawable.checklist_image_adult_3_cat);
			item3.put(MissionItemModel.KEY_PREPOSITIONED_IMAGE_REFERENCE_SPANISH, R.drawable.checklist_image_adult_3_esp);
			item3.put(MissionItemModel.KEY_PREPOSITIONED_IMAGE_REFERENCE_ENGLISH, R.drawable.checklist_image_adult_3_eng);
			item3.put(MissionItemModel.KEY_ANSWER_CHOICES, new JSONArray("['"
					+ Html.fromHtml(context.getResources().getString(R.string.adult_report_q3a1)) + "','"
					+ context.getResources().getString(R.string.adult_report_q3a2) + "','"
					+ context.getResources().getString(R.string.adult_report_q3a3) + "','"
					+ context.getResources().getString(R.string.adult_report_q3a4)
					+ "']"));

			items.put(item1);
			items.put(item2);
			items.put(item3);

			taskJson.put(KEY_ITEMS, items);

			task.put(Tasks.KEY_TASK_JSON, taskJson);

		} catch (JSONException e) {
			Util.logError(TAG, "error: " + e);
		}

		return task;
	}

/*	public static void storeTask(Context context, String task) {
		ContentResolver cr = context.getContentResolver();
		cr.insert(Util.getMissionsUri(context), ContProvValuesMissions.createTask(task));
	}*/

}
