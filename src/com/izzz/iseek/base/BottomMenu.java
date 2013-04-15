package com.izzz.iseek.base;

import com.example.iseek.R;
import com.izzz.iseek.SMS.SMSsender;
import com.izzz.iseek.app.IseekApplication;
import com.izzz.iseek.dialog.LogDialog;
import com.izzz.iseek.setting.SettingActivity;
import com.izzz.iseek.vars.StaticVar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class BottomMenu {
	
	private Context mContext = null;
	
	private ImageButton btnMenuCall     = null;		//菜单
	
	private Button btnMenuRefresh  = null;			//菜单
	
	private ImageButton btnMenuSettings = null;		//菜单
	
	private SMSsender menuSender = null;

	private LogDialog menuDialog = null;

	
	
	
	public BottomMenu(Context mContext, ImageButton btnMenuCall,
			Button btnMenuRefresh, ImageButton btnMenuSettings,
			SMSsender menuSender, LogDialog menuDialog) {
		super();
		this.mContext = mContext;
		this.btnMenuCall = btnMenuCall;
		this.btnMenuRefresh = btnMenuRefresh;
		this.btnMenuSettings = btnMenuSettings;
		this.menuSender = menuSender;
		this.menuDialog = menuDialog;
		
		btnMenuCall.setOnClickListener(new MenuBottomOnClickListner());
		btnMenuRefresh.setOnClickListener(new MenuBottomOnClickListner());
		btnMenuSettings.setOnClickListener(new MenuBottomOnClickListner());
	}

	private void MenuPhoneCall()
	{
		//调用系统打电话程序
		String targetPhone = IseekApplication.prefs.getString(IseekApplication.prefTargetPhoneKey, "unset");
		if(!targetPhone.equals("unset"))
		{
			Intent intent=new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+targetPhone));
			mContext.startActivity(intent);
		}
		else
		{
			Toast.makeText(mContext, R.string.ToastTargetSetEmpty, Toast.LENGTH_LONG).show();
		}
	}
	
	private void MenuRefresh()
	{
		//发送gps位置请求短信
		if(menuSender.SendMessage( null, StaticVar.SMS_GEO_REQU, StaticVar.COM_SMS_SEND_REFRESH, 
				StaticVar.COM_SMS_DELIVERY_REFRESH))
		{

			menuDialog .proMessage = (String) mContext.getResources().getString(R.string.DialogMsgHeader);
			menuDialog.proLogDialog.setMessage(menuDialog.proMessage);
			menuDialog.enable();
			menuDialog.showLog();
			
			IseekApplication.alarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(StaticVar.COM_ALARM_REFRESH);
			IseekApplication.alarmPI = PendingIntent.getBroadcast(mContext,0,intent,0);
			IseekApplication.alarmManager.set(AlarmManager.RTC_WAKEUP, 
					System.currentTimeMillis() + StaticVar.ALARM_TIME, IseekApplication.alarmPI);
			
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "alarm for refresh set ok!");
		}
	}
	
	private void MenuSettings()
	{
		Intent intent = new Intent();
		intent.setClass(mContext, SettingActivity.class);
		mContext.startActivity(intent);
	}
	
	class MenuBottomOnClickListner implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v.getId() == btnMenuCall.getId())
			{
				MenuPhoneCall();
			}
			else if(v.getId() == btnMenuRefresh.getId())
			{
				MenuRefresh();
			}
			else if(v.getId() == btnMenuSettings.getId())
			{
				MenuSettings();
			}
		}		
	}	
	
	
	
	
}
