package com.izzz.iseek.setting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.example.iseek.R;
import com.example.iseek.R.string;
import com.example.iseek.R.xml;
import com.izzz.iseek.app.IseekApplication;
import com.izzz.iseek.base.BaseMapMain;
import com.izzz.iseek.sms.SMSreceiver;
import com.izzz.iseek.sms.SMSsender;
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
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.widget.Toast;

//����ҳ�棬��������ļ�
public class SettingActivity extends PreferenceActivity implements OnPreferenceClickListener,
		OnPreferenceChangeListener{

	IseekApplication app = null;
	
	//��������ҳ��Ŀؼ�
	EditTextPreference prefTargetPhone = null;
	EditTextPreference prefSosNumber   = null;
	PreferenceScreen   prefAbout       = null;	
	
	SMSreceiver setReceiver = null;
	IntentFilter setFilter  = null;
	
	public static ProgressDialog setProDialog = null;
	public static String setLogMessage = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		app = (IseekApplication)this.getApplication();
		
		//����ҳ����Դ
		addPreferencesFromResource(R.xml.settings);		
		
		
		
		//��ȡ�ؼ�
		prefTargetPhone = (EditTextPreference)findPreference(app.prefTargetPhoneKey);
		prefSosNumber   = (EditTextPreference)findPreference(app.prefSosNumberKey);		
		prefAbout       = (PreferenceScreen)findPreference(app.prefAboutKey);
		
		//�󶨼�����
		prefTargetPhone.setOnPreferenceChangeListener(this);
		prefSosNumber.setOnPreferenceChangeListener(this);
		prefAbout.setOnPreferenceClickListener(this);
		
		prefTargetPhone.setSummary(app.prefs.getString(app.prefTargetPhoneKey, 
				(String) this.getResources().getText(R.string.set_targetPhone_summary)));
		prefSosNumber.setSummary(app.prefs.getString(app.prefSosNumberKey, 
				(String) this.getResources().getText(R.string.set_sosNumber_summary)));
		
		//ע��㲥����
		setReceiver = new SMSreceiver();
		setFilter = new IntentFilter();
		setFilter.addAction(StaticVar.COM_SMS_DELIVERY_SOS);
		setFilter.addAction(StaticVar.COM_SMS_SEND_SOS);
		setFilter.addAction(StaticVar.COM_SMS_DELIVERY_SOS_TAR);
		setFilter.addAction(StaticVar.COM_SMS_SEND_SOS_TAR);
		setFilter.addAction(StaticVar.COM_ALARM_SOS_SET);
		SettingActivity.this.registerReceiver(setReceiver, setFilter);
		
		//progressDialog��ʼ��
		setProDialog = new ProgressDialog(this);		
		setProDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);		
		setProDialog.setTitle(getResources().getText(R.string.DialogTitle));
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		SettingActivity.this.unregisterReceiver(setReceiver);
	}

	//ֵ�ı���Ӧ����
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "Change--key:" + preference.getKey() + "--newValue:" + newValue);
			
		//TargetPhone����
		if(preference.getKey() == app.prefTargetPhoneKey)
		{
			//������ʽ�ж��Ƿ�Ϸ��ֻ�����
			if(isMobileNumber((String)newValue))
			{
				Toast.makeText(SettingActivity.this, R.string.ToastTargetSetOK, Toast.LENGTH_LONG).show();
				prefTargetPhone.setSummary((CharSequence) newValue);
				return true;			
			}
			else
			{
				Toast.makeText(SettingActivity.this, R.string.ToastInvalidPhoneNumber, Toast.LENGTH_LONG).show();
				return false;
			}
		}
		//SOSphone����
		else if(preference.getKey() == app.prefSosNumberKey)
		{
			//�жϷ����ֻ����룬���dialog��ȷ�Ϸ��Ͷ���			
			if(isMobileNumber((String) newValue))
			{	
				//δ����targetPhone��ʱ�򲻷��������ź�
				if(isMobileNumber(app.prefs.getString(app.prefTargetPhoneKey, "unset")))
				{
					//��gps���Ͷ���
					SMSsender.SendMessage(SettingActivity.this, null, StaticVar.SMS_SET_SOS + newValue, 
							StaticVar.COM_SMS_SEND_SOS, StaticVar.COM_SMS_DELIVERY_SOS);
					//������sos���뷢�Ͷ���
					SMSsender.SendMessage(SettingActivity.this, (String)newValue, prefTargetPhone.getSummary() + StaticVar.SMS_SET_SOS_TAR , 
							StaticVar.COM_SMS_SEND_SOS_TAR, StaticVar.COM_SMS_DELIVERY_SOS_TAR);
				
				
					prefSosNumber.setSummary((CharSequence) newValue);					
					setLogMessage = (String) getResources().getText(R.string.DialogMsgHeader);
					setProDialog.setMessage(setLogMessage);
					setProDialog.show();
					
					IseekApplication.alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
					Intent intent = new Intent(StaticVar.COM_ALARM_SOS_SET);
					IseekApplication.alarmPI = PendingIntent.getBroadcast(this,0,intent,0);
					IseekApplication.alarmManager.set(AlarmManager.RTC_WAKEUP, 
							System.currentTimeMillis() + StaticVar.ALARM_TIME, IseekApplication.alarmPI);
					
					if(StaticVar.DEBUG_ENABLE)
						StaticVar.logPrint('D', "alarm for sos set start ok!");
					
					return true;
				}
				else
				{
					//��ʾ����target number
					Toast.makeText(this, this.getResources().getText(R.string.ToastTargetSetEmpty), Toast.LENGTH_LONG).show();
				}
				return false;
			}
			else
			{
				Toast.makeText(SettingActivity.this, R.string.ToastInvalidPhoneNumber, Toast.LENGTH_LONG).show();
				return false;
			}			
		}
		return false;		 
	}

	//�����Ӧ����
	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "Click--key:" + preference.getKey());			
		
		return true;
	}	
	
	//�ж��Ƿ�Ϊ�ֻ�����
	public boolean isMobileNumber(String mobiles){
		  Pattern p=Pattern.compile("^(((13[0-9])|18[0,5-9]|15[0-3,5-9])\\d{8})|(10086)$");
		  Matcher m=p.matcher(mobiles);
		  if(StaticVar.DEBUG_ENABLE)
			  StaticVar.logPrint('D', m.matches()+ "---");
		  return m.matches();
	}

}
