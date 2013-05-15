package com.izzz.iseek.activity;

import com.izzz.iseek.R;
import com.izzz.iseek.SMS.SMSReceiverSetting;
import com.izzz.iseek.SMS.SMSsender;
import com.izzz.iseek.app.IseekApplication;
import com.izzz.iseek.correction.AlarmControl;
import com.izzz.iseek.maplocate.GPSLocate;
import com.izzz.iseek.vars.PrefHolder;
import com.izzz.iseek.vars.StaticVar;
import com.izzz.iseek.view.LogDialog;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

//����ҳ�棬���������ļ�
public class SettingActivity extends PreferenceActivity implements OnPreferenceClickListener,
		OnPreferenceChangeListener{

	private EditTextPreference 	prefTargetPhone 	= null;
	
	private EditTextPreference 	prefSosNumber   	= null;
	
	private PreferenceScreen	prefCorrNumber	= null;
	
	private PreferenceScreen   	prefCorrection 	= null;
	
	private CheckBoxPreference 	prefCorrEnable	= null;
	
	private PreferenceScreen   	prefOffline 	= null;
	
	private PreferenceScreen   	prefGuide 	= null;
	
	private PreferenceScreen   	prefAbout = null;
	
	public static LogDialog 	settingDialog = null;
	
	private SMSsender 		settingSMSsender = null;		//���Ͷ��Žӿ�
	
	private SMSReceiverSetting 	setReceiver = null;
	
	private IntentFilter 	setFilter  = null;
	
	public static AlarmControl alarmHandler = null;
	
	ImageButton btnTitleBarSetting = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		boolean isCustom = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); //����ʹ���Զ������
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.settings);		
		
		if(isCustom)
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);//�Զ��岼�ָ�ֵ
		
		InitTitleBar();		
		
		Initprefs();	//��ʼ��prefs
		
		InitBCR();		//ע��㲥
		
		settingDialog = new LogDialog(SettingActivity.this, R.string.DialogMsgHeader, R.string.DialogTitle);
		
		settingSMSsender = new SMSsender(SettingActivity.this);
		
		alarmHandler = new AlarmControl(SettingActivity.this, StaticVar.COM_ALARM_SOS_SET);
	}
	
	
	
	private void InitTitleBar()
	{
		ImageButton btnTitleBar = (ImageButton)findViewById(R.id.btnTitleBar);
		TextView textTitleBar = (TextView)findViewById(R.id.textTitleBar);
		
		//�˴��޸�һ�£���Ӧҳ������ֱ���
		textTitleBar.setText(R.string.TitleBarSetting);
		
		btnTitleBar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	private void Initprefs()
	{
		//��ȡ�ؼ�
		prefTargetPhone = (EditTextPreference)findPreference(PrefHolder.prefTargetPhoneKey);
		prefSosNumber   = (EditTextPreference)findPreference(PrefHolder.prefSosNumberKey);
		prefCorrNumber	= (PreferenceScreen)findPreference(PrefHolder.prefOneKeyNumberKey);
		prefCorrection	= (PreferenceScreen)findPreference(PrefHolder.prefCorrEntryKey);
		prefCorrEnable  = (CheckBoxPreference)findPreference(PrefHolder.prefCorrEnableKey);
		prefOffline		= (PreferenceScreen)findPreference(PrefHolder.prefOfflineKey);
		prefGuide 		= (PreferenceScreen)findPreference(PrefHolder.prefGuideKey);
		prefAbout       = (PreferenceScreen)findPreference(PrefHolder.prefAboutKey);
		
		//�󶨼�����
		prefTargetPhone.setOnPreferenceChangeListener(this);
		prefSosNumber.setOnPreferenceChangeListener(this);
		prefCorrNumber.setOnPreferenceClickListener(this);
		prefCorrection.setOnPreferenceClickListener(this);
		prefCorrEnable.setOnPreferenceChangeListener(this);
		prefOffline.setOnPreferenceClickListener(this);
		prefGuide.setOnPreferenceClickListener(this);
		prefAbout.setOnPreferenceClickListener(this);
		
		prefTargetPhone.setSummary(PrefHolder.prefs.getString(PrefHolder.prefTargetPhoneKey, 
				(String) this.getResources().getText(R.string.set_targetPhone_summary)));
		prefSosNumber.setSummary(PrefHolder.prefs.getString(PrefHolder.prefSosNumberKey, 
				(String) this.getResources().getText(R.string.set_sosNumber_summary)));
				
	}
	
	private void InitBCR()
	{
		//ע��㲥����
		setReceiver = new SMSReceiverSetting();
		setFilter = new IntentFilter();
		setFilter.addAction(StaticVar.SYSTEM_SMS_ACTION);
		setFilter.addAction(StaticVar.COM_SMS_DELIVERY_SOS_GPS);
		setFilter.addAction(StaticVar.COM_SMS_SEND_SOS_GPS);
		setFilter.addAction(StaticVar.COM_SMS_DELIVERY_SOS_TAR);
		setFilter.addAction(StaticVar.COM_SMS_SEND_SOS_TAR);
		setFilter.addAction(StaticVar.COM_ALARM_SOS_SET);
		setFilter.setPriority(StaticVar.SMS_RECEIVER_PRIORITY_2);
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
		//�������ʽ�ж��Ƿ�Ϸ��ֻ�����
		if(SMSsender.isMobileNumber(phoneNum))
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
		
		if(!SMSsender.isMobileNumber(phoneNum))
		{
			Toast.makeText(SettingActivity.this, R.string.ToastInvalidPhoneNumber, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(PrefHolder.getTargetPhone() == null)
		{
			Toast.makeText(SettingActivity.this, R.string.ToastTargetSetEmpty,	Toast.LENGTH_LONG).show();
			return false;
		}
		
		settingDialog.enable();
		settingDialog.proMessage = (String) getResources().getText(R.string.DialogMsgHeader);
		settingDialog.proLogDialog.setMessage(settingDialog.proMessage);
		settingDialog.showLog();
		
		//��gps���Ͷ���
		if(settingSMSsender.SendMessage(null, StaticVar.SMS_SET_SOS + phoneNum, 
				StaticVar.COM_SMS_SEND_SOS_GPS, StaticVar.COM_SMS_DELIVERY_SOS_GPS))
		{
			//������sos���뷢�Ͷ���
			settingSMSsender.SendMessage(phoneNum, prefTargetPhone.getSummary() + StaticVar.SMS_SET_SOS_TAR , 
					StaticVar.COM_SMS_SEND_SOS_TAR, StaticVar.COM_SMS_DELIVERY_SOS_TAR);
			
			prefSosNumber.setSummary((CharSequence) phoneNum);			
			
//			alarmHandler.Start();			
						
			return true;
		}
		return false;
	}
	
	private boolean ToogleCorrBox(Object newValue)
	{
		if(newValue.equals(true))
		{
			if(!GPSLocate.InitCoef())
			{
				Toast.makeText(SettingActivity.this, R.string.ToastCoefNull, Toast.LENGTH_LONG).show();
				
				if(StaticVar.DEBUG_ENABLE)
					StaticVar.logPrint('D', "get corr failed!");
				
				return false;
			}
			else 
				IseekApplication.CORRECTION_ENABLE = true;
		}
		else
			IseekApplication.CORRECTION_ENABLE = false;
		
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "change:" + newValue);
		return true;
	}
	
	//ֵ�ı���Ӧ����
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "Change--key:" + preference.getKey() + "--newValue:" + newValue);
			
		//TargetPhone����
		if(preference.getKey() == PrefHolder.prefTargetPhoneKey)
		{
			return ChangeTargetPhone((String) newValue);
		}
		//SOSphone����
		else if(preference.getKey() == PrefHolder.prefSosNumberKey)
		{
			return ChangeSosPhone((String)newValue);		
		}
		else if(preference.getKey() == PrefHolder.prefCorrEnableKey)
		{
			return ToogleCorrBox(newValue);
		}
		return false;		 
	}
	
	//�����Ӧ����
	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "Click--key:" + preference.getKey());

		//����ҳ��
		if(preference.getKey() == PrefHolder.prefAboutKey)
		{
			Intent intent = new Intent();
			intent.setClass(SettingActivity.this, AboutActivity.class);
			startActivity(intent);
			return true;
		}
		//���߹���ҳ��
		else if(preference.getKey() == PrefHolder.prefOfflineKey)
		{
			Intent intent = new Intent();
			intent.setClass(SettingActivity.this, OfflineManage.class);
			startActivity(intent);
			return true;
		}
		//����ҳ��
		if(preference.getKey() == PrefHolder.prefGuideKey)
		{
			Intent intent = new Intent();
			intent.setClass(SettingActivity.this, AppGuide.class);
			startActivity(intent);
			return true;
		}
		//У׼����
		else if(preference.getKey() == PrefHolder.prefCorrEntryKey)
		{
			Intent intent = new Intent();
			intent.setClass(SettingActivity.this, CorrManage.class);
			startActivity(intent);
			return true;
		}
		//����һ�����ź���
		else if(preference.getKey() == PrefHolder.prefOneKeyNumberKey)
		{
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "set corr number clicked!");
			
			Intent intent = new Intent();
			intent.setClass(SettingActivity.this, SetOneKeyNumber.class);
			startActivity(intent);
			return true;
		}
		
		return false;
	}	
	
}