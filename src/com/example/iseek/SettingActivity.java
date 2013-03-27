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
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//����ҳ����Դ
		addPreferencesFromResource(R.xml.settings);		
		
		//��ȡ�ؼ�
		prefTargetPhone = (EditTextPreference)findPreference(StaticVar.prefTargetPhoneKey);
		prefSosNumber   = (EditTextPreference)findPreference(StaticVar.prefSosNumberKey);
		prefSaveAll		= (PreferenceScreen)findPreference(StaticVar.prefSaveAllKey);
		prefAbout       = (PreferenceScreen)findPreference(StaticVar.prefAboutKey);
		
		//�󶨼�����
		prefTargetPhone.setOnPreferenceChangeListener(this);
		prefSosNumber.setOnPreferenceChangeListener(this);
		prefSaveAll.setOnPreferenceClickListener(this);
		prefAbout.setOnPreferenceClickListener(this);
		
	}

	//ֵ�ı���Ӧ����
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		System.out.println("Change--key:" + preference.getKey() + "--newValue:" + newValue);
		return true;
	}

	//�����Ӧ����
	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
		System.out.println("Click--key:" + preference.getKey());		
		
		
		//�����saveall�ؼ��Ļ������Ͷ��ߵ�Ŀ��gps��������
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
