package com.example.iseek;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

//����ҳ�棬��������ļ�
public class SettingActivity extends PreferenceActivity implements OnPreferenceClickListener,
		OnPreferenceChangeListener{

	//��������ҳ��Ŀؼ�
	EditTextPreference prefTargetPhone = null;
	EditTextPreference prefSosNumber   = null;
	PreferenceScreen   prefSaveAll     = null;
	PreferenceScreen   prefAbout       = null;
	
	
	//�ؼ���Ӧ��key�ַ�������
	static String prefTargetPhoneKey = null;
	String prefSosNumberKey   = null;
	String prefSaveAllKey     = null;
	String prefAboutKey       = null;
	
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//����ҳ����Դ
		addPreferencesFromResource(R.xml.settings);
		
		//��ȡ�ؼ�key�ַ���
		prefTargetPhoneKey = getResources().getString(R.string.set_targetPhone_key);
		prefSosNumberKey   = getResources().getString(R.string.set_sosNumber_key);
		prefSaveAllKey     = getResources().getString(R.string.set_saveall_key);
		prefAboutKey       = getResources().getString(R.string.set_about_key);
		
		//��ȡ�ؼ�
		prefTargetPhone = (EditTextPreference)findPreference(prefTargetPhoneKey);
		prefSosNumber   = (EditTextPreference)findPreference(prefSosNumberKey);
		prefSaveAll		= (PreferenceScreen)findPreference(prefSaveAllKey);
		prefAbout       = (PreferenceScreen)findPreference(prefAboutKey);
		
		//�󶨼�����
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
