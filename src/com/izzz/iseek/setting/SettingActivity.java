package com.izzz.iseek.setting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.example.iseek.R;
import com.example.iseek.R.string;
import com.example.iseek.R.xml;
import com.izzz.iseek.SMS.SMSreceiver;
import com.izzz.iseek.SMS.SMSsender;
import com.izzz.iseek.app.IseekApplication;
import com.izzz.iseek.base.AboutActivity;
import com.izzz.iseek.base.BaseMapMain;
import com.izzz.iseek.dialog.LogDialog;
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
import android.widget.Toast;

//����ҳ�棬��������ļ�
public class SettingActivity extends PreferenceActivity implements OnPreferenceClickListener,
		OnPreferenceChangeListener{

	IseekApplication app = null;
	
	//��������ҳ��Ŀؼ�
	EditTextPreference prefTargetPhone 	= null;
	EditTextPreference prefSosNumber   	= null;
	PreferenceScreen   prefCorrection 	= null;
	PreferenceScreen   prefAbout       	= null;	
	
	SMSreceiver setReceiver = null;
	IntentFilter setFilter  = null;
	
	public static LogDialog settingDialog = null;
	
	//���Ͷ��Žӿ�
	private SMSsender settingSMSsender = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		app = (IseekApplication)this.getApplication();
		
		//����ҳ����Դ
		addPreferencesFromResource(R.xml.settings);		
		
		settingDialog = new LogDialog(SettingActivity.this, R.string.DialogMsgHeader, R.string.DialogTitle);
		settingDialog.enable();
		
		settingSMSsender = new SMSsender(SettingActivity.this);

		Initprefs();	//��ʼ��prefs
		InitBCR();		//ע��㲥
	}

	private void Initprefs()
	{
		//��ȡ�ؼ�
		prefTargetPhone = (EditTextPreference)findPreference(IseekApplication.prefTargetPhoneKey);
		prefSosNumber   = (EditTextPreference)findPreference(IseekApplication.prefSosNumberKey);
		prefCorrection	= (PreferenceScreen)findPreference(IseekApplication.prefCorrKey);
		prefAbout       = (PreferenceScreen)findPreference(IseekApplication.prefAboutKey);
		
		//�󶨼�����
		prefTargetPhone.setOnPreferenceChangeListener(this);
		prefSosNumber.setOnPreferenceChangeListener(this);
		prefCorrection.setOnPreferenceClickListener(this);
		prefAbout.setOnPreferenceClickListener(this);
		
		prefTargetPhone.setSummary(IseekApplication.prefs.getString(IseekApplication.prefTargetPhoneKey, 
				(String) this.getResources().getText(R.string.set_targetPhone_summary)));
		prefSosNumber.setSummary(IseekApplication.prefs.getString(IseekApplication.prefSosNumberKey, 
				(String) this.getResources().getText(R.string.set_sosNumber_summary)));
				
	}
	
	private void InitBCR()
	{
		//ע��㲥����
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
		//������ʽ�ж��Ƿ�Ϸ��ֻ�����
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
		//�жϷ����ֻ����룬���dialog��ȷ�Ϸ��Ͷ���			
		if(!isMobileNumber(phoneNum))
		{
			Toast.makeText(SettingActivity.this, R.string.ToastInvalidPhoneNumber, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		//��gps���Ͷ���
		if(settingSMSsender.SendMessage(null, StaticVar.SMS_SET_SOS + phoneNum, 
				StaticVar.COM_SMS_SEND_SOS_GPS, StaticVar.COM_SMS_DELIVERY_SOS_GPS))
		{
			
			settingDialog.proMessage = (String) getResources().getText(R.string.DialogMsgHeader);
			settingDialog.proLogDialog.setMessage(settingDialog.proMessage);
			settingDialog.showLog();
			
			//������sos���뷢�Ͷ���
			settingSMSsender.SendMessage(phoneNum, prefTargetPhone.getSummary() + StaticVar.SMS_SET_SOS_TAR , 
					StaticVar.COM_SMS_SEND_SOS_TAR, StaticVar.COM_SMS_DELIVERY_SOS_TAR);
		
			prefSosNumber.setSummary((CharSequence) phoneNum);		
			
			
			IseekApplication.alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(StaticVar.COM_ALARM_SOS_SET);
			IseekApplication.alarmPI = PendingIntent.getBroadcast(this,0,intent,0);
			IseekApplication.alarmManager.set(AlarmManager.RTC_WAKEUP, 
					System.currentTimeMillis() + StaticVar.ALARM_TIME, IseekApplication.alarmPI);
			
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "alarm for sos set start ok!");
			
			return true;
		}		
		return false;
		
	}
	
	//ֵ�ı���Ӧ����
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "Change--key:" + preference.getKey() + "--newValue:" + newValue);
			
		//TargetPhone����
		if(preference.getKey() == IseekApplication.prefTargetPhoneKey)
		{
			return ChangeTargetPhone((String) newValue);
		}
		//SOSphone����
		else if(preference.getKey() == IseekApplication.prefSosNumberKey)
		{
			return ChangeSosPhone((String)newValue);		
		}
		return false;		 
	}

	//�����Ӧ����
	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "Click--key:" + preference.getKey());

		if(preference.getKey() == IseekApplication.prefAboutKey)
		{
			Intent intent = new Intent();
			intent.setClass(SettingActivity.this, AboutActivity.class);
			startActivity(intent);
			return true;
		}
		if(preference.getKey() == IseekApplication.prefCorrKey)
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
	
	//�ж��Ƿ�Ϊ�ֻ�����
	public boolean isMobileNumber(String mobiles){
		  Pattern p=Pattern.compile("^(((13[0-9])|18[0,5-9]|15[0-3,5-9])\\d{8})|(10086)|(10001)|(5555)|(5556)$");
		  Matcher m=p.matcher(mobiles);
		  if(StaticVar.DEBUG_ENABLE)
			  StaticVar.logPrint('D', "match result:" + m.matches());
		  return m.matches();
	}

}
