package ceab.movelab.tigabib;

import android.content.Context;

public class Messages {
	private static final String TAG = "Messages";

	public static String makeIntentExtraKey(Context context, String simpleKey){
		return context.getResources().getString(R.string.package_prefix) + simpleKey;
	}
	
	// IMPORTANT: THIS MUST BE SET IN STRING AND IN MANIFEST IDENTICALLY
	public static String internalAction(Context context) {
Util.logInfo(TAG, "internal action string: " + context.getResources().getString(R.string.internal_action));
		return context.getResources().getString(R.string.internal_action);
	}

	public static String stopFixAction(Context context) {
		return internalAction(context) + ".stop_fix_action";
	}

	public static String taskFixAction(Context context) {
		return internalAction(context) + ".task_fix_action";
	}

	
	public static String newSamplesReadyAction(Context context) {
		return internalAction(context) + "new_samples_ready_action";
	}

	public static String INTERNAL_MESSAGE_EXTRA = "internal_message_extra";

	public static String START_DAILY_SAMPLING = "start_daily_sampling";

	public static String STOP_DAILY_SAMPLING = "stop_daily_sampling";

	public static String START_DAILY_SYNC = "start_daily_sync";

	public static String STOP_DAILY_SYNC = "stop_daily_sync";

	public static String START_TASK_FIX = "start_task_fix";

	public static String SHOW_TASK_NOTIFICATION = "show_task_notification";

	public static String REMOVE_TASK_NOTIFICATION = "remove_task_notification";

}
