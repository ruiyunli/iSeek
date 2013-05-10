package com.izzz.iseek.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.izzz.iseek.R;

public class PrefHolder {
	
	public static String prefTargetPhoneKey  	= null;			//�ؼ���Ӧ��key�ַ�������
  	
  	public static String prefSosNumberKey    	= null;			//�ؼ���Ӧ��key�ַ�������
  	
  	public static String prefCorrEntryKey     	= null;			//�ؼ���Ӧ��key�ַ�������
  	
  	public static String prefCorrEnableKey		= null;			//�ؼ���Ӧ��key�ַ�������
  	
  	public static String prefOfflineKey			= null;			//�ؼ���Ӧ��key�ַ�������
  	
  	public static String prefGuideKey			= null;			//�ؼ���Ӧ��key�ַ�������
  	
  	public static String prefAboutKey        	= null;			//�ؼ���Ӧ��key�ַ�������
  	
  	public static String prefLastLatitudeKey  	= null;			//�ؼ���Ӧ��key�ַ�������
  	
  	public static String prefLastLongitudeKey 	= null;			//�ؼ���Ӧ��key�ַ�������
  	
//  	public static String prefCorrPointSizeKey 	= null;			//�ؼ���Ӧ��key�ַ�������
//  	
//  	public static String[] prefCorrPointKey 	= null;			//�ؼ���Ӧ��key�ַ�������
  	
  	public static SharedPreferences prefs    	= null;			//�ؼ���Ӧ��key�ַ�������
  	
  	public static SharedPreferences.Editor prefsEditor = null;	//�ؼ���Ӧ��key�ַ�������
  	
	public PrefHolder(Context mcontext) {
		super();
		// TODO Auto-generated constructor stub
		
		//����ҳ��
		prefTargetPhoneKey  	= mcontext.getResources().getString(R.string.set_targetPhone_key);
		prefSosNumberKey    	= mcontext.getResources().getString(R.string.set_sosNumber_key);
		prefCorrEntryKey   		= mcontext.getResources().getString(R.string.set_correntry_key);
		prefOfflineKey			= mcontext.getResources().getString(R.string.set_offline_key);
		prefGuideKey			= mcontext.getResources().getString(R.string.set_guide_key);
		prefCorrEnableKey 		= mcontext.getResources().getString(R.string.set_correnable_key);
		prefAboutKey        	= mcontext.getResources().getString(R.string.set_about_key);
		
		//�ϴζ�λλ��
		prefLastLatitudeKey  	= mcontext.getResources().getString(R.string.set_last_latitude_key);
		prefLastLongitudeKey 	= mcontext.getResources().getString(R.string.set_last_longitude_key);
		
//		//У׼���ݴ洢
//		prefCorrPointKey 		= new String[5];
//		prefCorrPointSizeKey	= mcontext.getResources().getString(R.string.corr_point_size_key);
//		prefCorrPointKey[0]		= mcontext.getResources().getString(R.string.corr_point_0_key);
//		prefCorrPointKey[1]		= mcontext.getResources().getString(R.string.corr_point_1_key);
//		prefCorrPointKey[2]		= mcontext.getResources().getString(R.string.corr_point_2_key);
//		prefCorrPointKey[3]		= mcontext.getResources().getString(R.string.corr_point_3_key);
//		prefCorrPointKey[4]		= mcontext.getResources().getString(R.string.corr_point_4_key);

		//��ȡprefs��editor
		prefs = PreferenceManager.getDefaultSharedPreferences(mcontext);
		prefsEditor = prefs.edit();
	}
  	
  	
}


