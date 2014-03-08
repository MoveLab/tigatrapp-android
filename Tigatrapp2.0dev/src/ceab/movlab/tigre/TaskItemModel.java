package ceab.movlab.tigre;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TaskItemModel {

	private String itemId;
	private String itemText;
	private String itemHelp;
	private int itemHelpImage;
	private String[] itemChoices;

	public static final String KEY_ITEM_ID = "item_id";
	public static final String KEY_ITEM_TEXT = "item_text";
	public static final String KEY_ITEM_HELP = "item_help";
	public static final String KEY_ITEM_HELP_IMAGE = "item_help_image";
	public static final String KEY_ITEM_CHOICES = "item_choices";
	public static final String KEY_ITEM_RESPONSE = "item_response";

	public TaskItemModel(JSONObject item) {

		try {

			if (item.has(KEY_ITEM_ID))
				this.itemId = item.getString(KEY_ITEM_ID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {

			if (item.has(KEY_ITEM_TEXT))
				this.itemText = item.getString(KEY_ITEM_TEXT);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {

			if (item.has(KEY_ITEM_HELP))
				this.itemHelp = item.getString(KEY_ITEM_HELP);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {

			if (item.has(KEY_ITEM_HELP_IMAGE))
				this.itemHelpImage = item.getInt(KEY_ITEM_HELP_IMAGE);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {

			if (item.has(KEY_ITEM_CHOICES)) {
				JSONArray itemChoicesJson = item.getJSONArray(KEY_ITEM_CHOICES);
				String[] itemChoicesTemp = new String[itemChoicesJson.length() + 1];
				// Making the first row blank so that Spinner default is no
				// selection
				itemChoicesTemp[0] = " ";
				for (int i = 0; i < itemChoicesJson.length(); i++) {
					itemChoicesTemp[i + 1] = itemChoicesJson.getString(i);
				}
				itemChoices = itemChoicesTemp;
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

}
