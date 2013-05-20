package com.izzz.iseek.activity;

import com.izzz.iseek.R;
import com.izzz.iseek.SMS.SMSReceiverSetting;
import com.izzz.iseek.SMS.SMSsender;
import com.izzz.iseek.app.IseekApplication;
import com.izzz.iseek.maplocate.GPSLocate;
import com.izzz.iseek.vars.PrefHolder;
import com.izzz.iseek.vars.StaticVar;
import com.izzz.iseek.view.EditTextPref;
import com.izzz.iseek.view.LogDialog;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//设置页面，添加配置文件
public class SettingActivity extends PreferenceActivity implements OnPreferenceClickListener,
		OnPreferenceChangeListener{

	private PreferenceScreen 	prefTargetPhone 	= null;
	
	private PreferenceScreen 	prefSosNumber   	= null;
	
	private PreferenceScreen	prefCorrNumber	= null;
	
	private PreferenceScreen   	prefCorrection 	= null;
	
	private CheckBoxPreference 	prefCorrEnable	= null;
	
	private PreferenceScreen   	prefOffline 	= null;
	
	private PreferenceScreen   	prefGuide 	= null;
	
	private PreferenceScreen   	prefAbout = null;
	
	public LogDialog settingDialog = null;
	
	private SMSsender 		settingSMSsender = null;		//发送短信接口
	
	private SMSReceiverSetting 	setReceiver = null;
	
	private IntentFilter 	setFilter  = null;
	
//	public static AlarmControl alarmHandler = null;
	
	private EditTextPref editPrefTarget = null;
	
	private EditTextPref editPrefSos = null;
	
	private ImageButton btnTitleBarSetting = null;
	
	private final int PICK_CONTACT_REQUEST_TAR = 1;
	
	private final int PICK_CONTACT_REQUEST_SOS = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		boolean isCustom = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); //声明使用自定义标题
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.settings);		
		
		if(isCustom)
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);//自定义布局赋值
		
		InitTitleBar();		
		
		Initprefs();	//初始化prefs
		
		InitEditPrefs();
		
		
		settingDialog = new LogDialog(SettingActivity.this, R.string.DialogMsgHeader, R.string.DialogTitle);
		
		settingSMSsender = new SMSsender(SettingActivity.this);
		
