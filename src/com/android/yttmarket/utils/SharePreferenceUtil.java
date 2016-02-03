package com.android.yttmarket.utils;

/**/
import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtil {
	public static final String CITY_SHAREPRE_FILE = "city";
	private static final String CASH_CITY = "_city";

	//

	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	public SharePreferenceUtil(Context context, String file) {
		sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
		editor = sp.edit();
	}

	// city
	public void setCity(String city) {
		editor.putString(CASH_CITY, city);
		editor.commit();
	}

	public String getCity() {
		return sp.getString(CASH_CITY, "");
	}

}
