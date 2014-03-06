package ceab.movlab.tigre;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TaskItemModel {

	private String itemText = "";
	private String itemHelp = "";
	private String[] itemChoices = { "" };

	public static final String KEY_ITEM_TEXT = "item_text";
	public static final String KEY_ITEM_HELP = "item_help";
	public static final String KEY_ITEM_CHOICES = "item_choices";
	
	public TaskItemModel(JSONObject item) {

		try {

			this.itemText = item.getString(KEY_ITEM_TEXT);
			this.itemHelp = item.getString(KEY_ITEM_HELP);
			JSONArray itemChoicesJson = item.getJSONArray(KEY_ITEM_CHOICES);
			String[] itemChoicesTemp = new String[itemChoicesJson.length()];
			for (int i = 0; i < itemChoicesJson.length(); i++) {
				itemChoicesTemp[i] = itemChoicesJson.getString(i);
			}
			itemChoices = itemChoicesTemp;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*********** Set Methods ******************/

	public void setItemText(String itemText) {
		this.itemText = itemText;
	}

	public void setItemHelp(String itemHelp) {
		this.itemHelp = itemHelp;
	}

	public void setItemChoices(String[] itemChoices) {
		this.itemChoices = itemChoices;
	}

	/*********** Get Methods ****************/

	public String getItemText() {
		return this.itemText;
	}

	public String getItemHelp() {
		return this.itemHelp;
	}

	public String[] getItemChoices() {
		return this.itemChoices;
	}

}