//		alarmHandler = new AlarmControl(SettingActivity.this, StaticVar.COM_ALARM_SOS_SET);
		
		InitBCR();		//注册广播
	}
	
	
	
	private void InitTitleBar()
	{
		ImageButton btnTitleBar = (ImageButton)findViewById(R.id.btnTitleBar);
		TextView textTitleBar = (TextView)findViewById(R.id.textTitleBar);
		
		//此处修改一下，对应页面的文字标题
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
		//获取控件
		prefTargetPhone = (PreferenceScreen)findPreference(PrefHolder.prefTargetPhoneKey);
		prefSosNumber   = (PreferenceScreen)findPreference(PrefHolder.prefSosNumberKey);
		prefCorrNumber	= (PreferenceScreen)findPreference(PrefHolder.prefOneKeyNumberKey);
		prefCorrection	= (PreferenceScreen)findPreference(PrefHolder.prefCorrEntryKey);
		prefCorrEnable  = (CheckBoxPreference)findPreference(PrefHolder.prefCorrEnableKey);
		prefOffline		= (PreferenceScreen)findPreference(PrefHolder.prefOfflineKey);
		prefGuide 		= (PreferenceScreen)findPreference(PrefHolder.prefGuideKey);
		prefAbout       = (PreferenceScreen)findPreference(PrefHolder.prefAboutKey);
		
		//绑定监听器
		prefTargetPhone.setOnPreferenceClickListener(this);
		prefSosNumber.setOnPreferenceClickListener(this);
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
	
	private void InitEditPrefs()
	{
		editPrefTarget = new EditTextPref(SettingActivity.this);
		editPrefSos = new EditTextPref(SettingActivity.this);
		
		editPrefTarget.setTitle(R.string.set_targetPhone_dialogTitle);
		editPrefTarget.setContactPickerListener(new DialogContactPickerListener(PICK_CONTACT_REQUEST_TAR));
		editPrefTarget.setPositiveButton(new TargetNumberClickListener());
		
		editPrefSos.setTitle(R.string.set_sosNumber_dialogTitle);
		editPrefSos.setContactPickerListener(new DialogContactPickerListener(PICK_CONTACT_REQUEST_SOS));
		editPrefSos.setPositiveButton(new SosNumberClickListener());
		
	}
	
	private void InitBCR()
	{
		//注册广播监听
		setReceiver = new SMSReceiverSetting(settingDialog);
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
	
	//值改变响应函数
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "Change--key:" + preference.getKey() + "--newValue:" + newValue);

		if(preference.getKey() == PrefHolder.prefCorrEnableKey)
		{
			return ToogleCorrBox(newValue);
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
		if(preference.getKey() == PrefHolder.prefAboutKey)
		{
			Intent intent = new Intent();
			intent.setClass(SettingActivity.this, AboutActivity.class);
			startActivity(intent);
			return true;
		}
		//离线管理页面
		else if(preference.getKey() == PrefHolder.prefOfflineKey)
		{
			Intent intent = new Intent();
			intent.setClass(SettingActivity.this, OfflineManage.class);
			startActivity(intent);
			return true;
		}
		//关于页面
		if(preference.getKey() == PrefHolder.prefGuideKey)
		{
			Intent intent = new Intent();
			intent.setClass(SettingActivity.this, AppGuide.class);
			startActivity(intent);
			return true;
		}
		//校准设置
		else if(preference.getKey() == PrefHolder.prefCorrEntryKey)
		{
			Intent intent = new Intent();
			intent.setClass(SettingActivity.this, CorrManage.class);
			startActivity(intent);
			return true;
		}
		//设置一键拨号号码
		else if(preference.getKey() == PrefHolder.prefOneKeyNumberKey)
		{
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "set corr number clicked!");
			
			Intent intent = new Intent();
			intent.setClass(SettingActivity.this, SetOneKeyNumber.class);
			startActivity(intent);
			return true;
		}
		else if(preference.getKey() == PrefHolder.prefTargetPhoneKey)
		{
			editPrefTarget.RefreshView();
			editPrefTarget.show();
			return true;
		}
		else if(preference.getKey() == PrefHolder.prefSosNumberKey)
		{
			editPrefSos.RefreshView();
			editPrefSos.show();
			return true;
		}
		return false;
	}


	public class DialogContactPickerListener implements OnClickListener {
		
		private int contactRequestId;
		
		public DialogContactPickerListener(int contactRequestId) {
			super();
			this.contactRequestId = contactRequestId;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		    pickContactIntent.setType(Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
		    startActivityForResult(pickContactIntent, contactRequestId);
		}
	}
	
	public class TargetNumberClickListener implements DialogInterface.OnClickListener{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			
			String phoneNum = editPrefTarget.getText();
			
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "phone number:" + phoneNum);
			
			//正则表达式判断是否合法手机号码
			if(SMSsender.isMobileNumber(phoneNum))
			{
				PrefHolder.prefsEditor.putString(PrefHolder.prefTargetPhoneKey, phoneNum).commit();
				Toast.makeText(SettingActivity.this, R.string.ToastTargetSetOK, Toast.LENGTH_SHORT).show();
				prefTargetPhone.setSummary((CharSequence) phoneNum);
			}
			else
			{
				Toast.makeText(SettingActivity.this, R.string.ToastInvalidPhoneNumber, Toast.LENGTH_SHORT).show();
			}
			
		}	
	}
	
	public class SosNumberClickListener implements DialogInterface.OnClickListener{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			//判断符合手机号码，则打开dialog，确认发送短信	
			
			String phoneNum = editPrefSos.getText();
			
			if(!SMSsender.isMobileNumber(phoneNum))
			{
				Toast.makeText(SettingActivity.this, R.string.ToastInvalidPhoneNumber, Toast.LENGTH_SHORT).show();
				return ;
			}
			
			if(PrefHolder.getTargetPhone() == null)
			{
				Toast.makeText(SettingActivity.this, R.string.ToastTargetSetEmpty,	Toast.LENGTH_LONG).show();
				return ;
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
				
				PrefHolder.prefsEditor.putString(PrefHolder.prefSosNumberKey, phoneNum);
				prefSosNumber.setSummary((CharSequence) phoneNum);			
				
//				alarmHandler.Start();	
				
			}
		}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            String[] projectionNumber = {Phone.NUMBER};

            Cursor cursor = getContentResolver().query(contactUri, projectionNumber, null, null, null);
            cursor.moveToFirst();
            int columnNumber = cursor.getColumnIndex(Phone.NUMBER);	            
            String phoneNumber = cursor.getString(columnNumber);
           
            editPrefTarget.setText(phoneNumber);

        }
		
	}	
	
	
	
	
}
