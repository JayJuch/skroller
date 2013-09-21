package com.torusworks.android.db;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.torusworks.skroller.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

public class PreferencePersister {
	
	public static int getInt(Context context, String key, int defValue) {
		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		return app_preferences.getInt(key, defValue);		
	}

	public static boolean putInt(Context context, String key, int value) {
		boolean ret = false;
		try {
			SharedPreferences app_preferences = PreferenceManager
					.getDefaultSharedPreferences(context);
			SharedPreferences.Editor editor = app_preferences.edit();
			editor.putInt(key, value);
			editor.commit();
			ret = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public static String getString(Context context, String key, String defValue) {
		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		return app_preferences.getString(key, defValue);		
	}

	public static boolean putString(Context context, String key, String value) {
		boolean ret = false;
		try {
			SharedPreferences app_preferences = PreferenceManager
					.getDefaultSharedPreferences(context);
			SharedPreferences.Editor editor = app_preferences.edit();
			editor.putString(key, value);
			editor.commit();
			ret = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}	

	public static boolean getBoolean(Context context, String key, boolean defValue) {
		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		return app_preferences.getBoolean(key, defValue);		
	}

	public static boolean putBoolean(Context context, String key, boolean value) {
		boolean ret = false;
		try {
			SharedPreferences app_preferences = PreferenceManager
					.getDefaultSharedPreferences(context);
			SharedPreferences.Editor editor = app_preferences.edit();
			editor.putBoolean(key, value);
			editor.commit();
			ret = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	
	public static String[] getArray(Context context, String key, String subKey) {
		String[] aryMessageHistory = null;
		
		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		String serMessageHistory = app_preferences.getString(key, "");
		List<String> lRet = new LinkedList<String>();
		
		if (serMessageHistory != null && !serMessageHistory.isEmpty()) {
			try {
				JSONArray jary = new JSONArray(serMessageHistory);
				for (int i=0;i<jary.length();i++) {
					try {
						JSONObject jo = jary.getJSONObject(i);
						if (jo.getString(subKey) != null) {
							lRet.add(jo.getString(subKey));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				aryMessageHistory = lRet.toArray(new String[lRet.size()]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
		
		return aryMessageHistory;
	}
	
	public static boolean putInArray(Context context, String key, String subKey, String value) {
		boolean ret = false;
		
		try {

			SharedPreferences app_preferences = PreferenceManager
					.getDefaultSharedPreferences(context);
			String serMessageHistory = app_preferences.getString(
					key, "");
			JSONArray jary = null;
			boolean bExists = false;
			if (serMessageHistory != null && !serMessageHistory.isEmpty()) {
				jary = new JSONArray(serMessageHistory);
				for (int j = 0; j < jary.length(); j++) {
					JSONObject jo = jary.getJSONObject(j);
					if (jo.getString(subKey).equals(value)) {
						// value already exists, get out
						return true;
					}
				}

			} else {
				jary = new JSONArray();
			}
			
			JSONObject ob = new JSONObject();
			ob.put(subKey, value);
			jary.put(ob);

			String serializedMsgHistory = jary.toString();
			SharedPreferences.Editor editor = app_preferences.edit();
			editor.putString(key,
					serializedMsgHistory);
			editor.commit();
			ret = true;
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return ret;
	}
}
