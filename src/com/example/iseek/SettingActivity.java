package com.example.iseek;

import android.os.Bundle;
import android.preference.PreferenceActivity;

//����ҳ�棬��������ļ�
public class SettingActivity extends PreferenceActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.settings);
	}

}
