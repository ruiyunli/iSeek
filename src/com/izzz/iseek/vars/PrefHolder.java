package com.izzz.iseek.vars;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.izzz.iseek.R;

public class PrefHolder {
	
	public static String prefTargetPhoneKey  	= null;			//目标gps手机号
  	
  	public static String prefSosNumberKey    	= null;			//sos关联号码
  	
  	public static String prefCorrEntryKey     	= null;			//校准页面入口
  	
  	public static String prefCorrEnableKey		= null;			//校准使能
  	
  	public static String prefOfflineKey			= null;			//离线管理入口
  	
  	public static String prefGuideKey			= null;			//应用介绍入口
  	
  	public static String prefAboutKey        	= null;			//关于页面入口
  	
  	public static String prefLastLatitudeKey  	= null;			//上次定位纬度
  	
  	public static String prefLastLongitudeKey 	= null;			//上次定位精度
  	
  	public static String prefOneKeyNumberKey	= null;			//一键拨号设置
  	
  	public static String prefOneKeyNumber1Key	= null;			//一键拨号号码一
  	
  	public static String prefOneKeyNumber2Key	= null;			//一键拨号号码二
  	
  	public static String prefOneKeyNumber3Key	= null;			//一键拨号号码三
  	
  	public static SharedPreferences prefs		= null;			//SharedPreferences对象
  	
  	public static SharedPreferences.Editor prefsEditor = null;	//SharedPreferences编辑器
  	
	public PrefHolder(Context mcontext) {
		super();
		// TODO Auto-generated constructor stub
		
		//设置页面
		prefTargetPhoneKey  	= mcontext.getResources().getString(R.string.set_targetPhone_key);
		prefSosNumberKey    	= mcontext.getResources().getString(R.string.set_sosNumber_key);
		prefOneKeyNumberKey		= mcontext.getResources().getString(R.string.set_oneKeyNumber_key);
		prefCorrEntryKey   		= mcontext.getResources().getString(R.string.set_correntry_key);
		prefOfflineKey			= mcontext.getResources().getString(R.string.set_offline_key);
		prefGuideKey			= mcontext.getResources().getString(R.string.set_guide_key);
		prefCorrEnableKey 		= mcontext.getResources().getString(R.string.set_correnable_key);
		prefAboutKey        	= mcontext.getResources().getString(R.string.set_about_key);
		
		//上次定位位置
		prefLastLatitudeKey  	= mcontext.getResources().getString(R.string.set_last_latitude_key);
		prefLastLongitudeKey 	= mcontext.getResources().getString(R.string.set_last_longitude_key);
		
		//一键拨号
		prefOneKeyNumber1Key 	= mcontext.getResources().getString(R.string.onekey_number_1_key);
		prefOneKeyNumber2Key 	= mcontext.getResources().getString(R.string.onekey_number_2_key);
		prefOneKeyNumber3Key 	= mcontext.getResources().getString(R.string.onekey_number_3_key);
		
		//获取prefs和editor
		prefs = PreferenceManager.getDefaultSharedPreferences(mcontext);
		prefsEditor = prefs.edit();
	}
	
	public static String getTargetPhone()
	{
		String targetPhone = null;
		targetPhone = PrefHolder.prefs.getString(PrefHolder.prefTargetPhoneKey, "unset");
		if(targetPhone.equals("unset"))
		{
			targetPhone = null;
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "target phone did not set");
		}
		return targetPhone;
	}
  	
  	
}


