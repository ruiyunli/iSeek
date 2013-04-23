package com.izzz.iseek.base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.example.iseek.R;
import com.example.iseek.R.string;
import com.example.iseek.R.xml;
import com.izzz.iseek.SMS.SMSreceiver;
import com.izzz.iseek.SMS.SMSsender;
import com.izzz.iseek.app.IseekApplication;
import com.izzz.iseek.tools.AlarmControl;
import com.izzz.iseek.tools.LogDialog;
import com.izzz.iseek.vars.StaticVar;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.telephony.SmsManager;
import android.view.Window;
import android.widget.Toast;

//设置页面，添加配置文件
public class SettingActivity extends PreferenceActivity implements OnPreferenceClickListener,
		OnPreferenceChangeListener{

	private EditTextPreference prefTargetPhone 	= null;
	
	private EditTextPreference prefSosNumber   	= null;
	
	private PreferenceScreen   prefCorrection 	= null;
	
	private PreferenceScreen   prefOffline 	= null;
	
	private PreferenceScreen   prefAbout = null;
	
	public static LogDialog settingDialog = null;
	
	private SMSsender settingSMSsender = null;		//发送短信接口
	
	private SMSreceiver setReceiver = null;
	
	private IntentFilter setFilter  = null;
	
	public static AlarmControl alarmHandler = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		boolean isCustom = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); //声明使用自定义标题
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.settings);		
		
		if(isCustom)
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_setting);//自定义布局赋值
		
		settingDialog = new LogDialog(SettingActivity.this, R.string.DialogMsgHeader, R.string.DialogTitle);
		
		settingSMSsender = new SMSsender(SettingActivity.this);

		Initprefs();	//初始化prefs
		
		InitBCR();		//注册广播
		
		alarmHandler = new AlarmControl(SettingActivity.this, StaticVar.COM_ALARM_SOS_SET);
	}

	private void Initprefs()
	{
		//获取控件
		prefTargetPhone = (EditTextPreference)findPreference(IseekApplication.prefTargetPhoneKey);
		prefSosNumber   = (EditTextPreference)findPreference(IseekApplication.prefSosNumberKey);
		prefCorrection	= (PreferenceScreen)findPreference(IseekApplication.prefCorrKey);
		prefOffline		= (PreferenceScreen)findPreference(IseekApplication.prefOfflineKey);
		prefAbout       = (PreferenceScreen)findPreference(IseekApplication.prefAboutKey);
		
		//绑定监听器
		prefTargetPhone.setOnPreferenceChangeListener(this);
		prefSosNumber.setOnPreferenceChangeListener(this);
		prefCorrection.setOnPreferenceClickListener(this);
		prefOffline.setOnPreferenceClickListener(this);
		prefAbout.setOnPreferenceClickListener(this);
		
		prefTargetPhone.setSummary(IseekApplication.prefs.getString(IseekApplication.prefTargetPhoneKey, 
				(String) this.getResources().getText(R.string.set_targetPhone_summary)));
		prefSosNumber.setSummary(IseekApplication.prefs.getString(IseekApplication.prefSosNumberKey, 
				(String) this.getResources().getText(R.string.set_sosNumber_summary)));
				
	}
	
	private void InitBCR()
	{
		//注册广播监听
		setReceiver = new SMSreceiver();
		setFilter = new IntentFilter();
		setFilter.addAction(StaticVar.COM_SMS_DELIVERY_SOS_GPS);
		setFilter.addAction(StaticVar.COM_SMS_SEND_SOS_GPS);
		setFilter.addAction(StaticVar.COM_SMS_DELIVERY_SOS_TAR);
		setFilter.addAction(StaticVar.COM_SMS_SEND_SOS_TAR);
		setFilter.addAction(StaticVar.COM_ALARM_SOS_SET);
		SettingActivity.this.registerReceiver(setReceiver, setFilter);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		SettingActivity.this.unregisterReceiver(setReceiver);
	}

	private boolean ChangeTargetPhone(String phoneNum)
	{
		//正则表达式判断是否合法手机号码
		if(isMobileNumber(phoneNum))
		{
			Toast.makeText(SettingActivity.this, R.string.ToastTargetSetOK, Toast.LENGTH_SHORT).show();
			prefTargetPhone.setSummary((CharSequence) phoneNum);
			return true;			
		}
		else
		{
			Toast.makeText(SettingActivity.this, R.string.ToastInvalidPhoneNumber, Toast.LENGTH_SHORT).show();
			return false;
		}
	}
	
	private boolean ChangeSosPhone(String phoneNum)
	{
		//判断符合手机号码，则打开dialog，确认发送短信			
		if(!isMobileNumber(phoneNum))
		{
			Toast.makeText(SettingActivity.this, R.string.ToastInvalidPhoneNumber, Toast.LENGTH_SHORT).show();
			return false;
		}
		settingDialog.enable();
		settingDialog.proMessage = (String) getResources().getText(R.string.DialogMsgHeader);
		settingDialog.proLogDialog.setMessage(settingDialog.proMessage);
		settingDialog.showLog();
		
		//给gps发送短信
		if(settingSMSsender.SendMessage(null, StaticVar.SMS_SET_SOS + phoneNum, 
				StaticVar.COM_SMS_SEND_SOS_GPS, StaticVar.COM_SMS_DELIVERY_SOS_GPS))
		{
			//给关联sos号码发送短信
			settingSMSsender.SendMessage(phoneNum, prefTargetPhone.getSummary() + StaticVar.SMS_SET_SOS_TAR , 
					StaticVar.COM_SMS_SEND_SOS_TAR, StaticVar.COM_SMS_DELIVERY_SOS_TAR);
			
			prefSosNumber.setSummary((CharSequence) phoneNum);			
			
			alarmHandler.Start();			
						
			return true;
		}
		return false;
	}
	
	//值改变响应函数
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "Change--key:" + preference.getKey() + "--newValue:" + newValue);
			
		//TargetPhone设置
		if(preference.getKey() == IseekApplication.prefTargetPhoneKey)
		{
			return ChangeTargetPhone((String) newValue);
		}
		//SOSphone设置
		else if(preference.getKey() == IseekApplication.prefSosNumberKey)
		{
			return ChangeSosPhone((String)newValue);		
		}
		return false;		 
	}

	//点击响应函数
	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "Click--key:" + preference.getKey());

		//关于页面
		if(preference.getKey() == IseekApplication.prefAboutKey)
		{
			Intent intent = new Intent();
			intent.setClass(SettingActivity.this, AboutActivity.class);
			startActivity(intent);
			return true;
		}
		//离线管理页面
		else if(preference.getKey() == IseekApplication.prefOfflineKey)
		{
			Intent intent = new Intent();
			intent.setClass(SettingActivity.this, OfflineManage.class);
			startActivity(intent);
			return true;
		}
		//校准设置
		else if(preference.getKey() == IseekApplication.prefCorrKey)
		{
//			StaticVar.CORRECTION_ENABLE = true;
//			BaseMapMain.CorrSetBtnVisible();
			
			BaseMapMain.correction.CORRECTION_ENABLE = true;
			BaseMapMain.correction.SetAllButtonVisible();
			
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "Correction started!");
			finish();
			return true;
		}
		
		return false;
	}	
	
	//判断是否为手机号码
	public boolean isMobileNumber(String mobiles){
		  Pattern p=Pattern.compile("^(((13[0-9])|18[0,5-9]|15[0-3,5-9]|147)\\d{8})|(10086)|(10001)|(5555)|(5556)$");
		  Matcher m=p.matcher(mobiles);
		  if(StaticVar.DEBUG_ENABLE)
			  StaticVar.logPrint('D', "match result:" + m.matches());
		  return m.matches();
	}

}
