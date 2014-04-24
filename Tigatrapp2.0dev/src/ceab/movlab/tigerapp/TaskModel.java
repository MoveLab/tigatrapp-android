package ceab.movlab.tigerapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import ceab.movelab.tigerapp.R;
import ceab.movlab.tigerapp.ContentProviderContractTasks.Tasks;

public class TaskModel {

	public static final String KEY_TASK_TITLE = "task_title";
	public static final String KEY_TASK_DETAIL = "task_detail";
	public static final String KEY_TASK_HELP = "task_help";
	public static final String KEY_TASK_ITEMS = "task_items";
	public static final String KEY_TASK_PRESET_CONFIGURATION = "task_preset_configuation";

	public static final String KEY_TASK_BUTTON_LEFT_VISIBLE = "task_button_left_visible";
	public static final String KEY_TASK_BUTTON_MIDDLE_VISIBLE = "task_button_middle_visible";
	public static final String KEY_TASK_BUTTON_RIGHT_VISIBLE = "task_button_right_visible";
	public static final String KEY_TASK_BUTTON_LEFT_TEXT = "task_button_left_text";
	public static final String KEY_TASK_BUTTON_LEFT_ACTION = "task_button_left_action";
	public static final String KEY_TASK_BUTTON_LEFT_URL = "task_button_left_url";
	public static final String KEY_TASK_BUTTON_MIDDLE_TEXT = "task_button_middle_text";
	public static final String KEY_TASK_BUTTON_MIDDLE_ACTION = "task_button_middle_action";
	public static final String KEY_TASK_BUTTON_MIDDLE_URL = "task_button_middle_url";
	public static final String KEY_TASK_BUTTON_RIGHT_TEXT = "task_button_right_text";
	public static final String KEY_TASK_BUTTON_RIGHT_ACTION = "task_button_right_action";
	public static final String KEY_TASK_BUTTON_RIGHT_URL = "task_button_right_url";

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

