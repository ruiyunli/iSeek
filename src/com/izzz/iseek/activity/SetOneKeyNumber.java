package com.izzz.iseek.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.izzz.iseek.R;
import com.izzz.iseek.SMS.SMSsender;
import com.izzz.iseek.vars.PrefHolder;
import com.izzz.iseek.vars.StaticVar;

public class SetOneKeyNumber extends Activity{

	private EditText editNumber1 = null;
	private EditText editNumber2 = null;
	private EditText editNumber3 = null;
	
	private Button btnSetNumbers = null;
	
	private TextView textTargetNumber 	= null;
	private TextView textSosNumber 		= null;
	private TextView textOkeyNumber1 	= null;
	private TextView textOkeyNumber2 	= null;
	private TextView textOkeyNumber3 	= null;
	
	private String strOkeyNumber1 		= null;
	private String strOkeyNumber2 		= null;
	private String strOkeyNumber3 		= null;
	
	private boolean isNum1Valid = false;
	private boolean isNum2Valid = false;
	private boolean isNum3Valid = false;
	
	private SMSsender SetOneKeySender = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_onekey);
		
		InitView();
		
		SetOneKeySender = new SMSsender(SetOneKeyNumber.this);
		
	}
	
	@Override
	public void setContentView(int layoutResID) {
		// TODO Auto-generated method stub
		boolean isCustom = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); //声明使用自定义标题  
		super.setContentView(layoutResID);
		if(isCustom)
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);//自定义布局赋值   
		
		ImageButton btnTitleBar = (ImageButton)findViewById(R.id.btnTitleBar);
		TextView textTitleBar = (TextView)findViewById(R.id.textTitleBar);
		
		//此处修改一下，对应页面的文字标题
		textTitleBar.setText(R.string.TitleBarSetNumber);
		
		btnTitleBar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	/**初始化控件*/
	private void InitView()
	{
		
		editNumber1 = (EditText)findViewById(R.id.editNumber1);
		editNumber2 = (EditText)findViewById(R.id.editNumber2);
		editNumber3 = (EditText)findViewById(R.id.editNumber3);
		
		btnSetNumbers = (Button)findViewById(R.id.btnSetNumbers);
		
		textTargetNumber	= (TextView)findViewById(R.id.textTargetNumber);
		textSosNumber		= (TextView)findViewById(R.id.textSosNumber);
		textOkeyNumber1		= (TextView)findViewById(R.id.textOkeyNumber1);
		textOkeyNumber2		= (TextView)findViewById(R.id.textOkeyNumber2);
		textOkeyNumber3		= (TextView)findViewById(R.id.textOkeyNumber3);
		
		btnSetNumbers.setOnClickListener(new setNumOnClickListener());
		
		UpdateView();
	}
	
	private void UpdateView()
	{
		String numStr = null;
		
		numStr = PrefHolder.prefs.getString(PrefHolder.prefTargetPhoneKey, "unset");
		if(!numStr.equals("unset"))	textTargetNumber.setText(numStr);
		
		numStr = PrefHolder.prefs.getString(PrefHolder.prefSosNumberKey, "unset");
		if(!numStr.equals("unset"))	textSosNumber.setText(numStr);
		
		numStr = PrefHolder.prefs.getString(PrefHolder.prefOneKeyNumber1Key, "unset");
		if(!numStr.equals("unset"))	textOkeyNumber1.setText(numStr);
		
		numStr = PrefHolder.prefs.getString(PrefHolder.prefOneKeyNumber2Key, "unset");
		if(!numStr.equals("unset"))	textOkeyNumber2.setText(numStr);
		
		numStr = PrefHolder.prefs.getString(PrefHolder.prefOneKeyNumber3Key, "unset");
		if(!numStr.equals("unset"))	textOkeyNumber3.setText(numStr);
		
	}

	/**设置按钮响应*/
	class setNumOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			String alertMsg = null;
			
			boolean isSimpleChoice = false;	
			
			//尚未设置目标号码
			if(!SMSsender.isMobileNumber(textTargetNumber.getText().toString()))
			{
				Toast.makeText(SetOneKeyNumber.this, R.string.ToastTargetSetEmpty, Toast.LENGTH_LONG).show();
				return;
			}
			
			strOkeyNumber1 = editNumber1.getText().toString().trim();
			isNum1Valid = SMSsender.isMobileNumber(strOkeyNumber1);
			
			strOkeyNumber2 = editNumber2.getText().toString().trim();
			isNum2Valid = SMSsender.isMobileNumber(strOkeyNumber2);
			
			strOkeyNumber3 = editNumber3.getText().toString().trim();
			isNum3Valid = SMSsender.isMobileNumber(strOkeyNumber3);
			
			if((!isNum1Valid) && (!isNum2Valid) && (!isNum3Valid))
			{
				alertMsg = SetOneKeyNumber.this.getResources().getString(R.string.OKeyAlertInValidAll);
				isSimpleChoice = true;
			}
			else if(isNum1Valid && isNum2Valid && isNum3Valid)
				alertMsg = SetOneKeyNumber.this.getResources().getString(R.string.OKeyAlertValidAll);
			else
			{
				alertMsg = SetOneKeyNumber.this.getResources().getString(R.string.OKeyAlertMsgHeader);
				
				if(isNum1Valid)
					alertMsg = alertMsg + "\n  " + "No.1:" + strOkeyNumber1;
				if(isNum2Valid)
					alertMsg = alertMsg + "\n  " + "No.2:" + strOkeyNumber2;
				if(isNum3Valid)
					alertMsg = alertMsg + "\n  " + "No.3:" + strOkeyNumber3;
			}
			
			//警告
			AlertDialog.Builder setAlert =  
					new AlertDialog.Builder(SetOneKeyNumber.this) 
		    		.setTitle(R.string.OKeyAlertTitle)
		    		.setMessage(alertMsg);
			
		    if(isSimpleChoice)
		    {
		    	setAlert.setPositiveButton(R.string.OK_CH, null);
		    }
		    else
		    {
		    	setAlert.setPositiveButton(R.string.OK_CH, new AlertSetOnClickListener());
		    	setAlert.setNegativeButton(R.string.CANCLE_CH, null);
		    }
			
		    setAlert.show();
			
		}
	}
	
	/**确认发送按钮响应*/
	public class AlertSetOnClickListener implements DialogInterface.OnClickListener{
		
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			
			boolean setAllOK = true;
			
			//发送短信进行设置
			if(isNum1Valid)
			{
				if(!SetOneKeySender.SendMessage(null, StaticVar.SMS_SET_ONE_KEY_1 + strOkeyNumber1, null, null))
				{		
					setAllOK = false;
					Toast.makeText(SetOneKeyNumber.this, R.string.ToastOneKeySetNum1Fail, Toast.LENGTH_SHORT).show();
				}
				else
					PrefHolder.prefsEditor.putString(PrefHolder.prefOneKeyNumber1Key, strOkeyNumber1);
			}
			if(isNum2Valid)
			{
				if(!SetOneKeySender.SendMessage(null, StaticVar.SMS_SET_ONE_KEY_2 + strOkeyNumber2, null, null))
				{		
					setAllOK = false;
					Toast.makeText(SetOneKeyNumber.this, R.string.ToastOneKeySetNum2Fail, Toast.LENGTH_SHORT).show();
				}
				else
					PrefHolder.prefsEditor.putString(PrefHolder.prefOneKeyNumber2Key, strOkeyNumber2);
			}
			if(isNum3Valid)
			{
				if(!SetOneKeySender.SendMessage(null, StaticVar.SMS_SET_ONE_KEY_3 + strOkeyNumber3, null, null))
				{		
					setAllOK = false;
					Toast.makeText(SetOneKeyNumber.this, R.string.ToastOneKeySetNum3Fail, Toast.LENGTH_SHORT).show();
				}
				else
					PrefHolder.prefsEditor.putString(PrefHolder.prefOneKeyNumber3Key, strOkeyNumber3);
			}
			
			if(isNum1Valid || isNum2Valid || isNum3Valid)
				PrefHolder.prefsEditor.commit();
			
			if(setAllOK)
				Toast.makeText(SetOneKeyNumber.this, R.string.ToastOneKeySetNumOK, Toast.LENGTH_LONG).show();
			
			UpdateView();		//更新视图
			
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "saved one key number ok!");
		}
	}
	
}
