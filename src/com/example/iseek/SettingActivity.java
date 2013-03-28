package com.example.iseek;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

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
		
		String tmp = new String("");
		
		System.out.println("Change--key:" + preference.getKey() + "--newValue:" + newValue);
		
		if(preference.getKey() == StaticVar.prefTargetPhoneKey)
		{
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
			//判断符合手机号码，则打开dialog，确认发送短信
			
			
		}
		
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
	
	public boolean isMobileNumber(String mobiles){
		  Pattern p=Pattern.compile("^(((13[0-9])|18[0,5-9]|15[0-3,5-9])\\d{8})|(10086)$");//Patterns are compiled regular expressions;
		  Matcher m=p.matcher(mobiles);//matcher:The result of applying a Pattern to a given input;
		  System.out.println(m.matches()+ "---");//Tries to match the Pattern against the entire region ,true if (and only if) the Pattern matches the entire region.
		  return m.matches();
	}

}
