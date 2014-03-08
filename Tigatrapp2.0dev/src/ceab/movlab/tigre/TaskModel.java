package ceab.movlab.tigre;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import ceab.movlab.tigre.ContentProviderContractTasks.Tasks;
import ceab.movlab.tigre.ContentProviderContractTracks.Fixes;

public class TaskModel {

	public static final String KEY_TASK_TITLE = "task_title";
	public static final String KEY_TASK_DETAIL = "task_detail";
	public static final String KEY_TASK_HELP = "task_help";
	public static final String KEY_TASK_ITEMS = "task_items";
	public static final String KEY_TASK_BUTTON_LEFT_ACTION = "task_button_left_action";
	public static final String KEY_TASK_BUTTON_LEFT_URL = "task_button_left_url";
	public static final String KEY_TASK_MAKE_MAILING_OPTION = "task_make_mailing";
	public static final String KEY_TASK_N_SITE_REPORTS = "task_n_site";
	public static final String KEY_TASK_N_ADULT_REPORTS = "task_n_adult";

	public static final int TASK_CONFIGURATION_SIMPLE = 0;
	public static final int TASK_CONFIGURATION_SURVEY_TASK = 1;
	public static final int TASK_CONFIGURATION_ADULT_TASK = 2;
	public static final int TASK_CONFIGURATION_SITE_TASK = 3;
	public static final int TASK_CONFIGURATION_WEBLINK = 4;

	// task configurations used for the regular reports -- not intended to be
	// called from server
	public static final int TASK_CONFIGURATION_REPORT_SITE = 5;
	public static final int TASK_CONFIGURATION_REPORT_ADULT = 6;

