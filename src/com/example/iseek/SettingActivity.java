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
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//导入页面资源
		addPreferencesFromResource(R.xml.settings);		
		
		//获取控件
		prefTargetPhone = (EditTextPreference)findPreference(StaticVar.prefTargetPhoneKey);
		prefSosNumber   = (EditTextPreference)findPreference(StaticVar.prefSosNumberKey);
		prefSaveAll		= (PreferenceScreen)findPreference(StaticVar.prefSaveAllKey);
		prefAbout       = (PreferenceScreen)findPreference(StaticVar.prefAboutKey);
		
		//绑定监听器
		prefTargetPhone.setOnPreferenceChangeListener(this);
		prefSosNumber.setOnPreferenceChangeListener(this);
		prefSaveAll.setOnPreferenceClickListener(this);
		prefAbout.setOnPreferenceClickListener(this);
		
	}

	//值改变响应函数
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		System.out.println("Change--key:" + preference.getKey() + "--newValue:" + newValue);
		return true;
	}

	//点击响应函数
	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
		System.out.println("Click--key:" + preference.getKey());		
		
		
		//如果是saveall控件的话，发送短线到目标gps进行设置
		if(preference.getKey()==StaticVar.prefSaveAllKey)
		{
			String sosNumberValue = StaticVar.prefs.getString(StaticVar.prefSosNumberKey, "unset");
			if(sosNumberValue != "unset")
			{			
				StaticVar.SendMessage(SettingActivity.this, StaticVar.SMS_SET_SOS + sosNumberValue);
			}
		}		
		return true;
	}

}
