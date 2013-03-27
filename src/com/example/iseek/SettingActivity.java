package com.example.iseek;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

//设置页面，添加配置文件
public class SettingActivity extends PreferenceActivity implements OnPreferenceClickListener,
		OnPreferenceChangeListener{

	//声明设置页面的控件
	EditTextPreference prefTargetPhone = null;
	EditTextPreference prefSosNumber   = null;
	PreferenceScreen   prefSaveAll     = null;
	PreferenceScreen   prefAbout       = null;
	
	
	//控件对应的key字符串声明
	static String prefTargetPhoneKey = null;
	String prefSosNumberKey   = null;
	String prefSaveAllKey     = null;
	String prefAboutKey       = null;
	
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//导入页面资源
		addPreferencesFromResource(R.xml.settings);
		
		//获取控件key字符串
		prefTargetPhoneKey = getResources().getString(R.string.set_targetPhone_key);
		prefSosNumberKey   = getResources().getString(R.string.set_sosNumber_key);
		prefSaveAllKey     = getResources().getString(R.string.set_saveall_key);
		prefAboutKey       = getResources().getString(R.string.set_about_key);
		
		//获取控件
		prefTargetPhone = (EditTextPreference)findPreference(prefTargetPhoneKey);
		prefSosNumber   = (EditTextPreference)findPreference(prefSosNumberKey);
		prefSaveAll		= (PreferenceScreen)findPreference(prefSaveAllKey);
		prefAbout       = (PreferenceScreen)findPreference(prefAboutKey);
		
		//绑定监听器
		prefTargetPhone.setOnPreferenceChangeListener(this);
		prefSosNumber.setOnPreferenceChangeListener(this);
		prefSaveAll.setOnPreferenceClickListener(this);
		prefAbout.setOnPreferenceClickListener(this);
		
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		System.out.println("Change--key:" + preference.getKey() + "--newValue:" + newValue);
		return true;
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
		System.out.println("Click--key:" + preference.getKey());
		
		String sosNumberValue = MainActivity.prefs.getString(prefSosNumberKey, "unset");
		
		if((preference.getKey()==prefSaveAllKey) && (sosNumberValue != "unset"))
		{
			
			MainActivity.SendMessage(SettingActivity.this, MainActivity.SMS_SET_SOS + sosNumberValue);
		}
		
		return true;
	}

}
