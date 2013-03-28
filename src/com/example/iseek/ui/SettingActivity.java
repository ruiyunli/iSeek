package com.example.iseek.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.iseek.R;
import com.example.iseek.R.string;
import com.example.iseek.R.xml;
import com.example.iseek.vars.StaticVar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

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
		
		if(newValue == null) return false;
		
		if(preference.getKey() == StaticVar.prefTargetPhoneKey)
		{
			//������ʽ�ж��Ƿ�Ϸ��ֻ�����
			if(isMobileNumber((String)newValue))
			{
				Toast.makeText(SettingActivity.this, R.string.ToastValidPhoneNumber, Toast.LENGTH_LONG).show();
				return true;				
			}
			else
			{
				Toast.makeText(SettingActivity.this, R.string.ToastInvalidPhoneNumber, Toast.LENGTH_LONG).show();
				return false;
			}
		}
		else if(preference.getKey() == StaticVar.prefSosNumberKey)
		{
			//�жϷ����ֻ����룬���dialog��ȷ�Ϸ��Ͷ���
			AlertDialog.Builder setSosDialog = new AlertDialog.Builder(this);
			if(isMobileNumber((String) newValue))
			{	
				StaticVar.prefsSosNumStr = (String) newValue;
				setSosDialog.setTitle(R.string.DialogSosTitleOK);
				setSosDialog.setMessage(getResources().getText(R.string.DialogSosMsgOK) + (String)newValue + "?");
				setSosDialog.setPositiveButton(R.string.DialogSosOk, new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						StaticVar.prefs.edit().putString(StaticVar.prefSosNumberKey, StaticVar.prefsSosNumStr).commit();
						StaticVar.prefs = PreferenceManager.getDefaultSharedPreferences(SettingActivity.this);
					}
				});
				setSosDialog.setNegativeButton(R.string.DialogSosCancel, null);
				setSosDialog.show();
				
				return false;
			}
			else
			{
				setSosDialog.setTitle(R.string.DialogSosTitleNO);
				setSosDialog.setMessage(getResources().getText(R.string.DialogSosMsgNO) + (String)newValue);
				setSosDialog.setPositiveButton(R.string.DialogSosOk, null);
				setSosDialog.show();
				return false;
			}			
		}
		return false;		 
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
	
	public boolean isMobileNumber(String mobiles){
		  Pattern p=Pattern.compile("^(((13[0-9])|18[0,5-9]|15[0-3,5-9])\\d{8})|(10086)$");//Patterns are compiled regular expressions;
		  Matcher m=p.matcher(mobiles);//matcher:The result of applying a Pattern to a given input;
		  System.out.println(m.matches()+ "---");//Tries to match the Pattern against the entire region ,true if (and only if) the Pattern matches the entire region.
		  return m.matches();
	}

}
