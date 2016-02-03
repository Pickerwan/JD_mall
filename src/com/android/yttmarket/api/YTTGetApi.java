package com.android.yttmarket.api;

import com.android.yttmarket.utils.WSError;

import android.content.Context;

/**
 * 接口方法
 * @author ydy
 *
 */

public interface YTTGetApi {
	
	String Login(Context context, String username, String pwd) throws WSError;

	String GetResult(Context context, String ServerID, String OperationID,
			String inputstrings) throws WSError;

	
}
