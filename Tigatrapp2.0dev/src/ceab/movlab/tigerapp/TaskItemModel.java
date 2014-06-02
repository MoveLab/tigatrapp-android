package ceab.movlab.tigerapp;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import ceab.movelab.tigerapp.R;

public class TaskItemModel {

	private static String TAG = "TaskItemModel";

	private String itemId;
	private String itemText;

	private String itemHelp;

	private int itemHelpImage;
	private String[] itemChoices;

	private String itemResponse;

	public static final String KEY_ITEM_ID = "id";
	public static final String KEY_QUESTION = "question";
	public static final String KEY_QUESTION_CATALAN = "question_catalan";
	public static final String KEY_QUESTION_SPANISH = "question_spanish";
	public static final String KEY_QUESTION_ENGLISH = "question_english";

	public static final String KEY_HELP_TEXT = "help_text";
	public static final String KEY_HELP_TEXT_CATALAN = "help_text_catalan";
	public static final String KEY_HELP_TEXT_SPANISH = "help_text_spanish";
	public static final String KEY_HELP_TEXT_ENGLISH = "help_text_english";

	public static final String KEY_PREPOSITIONED_IMAGE_REFERENCE = "prepositioned_image_reference";
	public static final String KEY_ANSWER_CHOICES = "answer_choices";
	public static final String KEY_ANSWER_CHOICES_CATALAN = "answer_choices_catalan";
	public static final String KEY_ANSWER_CHOICES_SPANISH = "answer_choices_spanish";
	public static final String KEY_ANSWER_CHOICES_ENGLISH = "answer_choices_english";

	public static final String KEY_ITEM_TEXT = "item_text";

	public static final String KEY_ITEM_RESPONSE = "item_response";

