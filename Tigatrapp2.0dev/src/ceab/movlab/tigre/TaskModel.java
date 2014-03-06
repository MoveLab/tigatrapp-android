package ceab.movlab.tigre;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TaskModel {

	public static final String KEY_TASK_TITLE = "task_title";
	public static final String KEY_TASK_DETAIL = "task_detail";
	public static final String KEY_TASK_HELP = "task_help";
	public static final String KEY_TASK_ITEMS = "task_items";

	public static final String KEY_TASK_BUTTON_LEFT_TEXT = "task_button_left_text";
	public static final String KEY_TASK_BUTTON_RIGHT_TEXT = "task_button_right_text";
	public static final String KEY_TASK_BUTTON_LEFT_ACTION = "task_button_left_action";
	public static final String KEY_TASK_BUTTON_RIGHT_ACTION = "task_button_right_action";

	public static JSONObject makeSampleTask() {

		JSONObject task = new JSONObject();
		JSONArray items = new JSONArray();
		JSONObject item1 = new JSONObject();
		JSONObject item2 = new JSONObject();

		try {
			task.put(KEY_TASK_TITLE, "Sample Task Title");
			task.put(KEY_TASK_DETAIL, "sample task detail");
			task.put(KEY_TASK_HELP, "sample task help");

			item1.put(TaskItemModel.KEY_ITEM_TEXT, "Item 1 text");
			item1.put(TaskItemModel.KEY_ITEM_HELP, "Item 1 help");
			item1.put(TaskItemModel.KEY_ITEM_CHOICES, new JSONArray(
					"[c11, c12]"));

			item2.put(TaskItemModel.KEY_ITEM_TEXT, "Item 2 text");
			item2.put(TaskItemModel.KEY_ITEM_HELP, "Item 2 help");
			item2.put(TaskItemModel.KEY_ITEM_CHOICES, new JSONArray(
					"[c21, c22]"));

			items.put(item1);
			items.put(item2);

			task.put(KEY_TASK_ITEMS, items);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return task;
	}
}
