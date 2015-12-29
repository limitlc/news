package com.itheima45.zhbj.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author andong
 * ���湤����
 */
public class CacheUtils {
	
	private static final String CACHE_FILE_NAME = "itheima45";
	private static SharedPreferences mSharedPreferences;
	
	public static void putBoolean(Context context, String key, boolean value) {
		if(mSharedPreferences == null) {
			mSharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME, Context.MODE_PRIVATE);
		}
		mSharedPreferences.edit().putBoolean(key, value).commit();
	}

	public static boolean getBoolean(Context context, String key, boolean defValue) {
		if(mSharedPreferences == null) {
			mSharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME, Context.MODE_PRIVATE);
		}
		return mSharedPreferences.getBoolean(key, defValue);
	}
	
	/**
	 * ��SharedPreferences�д洢һ���ַ���
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void putString(Context context, String key, String value) {
		if(mSharedPreferences == null) {
			mSharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME, Context.MODE_PRIVATE);
		}
		mSharedPreferences.edit().putString(key, value).commit();
	}

	/**
	 * ��SharedPreferences��ȡһ���ַ���
	 * @param context
	 * @param key
	 * @param defValue ȱʡֵ
	 */
	public static String getString(Context context, String key, String defValue) {
		if(mSharedPreferences == null) {
			mSharedPreferences = context.getSharedPreferences(CACHE_FILE_NAME, Context.MODE_PRIVATE);
		}
		return mSharedPreferences.getString(key, defValue);
	}
}
