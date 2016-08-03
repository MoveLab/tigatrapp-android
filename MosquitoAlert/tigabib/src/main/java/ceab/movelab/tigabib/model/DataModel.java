package ceab.movelab.tigabib.model;

import android.content.Context;

import io.realm.Realm;

public class DataModel {
	
	private static DataModel instance = null;

	public static float scale = 0.0f;

	public static Realm myRealm;


	protected DataModel() {
		// Exists only to defeat instantiation
	}
	
	public static void initialize(Context ctx) {
		instance = null;
		getInstance();
	}
	
	public static synchronized DataModel getInstance() {
		if ( instance == null ) {
			instance = new DataModel();
	    }
	    return instance;
	}
	
}
