package com.izzz.iseek.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.izzz.iseek.R;

public class PrefHolder {
	
	public static String prefTargetPhoneKey  	= null;			//控件对应的key字符串声明
  	
  	public static String prefSosNumberKey    	= null;			//控件对应的key字符串声明
  	
  	public static String prefCorrEntryKey     	= null;			//控件对应的key字符串声明
  	
  	public static String prefCorrEnableKey		= null;			//控件对应的key字符串声明
  	
  	public static String prefOfflineKey			= null;			//控件对应的key字符串声明
  	
  	public static String prefGuideKey			= null;			//控件对应的key字符串声明
  	
  	public static String prefAboutKey        	= null;			//控件对应的key字符串声明
  	
  	public static String prefLastLatitudeKey  	= null;			//控件对应的key字符串声明
  	
  	public static String prefLastLongitudeKey 	= null;			//控件对应的key字符串声明
  	
//  	public static String prefCorrPointSizeKey 	= null;			//控件对应的key字符串声明
//  	
//  	public static String[] prefCorrPointKey 	= null;			//控件对应的key字符串声明
  	
  	public static SharedPreferences prefs    	= null;			//控件对应的key字符串声明
  	
  	public static SharedPreferences.Editor prefsEditor = null;	//控件对应的key字符串声明
  	
	public PrefHolder(Context mcontext) {
		super();
		// TODO Auto-generated constructor stub
		
		//设置页面
		prefTargetPhoneKey  	= mcontext.getResources().getString(R.string.set_targetPhone_key);
		prefSosNumberKey    	= mcontext.getResources().getString(R.string.set_sosNumber_key);
		prefCorrEntryKey   		= mcontext.getResources().getString(R.string.set_correntry_key);
		prefOfflineKey			= mcontext.getResources().getString(R.string.set_offline_key);
		prefGuideKey			= mcontext.getResources().getString(R.string.set_guide_key);
		prefCorrEnableKey 		= mcontext.getResources().getString(R.string.set_correnable_key);
		prefAboutKey        	= mcontext.getResources().getString(R.string.set_about_key);
		
		//上次定位位置
		prefLastLatitudeKey  	= mcontext.getResources().getString(R.string.set_last_latitude_key);
		prefLastLongitudeKey 	= mcontext.getResources().getString(R.string.set_last_longitude_key);
		
//		//校准内容存储
//		prefCorrPointKey 		= new String[5];
//		prefCorrPointSizeKey	= mcontext.getResources().getString(R.string.corr_point_size_key);
//		prefCorrPointKey[0]		= mcontext.getResources().getString(R.string.corr_point_0_key);
//		prefCorrPointKey[1]		= mcontext.getResources().getString(R.string.corr_point_1_key);
//		prefCorrPointKey[2]		= mcontext.getResources().getString(R.string.corr_point_2_key);
//		prefCorrPointKey[3]		= mcontext.getResources().getString(R.string.corr_point_3_key);
//		prefCorrPointKey[4]		= mcontext.getResources().getString(R.string.corr_point_4_key);

		//获取prefs和editor
		prefs = PreferenceManager.getDefaultSharedPreferences(mcontext);
		prefsEditor = prefs.edit();
	}
  	
  	
}


