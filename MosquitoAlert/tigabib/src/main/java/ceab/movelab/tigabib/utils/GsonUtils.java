package ceab.movelab.tigabib.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GsonUtils {
	
	static SimpleDateFormat format_from = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.UK);  	// "2015-06-02T11:30:00Z"

	private static class DateDeserializer implements JsonDeserializer<Date>, JsonSerializer<Date> {
		@Override
		public Date deserialize(JsonElement src, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
			try {
				return format_from.parse(src.getAsString());
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
			//return new Date(Long.parseLong(src.getAsString()));
		}
		@Override
		public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src != null ? Long.toString(src.getTime()) : "0");
		}
	}


    private static Gson mGson;

    synchronized public static Gson getGson() {
		//format_from.setTimeZone(TimeZone.getTimeZone("UTC"));

        if ( mGson == null ) {
            mGson = new GsonBuilder()
            	.registerTypeAdapter(Date.class, new DateDeserializer())
				.serializeNulls()
            .create();
        }
        return mGson;
    }


	private static Gson mRealmGson;

	synchronized public Gson getRealmGson() {
		if ( mRealmGson == null ) {
			mRealmGson = new GsonBuilder()
					.registerTypeAdapter(Date.class, new DateDeserializer())
					.serializeNulls()
					.create();
		}
		return mRealmGson;
	}

}