	public static JSONObject makeDemoTaskA() {

		JSONObject task = new JSONObject();
		JSONObject taskJson = new JSONObject();

		try {

			task.put(Tasks.KEY_TASK_ID, "taskA");
			task.put(Tasks.KEY_TASK_HEADING, "Task Model A");
			task.put(Tasks.KEY_TASK_SHORT_DESCRIPTION, "A simple task");
			task.put(Tasks.KEY_DATE, System.currentTimeMillis());
			task.put(Tasks.KEY_EXPIRATION_DATE, System.currentTimeMillis()
					+ 1000 * 60 * 60 * 24 * 10); // 10 days

			taskJson.put(KEY_TASK_TITLE, "Task Model A Title");
			taskJson.put(
					KEY_TASK_DETAIL,
					"This is a simple task that the user must do on their own. There is only one button and "
							+ "clicking it simply marks the task as complete.");
			taskJson.put(KEY_TASK_HELP, "This is the help text for task 0");

			taskJson.put(KEY_TASK_BUTTON_LEFT_VISIBLE, 1);
			taskJson.put(KEY_TASK_BUTTON_MIDDLE_VISIBLE, 1);
			taskJson.put(KEY_TASK_BUTTON_RIGHT_VISIBLE, 1);

			taskJson.put(KEY_TASK_BUTTON_LEFT_TEXT, "Mark Task Complete");
			taskJson.put(KEY_TASK_BUTTON_LEFT_ACTION, BUTTONACTIONS_DO_TASK);
			taskJson.put(KEY_TASK_BUTTON_MIDDLE_TEXT, "Do Task Later");
			taskJson.put(KEY_TASK_BUTTON_MIDDLE_ACTION,
					BUTTONACTIONS_DO_TASK_LATER);
			taskJson.put(KEY_TASK_BUTTON_RIGHT_TEXT, "Delete Task");
			taskJson.put(KEY_TASK_BUTTON_RIGHT_ACTION,
					BUTTONACTIONS_DELETE_TASK);

			task.put(Tasks.KEY_TASK_JSON, taskJson);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return task;
	}

	public static JSONObject makeDemoTaskB() {

		JSONObject task = new JSONObject();
		JSONObject taskJson = new JSONObject();
		JSONArray items = new JSONArray();
		JSONObject item1 = new JSONObject();
		JSONObject item2 = new JSONObject();

		try {

			task.put(Tasks.KEY_TASK_ID, "taskB");
			task.put(Tasks.KEY_TASK_HEADING, "Task Model B");
			task.put(Tasks.KEY_TASK_SHORT_DESCRIPTION, "Survey Task");
			task.put(Tasks.KEY_DATE, System.currentTimeMillis());
			task.put(Tasks.KEY_EXPIRATION_DATE, System.currentTimeMillis()
					+ 1000 * 60 * 60 * 24 * 10); // 10 days

			taskJson.put(KEY_TASK_TITLE, "Task Model B Title");
			taskJson.put(
					KEY_TASK_DETAIL,
					"This is a survey task: The user is given a list of survey items, each with a multiple"
							+ "choice response. We can set as many items as we want, and as many responses as we want for each. The left button "
							+ "submits user responses to the server and marks task complete. The right button cancels the dialog.");
			taskJson.put(KEY_TASK_HELP,
					"This is the help text for Task Model B.");

			taskJson.put(KEY_TASK_BUTTON_LEFT_VISIBLE, 1);
			taskJson.put(KEY_TASK_BUTTON_MIDDLE_VISIBLE, 1);
			taskJson.put(KEY_TASK_BUTTON_RIGHT_VISIBLE, 1);

			taskJson.put(KEY_TASK_BUTTON_LEFT_TEXT, "Submit Responses");
			taskJson.put(KEY_TASK_BUTTON_LEFT_ACTION, BUTTONACTIONS_DO_TASK);
			taskJson.put(KEY_TASK_BUTTON_MIDDLE_TEXT, "Answer Later");
			taskJson.put(KEY_TASK_BUTTON_MIDDLE_ACTION,
					BUTTONACTIONS_DO_TASK_LATER);
			taskJson.put(KEY_TASK_BUTTON_RIGHT_TEXT, "Delete Task");
			taskJson.put(KEY_TASK_BUTTON_RIGHT_ACTION,
					BUTTONACTIONS_DELETE_TASK);

			item1.put(TaskItemModel.KEY_ITEM_ID, "Item1");
			item1.put(TaskItemModel.KEY_ITEM_TEXT,
					"How many mosquitoes did you see today?");
			item1.put(TaskItemModel.KEY_ITEM_HELP,
					"This is the help text for item 1.");
			item1.put(TaskItemModel.KEY_ITEM_CHOICES, new JSONArray(
					"['less than 5', '6-20', 'more than 20']"));

			item2.put(TaskItemModel.KEY_ITEM_ID, "Item2");
			item2.put(TaskItemModel.KEY_ITEM_TEXT,
					"In which settings do you get bitten by mosquitoes most often?");
			item2.put(TaskItemModel.KEY_ITEM_HELP,
					"This is the help text for item 2.");
			item2.put(
					TaskItemModel.KEY_ITEM_CHOICES,
					new JSONArray(
							"['in my garden', 'at the beach', 'in the woods', 'at the supermarket', 'at the movies']"));
			items.put(item1);
			items.put(item2);

			taskJson.put(KEY_TASK_ITEMS, items);

			task.put(Tasks.KEY_TASK_JSON, taskJson);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return task;
	}

	public static JSONObject makeDemoTaskC() {

		JSONObject task = new JSONObject();
		JSONObject taskJson = new JSONObject();
		JSONArray items = new JSONArray();
		JSONObject item1 = new JSONObject();
		JSONObject item2 = new JSONObject();

		try {

			task.put(Tasks.KEY_TASK_ID, "taskC");
			task.put(Tasks.KEY_TASK_HEADING, "Task Model C");
			task.put(Tasks.KEY_TASK_SHORT_DESCRIPTION, "Website task");
			task.put(Tasks.KEY_DATE, System.currentTimeMillis());
			task.put(Tasks.KEY_EXPIRATION_DATE, System.currentTimeMillis()
					+ 1000 * 60 * 60 * 24 * 10); // 10 days

			taskJson.put(KEY_TASK_TITLE,
					"Visit the Tigatrapp Map and look at the results!");
			taskJson.put(
					KEY_TASK_DETAIL,
					"This task directs the user to a website on our server. We choose the URL and"
							+ "the app makes this the target of the left button. When the user clicks the "
							+ "button, the task is marked as complete and the user is taken to the website.");
			taskJson.put(KEY_TASK_HELP,
					"Help text for task model C. Bla bla bla.");

			taskJson.put(KEY_TASK_BUTTON_LEFT_VISIBLE, 1);
			taskJson.put(KEY_TASK_BUTTON_MIDDLE_VISIBLE, 1);
			taskJson.put(KEY_TASK_BUTTON_RIGHT_VISIBLE, 1);

			taskJson.put(KEY_TASK_BUTTON_LEFT_TEXT, "Go to website");
			taskJson.put(KEY_TASK_BUTTON_LEFT_ACTION, BUTTONACTIONS_DO_TASK);
			taskJson.put(KEY_TASK_BUTTON_LEFT_URL,
					"http://tce.ceab.csic.es/tigaDev2/TigatrappMap.html");

			taskJson.put(KEY_TASK_BUTTON_MIDDLE_TEXT, "Do Task Later");
			taskJson.put(KEY_TASK_BUTTON_MIDDLE_ACTION,
					BUTTONACTIONS_DO_TASK_LATER);
			taskJson.put(KEY_TASK_BUTTON_RIGHT_TEXT, "Delete Task");
			taskJson.put(KEY_TASK_BUTTON_RIGHT_ACTION,
					BUTTONACTIONS_DELETE_TASK);

			task.put(Tasks.KEY_TASK_JSON, taskJson);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return task;
	}

	public static JSONObject makeDemoTaskBWithTriggers() {

		JSONObject task = new JSONObject();
		JSONObject taskJson = new JSONObject();
		JSONArray items = new JSONArray();
		JSONObject item1 = new JSONObject();
		JSONObject item2 = new JSONObject();

		try {

			task.put(Tasks.KEY_TASK_ID, "taskB_t");
			task.put(Tasks.KEY_TASK_HEADING, "Task Model B With Triggers");
			task.put(Tasks.KEY_TASK_SHORT_DESCRIPTION,
					"Survey Task With Triggers");
			task.put(Tasks.KEY_DATE, System.currentTimeMillis());
			task.put(Tasks.KEY_EXPIRATION_DATE, System.currentTimeMillis()
					+ 1000 * 60 * 60 * 24 * 10); // 10 days
			task.put(
					Tasks.KEY_LOCATION_TRIGGERS_JSON,
					"["
							+ "{'lat': 41.5, 'lon': 2.5, 'start_hour': 0, 'end_hour': 24}, "
							+ "{'lat': 41.0, 'lon': 2.0, 'start_hour': 0, 'end_hour': 24} "

							+ "]");
			taskJson.put(KEY_TASK_TITLE, "Task Model B With Triggers");
			taskJson.put(
					KEY_TASK_DETAIL,
					"This is a survey task with triggers: For the demo, I have inluded trigger points in Barcelona and Blanes for all hours of the day. When triggers, the user is given a list of survey items, each with a multiple"
							+ "choice response. We can set as many items as we want, and as many responses as we want for each. The left button "
							+ "submits user responses to the server and marks task complete. The right button cancels the dialog.");
			taskJson.put(KEY_TASK_HELP,
					"This is the help text for Task Model B.");

			taskJson.put(KEY_TASK_BUTTON_LEFT_VISIBLE, 1);
			taskJson.put(KEY_TASK_BUTTON_MIDDLE_VISIBLE, 1);
			taskJson.put(KEY_TASK_BUTTON_RIGHT_VISIBLE, 1);

			taskJson.put(KEY_TASK_BUTTON_LEFT_TEXT, "Submit Responses");
			taskJson.put(KEY_TASK_BUTTON_LEFT_ACTION, BUTTONACTIONS_DO_TASK);
			taskJson.put(KEY_TASK_BUTTON_MIDDLE_TEXT, "Answer Later");
			taskJson.put(KEY_TASK_BUTTON_MIDDLE_ACTION,
					BUTTONACTIONS_DO_TASK_LATER);
			taskJson.put(KEY_TASK_BUTTON_RIGHT_TEXT, "Delete Task");
			taskJson.put(KEY_TASK_BUTTON_RIGHT_ACTION,
					BUTTONACTIONS_DELETE_TASK);

			item1.put(TaskItemModel.KEY_ITEM_ID, "Item1");
			item1.put(TaskItemModel.KEY_ITEM_TEXT,
					"Have you been bitten by a mosquito in the last hour?");
			item1.put(TaskItemModel.KEY_ITEM_HELP,
					"This is the help text for item 1.");
			item1.put(TaskItemModel.KEY_ITEM_CHOICES, new JSONArray(
					"['yes', 'no', 'maybe']"));

			item2.put(TaskItemModel.KEY_ITEM_ID, "Item2");
			item2.put(TaskItemModel.KEY_ITEM_TEXT,
					"Have you seen any tiger mosquitos in the vicinity of your current location?");
			item2.put(TaskItemModel.KEY_ITEM_HELP,
					"This is the help text for item 2.");
			item2.put(TaskItemModel.KEY_ITEM_CHOICES, new JSONArray(
					"['yes', 'no', 'maybe']"));
			items.put(item1);
			items.put(item2);

			taskJson.put(KEY_TASK_ITEMS, items);

			task.put(Tasks.KEY_TASK_JSON, taskJson);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return task;
	}

	public static JSONObject makeSiteConfirmation(Context context) {

		JSONObject task = new JSONObject();
		JSONObject taskJson = new JSONObject();
		JSONArray items = new JSONArray();
		JSONObject item1 = new JSONObject();
		JSONObject item2 = new JSONObject();
		JSONObject item3 = new JSONObject();

		try {

			task.put(Tasks.KEY_TASK_ID, "breedingSiteChecklist");
			task.put(Tasks.KEY_TASK_HEADING, "");
			task.put(Tasks.KEY_TASK_SHORT_DESCRIPTION,
					"Checklist for confirming breeding site");
			task.put(Tasks.KEY_DATE, -1);
			task.put(Tasks.KEY_EXPIRATION_DATE, -1);

			taskJson.put(
					KEY_TASK_TITLE,
					context.getResources().getString(
							R.string.report_checklist_title_site));
			taskJson.put(KEY_TASK_HELP, Html.fromHtml(context.getResources()
					.getString(R.string.site_report_help_html), null,
					new TigaTagHandler()));
			taskJson.put(KEY_TASK_PRESET_CONFIGURATION, PRECONFIRUATION_SITES);

			item1.put(TaskItemModel.KEY_ITEM_ID, "Item1");
			item1.put(TaskItemModel.KEY_ITEM_HELP_IMAGE,
					R.drawable.checklist_image_sites_1);

			item1.put(TaskItemModel.KEY_ITEM_TEXT, context.getResources()
					.getString(R.string.site_report_q1));
			item1.put(TaskItemModel.KEY_ITEM_HELP, context.getResources()
					.getString(R.string.site_report_item_help_1));
			item1.put(TaskItemModel.KEY_ITEM_CHOICES, new JSONArray("['"
					+ context.getResources().getString(R.string.embornals)
					+ "', '" + context.getResources().getString(R.string.fonts)
					+ "', '"
					+ context.getResources().getString(R.string.basses)
					+ "', '"
					+ context.getResources().getString(R.string.bidons)
					+ "', '" + context.getResources().getString(R.string.pous)
					+ "', '"
					+ context.getResources().getString(R.string.altres) + "']"));

			item2.put(TaskItemModel.KEY_ITEM_ID, "Item2");
			item2.put(TaskItemModel.KEY_ITEM_HELP_IMAGE,
					R.drawable.checklist_image_sites_2);

			item2.put(TaskItemModel.KEY_ITEM_TEXT, context.getResources()
					.getString(R.string.site_report_q2));
			item2.put(TaskItemModel.KEY_ITEM_HELP, context.getResources()
					.getString(R.string.site_report_item_help_2));
			item2.put(TaskItemModel.KEY_ITEM_CHOICES, new JSONArray("['"
					+ context.getResources().getString(R.string.yes) + "','"
					+ context.getResources().getString(R.string.no) + "']"));

			item3.put(TaskItemModel.KEY_ITEM_ID, "Item3");
			item3.put(TaskItemModel.KEY_ITEM_HELP_IMAGE,

			R.drawable.checklist_image_sites_3);
			item3.put(TaskItemModel.KEY_ITEM_TEXT, context.getResources()
					.getString(R.string.site_report_q3));
			item3.put(TaskItemModel.KEY_ITEM_HELP, context.getResources()
					.getString(R.string.site_report_item_help_3));
			item3.put(TaskItemModel.KEY_ITEM_CHOICES, new JSONArray("['"
					+ context.getResources().getString(R.string.yes) + "','"
					+ context.getResources().getString(R.string.no) + "','"
					+ context.getResources().getString(R.string.dontknow)
					+ "']"));

			items.put(item1);
			items.put(item2);
			items.put(item3);

			taskJson.put(KEY_TASK_ITEMS, items);

			task.put(Tasks.KEY_TASK_JSON, taskJson);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			task.put(Tasks.KEY_TASK_ID, "adultChecklist");
			task.put(Tasks.KEY_TASK_HEADING, "");
			task.put(Tasks.KEY_TASK_SHORT_DESCRIPTION,
					"Checklist for confirming adult mosquito");
			task.put(Tasks.KEY_DATE, -1);
			task.put(Tasks.KEY_EXPIRATION_DATE, -1);

			taskJson.put(
					KEY_TASK_TITLE,
					context.getResources().getString(
							R.string.report_checklist_title_adult));
			taskJson.put(KEY_TASK_PRESET_CONFIGURATION, PRECONFIRUATION_ADULTS);
			taskJson.put(KEY_TASK_HELP, Html.fromHtml(context.getResources()
					.getString(R.string.adult_report_help_html), null,
					new TigaTagHandler()));

			item1.put(TaskItemModel.KEY_ITEM_ID, "Item1");
			item1.put(TaskItemModel.KEY_ITEM_TEXT, context.getResources()
					.getString(R.string.confirmation_q1_adult_sizecolor));
			item1.put(TaskItemModel.KEY_ITEM_HELP, context.getResources()
					.getString(R.string.q1_sizecolor_text));
			item1.put(TaskItemModel.KEY_ITEM_HELP_IMAGE,
					R.drawable.checklist_image_adult_1);
			item1.put(TaskItemModel.KEY_ITEM_CHOICES, new JSONArray("['"
					+ context.getResources().getString(R.string.yes) + "','"
					+ context.getResources().getString(R.string.no) + "','"
					+ context.getResources().getString(R.string.dontknow)
					+ "']"));

			item2.put(TaskItemModel.KEY_ITEM_ID, "item2");
			item2.put(TaskItemModel.KEY_ITEM_TEXT, context.getResources()
					.getString(R.string.confirmation_q2_adult_headthorax));
			item2.put(TaskItemModel.KEY_ITEM_HELP, context.getResources()
					.getString(R.string.q3_headthorax_text));
			item2.put(TaskItemModel.KEY_ITEM_HELP_IMAGE,
					R.drawable.checklist_image_adult_2);
			item2.put(TaskItemModel.KEY_ITEM_CHOICES, new JSONArray("['"
					+ context.getResources().getString(R.string.yes) + "','"
					+ context.getResources().getString(R.string.no) + "','"
					+ context.getResources().getString(R.string.dontknow)
					+ "']"));

			item3.put(TaskItemModel.KEY_ITEM_ID, "item3");
			item3.put(TaskItemModel.KEY_ITEM_TEXT, context.getResources()
					.getString(R.string.adult_report_q3));
			item3.put(TaskItemModel.KEY_ITEM_HELP, context.getResources()
					.getString(R.string.q2_abdomenlegs_text));
			item3.put(TaskItemModel.KEY_ITEM_HELP_IMAGE,
					R.drawable.checklist_image_adult_3);
			item3.put(TaskItemModel.KEY_ITEM_CHOICES, new JSONArray("['"
					+ context.getResources().getString(R.string.yes) + "','"
					+ context.getResources().getString(R.string.no) + "','"
					+ context.getResources().getString(R.string.dontknow)
					+ "']"));

			items.put(item1);
			items.put(item2);
			items.put(item3);

			taskJson.put(KEY_TASK_ITEMS, items);

			task.put(Tasks.KEY_TASK_JSON, taskJson);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return task;
	}

	public static void storeTask(Context context, String task) {

		ContentResolver cr = context.getContentResolver();
		cr.insert(Tasks.CONTENT_URI,
				ContentProviderValuesTasks.createTask(task));

	}

}