	public static JSONObject makeDemoTask0() {

		JSONObject task = new JSONObject();
		JSONObject taskJson = new JSONObject();
		JSONArray items = new JSONArray();
		JSONObject item1 = new JSONObject();
		JSONObject item2 = new JSONObject();

		try {

			task.put(Tasks.KEY_TASK_ID, "task0");
			task.put(Tasks.KEY_TASK_HEADING, "Task Model 0");
			task.put(Tasks.KEY_TASK_SHORT_DESCRIPTION, "A simple task");
			task.put(Tasks.KEY_DATE, System.currentTimeMillis());
			task.put(Tasks.KEY_EXPIRATION_DATE, System.currentTimeMillis());

			taskJson.put(KEY_TASK_TITLE, "Task Model 0 Title");
			taskJson.put(
					KEY_TASK_DETAIL,
					"This is a simple task that the user must do on their own. There is only one button and "
							+ "clicking it simply marks the task as complete.");
			taskJson.put(KEY_TASK_HELP, "This is the help text for task 0");
			taskJson.put(KEY_TASK_BUTTON_LEFT_ACTION, TASK_CONFIGURATION_SIMPLE);

			task.put(Tasks.KEY_TASK_JSON, taskJson);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return task;
	}

	public static JSONObject makeDemoTask1() {

		JSONObject task = new JSONObject();
		JSONObject taskJson = new JSONObject();
		JSONArray items = new JSONArray();
		JSONObject item1 = new JSONObject();
		JSONObject item2 = new JSONObject();

		try {

			task.put(Tasks.KEY_TASK_ID, "task1");
			task.put(Tasks.KEY_TASK_HEADING, "Task Model 1");
			task.put(Tasks.KEY_TASK_SHORT_DESCRIPTION, "Survey Task");
			task.put(Tasks.KEY_DATE, System.currentTimeMillis());
			task.put(Tasks.KEY_EXPIRATION_DATE, System.currentTimeMillis());

			taskJson.put(KEY_TASK_TITLE, "Task Model 1 Title");
			taskJson.put(
					KEY_TASK_DETAIL,
					"This is a survey task: The user is given a list of survey items, each with a multiple"
							+ "choice response. We can set as many items as we want, and as many responses as we want for each. The left button "
							+ "submits user responses to the server and marks task complete. The right button cancels the dialog.");
			taskJson.put(KEY_TASK_HELP,
					"This is the help text for Task Model 1.");
			taskJson.put(KEY_TASK_BUTTON_LEFT_ACTION,
					TASK_CONFIGURATION_SURVEY_TASK);

			item1.put(TaskItemModel.KEY_ITEM_ID, "Item1");
			item1.put(TaskItemModel.KEY_ITEM_TEXT,
					"How many mosquitoes did you see today?");
			item1.put(TaskItemModel.KEY_ITEM_HELP,
					"This is the help text for item 1.");
			item1.put(TaskItemModel.KEY_ITEM_CHOICES, new JSONArray(
					"[less than 5, 6-20, more than 20]"));

			item2.put(TaskItemModel.KEY_ITEM_ID, "Item2");
			item2.put(TaskItemModel.KEY_ITEM_TEXT,
					"In which settings do you get bitten by mosquitoes most often?");
			item2.put(TaskItemModel.KEY_ITEM_HELP,
					"This is the help text for item 2.");
			item2.put(
					TaskItemModel.KEY_ITEM_CHOICES,
					new JSONArray(
							"[in my garden, at the beach, in the woods, at the supermarket, at the movies]"));
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

	public static JSONObject makeDemoTask2() {

		JSONObject task = new JSONObject();
		JSONObject taskJson = new JSONObject();
		JSONArray items = new JSONArray();
		JSONObject item1 = new JSONObject();
		JSONObject item2 = new JSONObject();

		try {

			task.put(Tasks.KEY_TASK_ID, "task2");
			task.put(Tasks.KEY_TASK_HEADING, "Task Model 2");
			task.put(Tasks.KEY_TASK_SHORT_DESCRIPTION, "Adult reporting task");
			task.put(Tasks.KEY_DATE, System.currentTimeMillis());
			task.put(Tasks.KEY_EXPIRATION_DATE, System.currentTimeMillis());

			taskJson.put(KEY_TASK_TITLE, "Task Model 2 Title");
			taskJson.put(
					KEY_TASK_DETAIL,
					"This task directs the user to the adult reporting tool. When the user clicks"
							+ "the left button, that tool is opened. The task is marked as complete when the user submits "
							+ "the requested number of reports."
							+ "The mailing specimen option in the report can be made visible by setting KEY_TASK_MAKE_MAILING_OPTION"
							+ "to 1. And the number of reports required to complete the task can be set with KEY_TASK_N_ADULT_REPORTS.");
			taskJson.put(KEY_TASK_HELP, "sample task help");
			taskJson.put(KEY_TASK_BUTTON_LEFT_ACTION,
					TASK_CONFIGURATION_ADULT_TASK);
			taskJson.put(KEY_TASK_MAKE_MAILING_OPTION, 1);
			taskJson.put(KEY_TASK_N_ADULT_REPORTS, 1);

			task.put(Tasks.KEY_TASK_JSON, taskJson);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return task;
	}

	public static JSONObject makeDemoTask3() {

		JSONObject task = new JSONObject();
		JSONObject taskJson = new JSONObject();
		JSONArray items = new JSONArray();
		JSONObject item1 = new JSONObject();
		JSONObject item2 = new JSONObject();

		try {

			task.put(Tasks.KEY_TASK_ID, "task3");
			task.put(Tasks.KEY_TASK_HEADING, "Task Model 3");
			task.put(Tasks.KEY_TASK_SHORT_DESCRIPTION,
					"Breeding Site Reporting Task");
			task.put(Tasks.KEY_DATE, System.currentTimeMillis());
			task.put(Tasks.KEY_EXPIRATION_DATE, System.currentTimeMillis());

			taskJson.put(KEY_TASK_TITLE, "Task Model 3 Title");
			taskJson.put(
					KEY_TASK_DETAIL,
					"This task directs the user to the breeding site reporting tool. When the user clicks"
							+ "the left button, that tool is opened. The task is marked as complete when the user submits the requested"
							+ "number of reports (set with KEY_TASK_N_SITE_REPORTS).");
			taskJson.put(KEY_TASK_HELP, "sample task help");
			taskJson.put(KEY_TASK_BUTTON_LEFT_ACTION,
					TASK_CONFIGURATION_SITE_TASK);
			taskJson.put(KEY_TASK_N_SITE_REPORTS, 1);

			task.put(Tasks.KEY_TASK_JSON, taskJson);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return task;
	}

	public static JSONObject makeDemoTask4() {

		JSONObject task = new JSONObject();
		JSONObject taskJson = new JSONObject();
		JSONArray items = new JSONArray();
		JSONObject item1 = new JSONObject();
		JSONObject item2 = new JSONObject();

		try {

			task.put(Tasks.KEY_TASK_ID, "task4");
			task.put(Tasks.KEY_TASK_HEADING, "Task Model 4");
			task.put(Tasks.KEY_TASK_SHORT_DESCRIPTION, "Website task");
			task.put(Tasks.KEY_DATE, System.currentTimeMillis());
			task.put(Tasks.KEY_EXPIRATION_DATE, System.currentTimeMillis());

			taskJson.put(KEY_TASK_TITLE, "Task Model 4 Title");
			taskJson.put(
					KEY_TASK_DETAIL,
					"This task directs the user to a website. We choose the URL and"
							+ "the app makes this the target of the left button. Whent the user clicks the "
							+ "button, the task is marked as complete and the user is taken to the website.");
			taskJson.put(KEY_TASK_HELP,
					"Help text for task model 4. Bla bla bla.");
			taskJson.put(KEY_TASK_BUTTON_LEFT_ACTION,
					TASK_CONFIGURATION_WEBLINK);
			taskJson.put(KEY_TASK_BUTTON_LEFT_URL, "http://atrapaeltigre.com");
			task.put(Tasks.KEY_TASK_JSON, taskJson);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return task;
	}

	public static JSONObject makeSiteConfirmation() {

		JSONObject task = new JSONObject();
		JSONObject taskJson = new JSONObject();
		JSONArray items = new JSONArray();
		JSONObject item1 = new JSONObject();
		JSONObject item2 = new JSONObject();
		JSONObject item3 = new JSONObject();
		JSONObject item4 = new JSONObject();

		try {

			task.put(Tasks.KEY_TASK_ID, "breedingSiteChecklist");
			task.put(Tasks.KEY_TASK_HEADING, "");
			task.put(Tasks.KEY_TASK_SHORT_DESCRIPTION,
					"Checklist for confirming breeding site");
			task.put(Tasks.KEY_DATE, -1);
			task.put(Tasks.KEY_EXPIRATION_DATE, -1);

			taskJson.put(KEY_TASK_TITLE, "Breeding Site Confirmation");
			taskJson.put(
					KEY_TASK_HELP,
					"Potential breeding sites are small recipients or public "
							+ "spaces that contain stagnant water that can be used by "
							+ "tiger females to lay their eggs and therefore, "
							+ "allow mosquitoes to reproduce. They have no fishes or "
							+ "other animals that eat mosquito larvae. More info on the website.");
			taskJson.put(KEY_TASK_BUTTON_LEFT_ACTION,
					TASK_CONFIGURATION_REPORT_SITE);

			item1.put(TaskItemModel.KEY_ITEM_ID, "Item1");
			item1.put(TaskItemModel.KEY_ITEM_TEXT,
					"Select the type of potential breeding site:");
			item1.put(TaskItemModel.KEY_ITEM_HELP, "");
			item1.put(
					TaskItemModel.KEY_ITEM_CHOICES,
					new JSONArray(
							"[embornals, fonts, basses artificials, bidons, pous, altres]"));

			item2.put(TaskItemModel.KEY_ITEM_ID, "Item2");
			item2.put(TaskItemModel.KEY_ITEM_TEXT,
					"Does it have stagnant water inside?");
			item2.put(TaskItemModel.KEY_ITEM_HELP, "");
			item2.put(TaskItemModel.KEY_ITEM_CHOICES,
					new JSONArray("[yes, no]"));

			item3.put(TaskItemModel.KEY_ITEM_ID, "Item3");
			item3.put(TaskItemModel.KEY_ITEM_TEXT,
					"Have you seen mosquito larvae (not necessarily tiger mosquito) inside?");
			item3.put(TaskItemModel.KEY_ITEM_HELP, "");
			item3.put(TaskItemModel.KEY_ITEM_CHOICES,
					new JSONArray("[yes, no]"));

			item4.put(TaskItemModel.KEY_ITEM_ID, "Item4");
			item4.put(
					TaskItemModel.KEY_ITEM_TEXT,
					"Have you seen adult mosquitoes (not necessarily tigre) inside or very closeby?");
			item4.put(TaskItemModel.KEY_ITEM_HELP, "");
			item4.put(TaskItemModel.KEY_ITEM_CHOICES,
					new JSONArray("[yes, no]"));

			items.put(item1);
			items.put(item2);
			items.put(item3);
			items.put(item4);

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

			taskJson.put(KEY_TASK_TITLE, "Tiger Mosquito Taxonomy");
			taskJson.put(KEY_TASK_BUTTON_LEFT_ACTION,
					TASK_CONFIGURATION_REPORT_ADULT);

			item1.put(TaskItemModel.KEY_ITEM_ID, "Item1");
			item1.put(TaskItemModel.KEY_ITEM_TEXT, context.getResources()
					.getString(R.string.confirmation_q1_adult_sizecolor));
			item1.put(TaskItemModel.KEY_ITEM_HELP, context.getResources()
					.getString(R.string.q1_sizecolor_text));
			item1.put(TaskItemModel.KEY_ITEM_HELP_IMAGE, R.drawable.m);
			item1.put(TaskItemModel.KEY_ITEM_CHOICES, new JSONArray("["
					+ context.getResources().getString(R.string.yes) + ","
					+ context.getResources().getString(R.string.no) + "]"));

			
			item2.put(TaskItemModel.KEY_ITEM_ID, "item2");
			item2.put(TaskItemModel.KEY_ITEM_TEXT, context.getResources()
					.getString(R.string.confirmation_q2_adult_headthorax));
			item2.put(TaskItemModel.KEY_ITEM_HELP, context.getResources()
					.getString(R.string.q3_headthorax_text));
			item2.put(TaskItemModel.KEY_ITEM_HELP_IMAGE, R.drawable.n);
			item2.put(TaskItemModel.KEY_ITEM_CHOICES, new JSONArray("["
					+ context.getResources().getString(R.string.yes) + ","
					+ context.getResources().getString(R.string.no) + "]"));

			
			item3.put(TaskItemModel.KEY_ITEM_ID, "item3");
			item3.put(TaskItemModel.KEY_ITEM_TEXT, context.getResources()
					.getString(R.string.confirmation_q3_abdomenlegs));
			item3.put(TaskItemModel.KEY_ITEM_HELP, context.getResources()
					.getString(R.string.q2_abdomenlegs_text));
			item3.put(TaskItemModel.KEY_ITEM_HELP_IMAGE, R.drawable.o);
			item3.put(TaskItemModel.KEY_ITEM_CHOICES, new JSONArray("["
					+ context.getResources().getString(R.string.yes) + ","
					+ context.getResources().getString(R.string.no) + "]"));

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
