package com.izzz.iseek.vars;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.izzz.iseek.R;

public class PrefHolder {
	
	public static String prefTargetPhoneKey  	= null;			//Ŀ��gps�ֻ���
  	
  	public static String prefSosNumberKey    	= null;			//sos��������
  	
  	public static String prefCorrEntryKey     	= null;			//У׼ҳ�����
  	
  	public static String prefCorrEnableKey		= null;			//У׼ʹ��
  	
  	public static String prefOfflineKey			= null;			//���߹������
  	
  	public static String prefGuideKey			= null;			//Ӧ�ý������
  	
  	public static String prefAboutKey        	= null;			//����ҳ�����
  	
  	public static String prefLastLatitudeKey  	= null;			//�ϴζ�λγ��
  	
  	public static String prefLastLongitudeKey 	= null;			//�ϴζ�λ����
  	
  	public static String prefOneKeyNumberKey	= null;			//һ����������
  	
  	public static String prefOneKeyNumber1Key	= null;			//һ�����ź���һ
  	
  	public static String prefOneKeyNumber2Key	= null;			//һ�����ź����
  	
  	public static String prefOneKeyNumber3Key	= null;			//һ�����ź�����
  	
  	public static SharedPreferences prefs		= null;			//SharedPreferences����
  	
  	public static SharedPreferences.Editor prefsEditor = null;	//SharedPreferences�༭��
  	
	public PrefHolder(Context mcontext) {
		super();
		// TODO Auto-generated constructor stub
		
		//����ҳ��
		prefTargetPhoneKey  	= mcontext.getResources().getString(R.string.set_targetPhone_key);
		prefSosNumberKey    	= mcontext.getResources().getString(R.string.set_sosNumber_key);
		prefOneKeyNumberKey		= mcontext.getResources().getString(R.string.set_oneKeyNumber_key);
		prefCorrEntryKey   		= mcontext.getResources().getString(R.string.set_correntry_key);
		prefOfflineKey			= mcontext.getResources().getString(R.string.set_offline_key);
		prefGuideKey			= mcontext.getResources().getString(R.string.set_guide_key);
		prefCorrEnableKey 		= mcontext.getResources().getString(R.string.set_correnable_key);
		prefAboutKey        	= mcontext.getResources().getString(R.string.set_about_key);
		
		//�ϴζ�λλ��
		prefLastLatitudeKey  	= mcontext.getResources().getString(R.string.set_last_latitude_key);
		prefLastLongitudeKey 	= mcontext.getResources().getString(R.string.set_last_longitude_key);
		
		//һ������
		prefOneKeyNumber1Key 	= mcontext.getResources().getString(R.string.onekey_number_1_key);
		prefOneKeyNumber2Key 	= mcontext.getResources().getString(R.string.onekey_number_2_key);
		prefOneKeyNumber3Key 	= mcontext.getResources().getString(R.string.onekey_number_3_key);
		
		//��ȡprefs��editor
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


