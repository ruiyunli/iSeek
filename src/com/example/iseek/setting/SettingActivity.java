package com.example.iseek.setting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.example.iseek.MainActivity;
import com.example.iseek.R;
import com.example.iseek.R.string;
import com.example.iseek.R.xml;
import com.example.iseek.sms.SMSreceiver;
import com.example.iseek.vars.StaticVar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
		
		//����ҳ����Դ
		addPreferencesFromResource(R.xml.settings);		
		
		//��ȡ�ؼ�
		prefTargetPhone = (EditTextPreference)findPreference(StaticVar.prefTargetPhoneKey);
		prefSosNumber   = (EditTextPreference)findPreference(StaticVar.prefSosNumberKey);		
		prefAbout       = (PreferenceScreen)findPreference(StaticVar.prefAboutKey);
		
		//�󶨼�����
		prefTargetPhone.setOnPreferenceChangeListener(this);
		prefSosNumber.setOnPreferenceChangeListener(this);
		prefAbout.setOnPreferenceClickListener(this);
		
		//ע��㲥����
		setReceiver = new SMSreceiver();
		setFilter = new IntentFilter();
		setFilter.addAction(StaticVar.COM_SMS_DELIVERY_SOS);
		setFilter.addAction(StaticVar.COM_SMS_SEND_SOS);
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
		
			
		System.out.println("Change--key:" + preference.getKey() + "--newValue:" + newValue);
			
		//TargetPhone����
		if(preference.getKey() == StaticVar.prefTargetPhoneKey)
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
		else if(preference.getKey() == StaticVar.prefSosNumberKey)
		{
			//�жϷ����ֻ����룬���dialog��ȷ�Ϸ��Ͷ���			
			if(isMobileNumber((String) newValue))
			{			
				//������
				//Toast.makeText(SettingActivity.this, "sos number valid", Toast.LENGTH_LONG).show();
				
				StaticVar.SendMessage(SettingActivity.this, StaticVar.SMS_SET_SOS, StaticVar.COM_SMS_SEND_SOS, StaticVar.COM_SMS_DELIVERY_SOS);
				
				prefSosNumber.setSummary((CharSequence) newValue);
				
				setLogMessage = (String) getResources().getText(R.string.DialogMsgHeader);
				setProDialog.setMessage(setLogMessage);
				setProDialog.show();
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
		
		System.out.println("Click--key:" + preference.getKey());			
		
		return true;
	}	
	
	public boolean isMobileNumber(String mobiles){
		  Pattern p=Pattern.compile("^(((13[0-9])|18[0,5-9]|15[0-3,5-9])\\d{8})|(10086)$");//Patterns are compiled regular expressions;
		  Matcher m=p.matcher(mobiles);//matcher:The result of applying a Pattern to a given input;
		  System.out.println(m.matches()+ "---");//Tries to match the Pattern against the entire region ,true if (and only if) the Pattern matches the entire region.
		  return m.matches();
	}

}