	public TaskItemModel(Context context, JSONObject item) {

		try {

			if (item.has(KEY_ITEM_ID))
				this.itemId = item.getString(KEY_ITEM_ID);
		} catch (JSONException e) {
			Util.logError(context, TAG, "error: " + e);
		}

		// if answered, then language already decided:
		if (item.has(KEY_ITEM_TEXT)) {
			try {
				this.itemText = item.getString(KEY_ITEM_TEXT);
			} catch (JSONException e) {
				Util.logError(context, TAG, "error: " + e);
			}
			// if preset, then language already decided:
		} else if (item.has(KEY_ANSWER_CHOICES) && item.has(KEY_HELP_TEXT)
				&& item.has(KEY_QUESTION)) {
			try {
				this.itemText = item.getString(KEY_QUESTION);
				this.itemHelp = item.getString(KEY_HELP_TEXT);

				JSONArray itemChoicesJson = item
						.getJSONArray(KEY_ANSWER_CHOICES);

				String[] itemChoicesTemp = new String[itemChoicesJson.length() + 1];
				// Making the first row blank so that Spinner default is no
				// selection
				itemChoicesTemp[0] = context.getResources().getString(
						R.string.spinner_nothing_selected);
				for (int i = 0; i < itemChoicesJson.length(); i++) {

					itemChoicesTemp[i + 1] = itemChoicesJson.getString(i);
				}
				itemChoices = itemChoicesTemp;

			} catch (JSONException e) {
				Util.logError(context, TAG, "error: " + e);
			}

		} else {

			String currentLang = PropertyHolder.getLanguage();

			if (currentLang.equals("ca")) {
				try {

					if (item.has(KEY_QUESTION_CATALAN))
						this.itemText = item.getString(KEY_QUESTION_CATALAN);
				} catch (JSONException e) {
					Util.logError(context, TAG, "error: " + e);
				}
				try {

					if (item.has(KEY_HELP_TEXT_CATALAN))
						this.itemHelp = item.getString(KEY_HELP_TEXT_CATALAN);
				} catch (JSONException e) {
					Util.logError(context, TAG, "error: " + e);
				}

				try {

					if (item.has(KEY_ANSWER_CHOICES_CATALAN)) {

						String theseChoices = item
								.getString(KEY_ANSWER_CHOICES_CATALAN);

						JSONArray itemChoicesJson = new JSONArray(theseChoices);
						String[] itemChoicesTemp = new String[itemChoicesJson
								.length() + 1];
						// Making the first row blank so that Spinner default is
						// no
						// selection
						itemChoicesTemp[0] = context.getResources().getString(
								R.string.spinner_nothing_selected);
						for (int i = 0; i < itemChoicesJson.length(); i++) {
							itemChoicesTemp[i + 1] = itemChoicesJson
									.getString(i);
						}
						itemChoices = itemChoicesTemp;
					}

				} catch (JSONException e) {
					Util.logError(context, TAG, "error: " + e);
				}

			} else if (currentLang.equals("es")) {

				try {

					if (item.has(KEY_QUESTION_SPANISH))
						this.itemText = item.getString(KEY_QUESTION_SPANISH);
				} catch (JSONException e) {
					Util.logError(context, TAG, "error: " + e);
				}
				try {

					if (item.has(KEY_HELP_TEXT_SPANISH))
						this.itemHelp = item.getString(KEY_HELP_TEXT_SPANISH);
				} catch (JSONException e) {
					Util.logError(context, TAG, "error: " + e);
				}

				try {

					if (item.has(KEY_ANSWER_CHOICES_SPANISH)) {

						String theseChoices = item
								.getString(KEY_ANSWER_CHOICES_SPANISH);

						JSONArray itemChoicesJson = new JSONArray(theseChoices);
						String[] itemChoicesTemp = new String[itemChoicesJson
								.length() + 1];

						// Making the first row blank so that Spinner default is
						// no
						// selection
						itemChoicesTemp[0] = context.getResources().getString(
								R.string.spinner_nothing_selected);
						for (int i = 0; i < itemChoicesJson.length(); i++) {
							itemChoicesTemp[i + 1] = itemChoicesJson
									.getString(i);
						}
						itemChoices = itemChoicesTemp;
					}

				} catch (JSONException e) {
					Util.logError(context, TAG, "error: " + e);
				}

			} else if (currentLang.equals("en")) {

				try {

					if (item.has(KEY_QUESTION_ENGLISH))
						this.itemText = item.getString(KEY_QUESTION_ENGLISH);
				} catch (JSONException e) {
					Util.logError(context, TAG, "error: " + e);
				}
				try {

					if (item.has(KEY_HELP_TEXT_ENGLISH))
						this.itemHelp = item.getString(KEY_HELP_TEXT_ENGLISH);
				} catch (JSONException e) {
					Util.logError(context, TAG, "error: " + e);
				}

				try {

					if (item.has(KEY_ANSWER_CHOICES_ENGLISH)) {

						String theseChoices = item
								.getString(KEY_ANSWER_CHOICES_ENGLISH);

						JSONArray itemChoicesJson = new JSONArray(theseChoices);
						String[] itemChoicesTemp = new String[itemChoicesJson
								.length() + 1];
						// Making the first row blank so that Spinner default is
						// no
						// selection
						itemChoicesTemp[0] = context.getResources().getString(
								R.string.spinner_nothing_selected);
						for (int i = 0; i < itemChoicesJson.length(); i++) {
							itemChoicesTemp[i + 1] = itemChoicesJson
									.getString(i);
						}
						itemChoices = itemChoicesTemp;
					}

				} catch (JSONException e) {
					Util.logError(context, TAG, "error: " + e);
				}

			}

		}

		// ////////////////////////////
		try {

			if (item.has(KEY_PREPOSITIONED_IMAGE_REFERENCE))
				if (item.getString(KEY_PREPOSITIONED_IMAGE_REFERENCE) != null
						&& item.getString(KEY_PREPOSITIONED_IMAGE_REFERENCE)
								.length() > 0
						&& !item.getString(KEY_PREPOSITIONED_IMAGE_REFERENCE)
								.equals("null"))
					this.itemHelpImage = item
							.getInt(KEY_PREPOSITIONED_IMAGE_REFERENCE);
		} catch (JSONException e) {
			Util.logError(context, TAG, "error: " + e);
		}

		try {

			if (item.has(KEY_ITEM_RESPONSE))
				this.itemResponse = item.getString(KEY_ITEM_RESPONSE);
		} catch (JSONException e) {
			Util.logError(context, TAG, "error: " + e);
		}

	}

	/*********** Set Methods ******************/

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public void setItemText(String itemText) {
		this.itemText = itemText;
	}

	public void setItemHelp(String itemHelp) {
		this.itemHelp = itemHelp;
	}

	public void setItemHelpImage(int itemHelpImage) {
		this.itemHelpImage = itemHelpImage;
	}

	public void setItemChoices(String[] itemChoices) {
		this.itemChoices = itemChoices;
	}

	public void setItemResponse(String itemResponse) {
		this.itemResponse = itemResponse;
	}

	/*********** Get Methods ****************/

	public String getItemId() {
		return this.itemId;
	}

	public String getItemText() {
		return this.itemText;
	}

	public String getItemHelp() {
		return this.itemHelp;
	}

	public int getItemHelpImage() {
		return this.itemHelpImage;
	}

	public String[] getItemChoices() {
		return this.itemChoices;
	}

	public String getItemResponse() {
		return this.itemResponse;
	}

	public int getItemResponsePosition() {
		int result = -1;
		if (this.itemResponse != null) {
			JSONObject thisItemResponse;
			try {
				thisItemResponse = new JSONObject(this.itemResponse);
				String thisResponse = thisItemResponse
						.getString(TaskItemModel.KEY_ITEM_RESPONSE);
				if (thisResponse != null)
					result = Arrays.asList(getItemChoices()).indexOf(
							thisResponse);

			} catch (JSONException e) {
				// TODO
			}
		}
		return result;
	}

}
