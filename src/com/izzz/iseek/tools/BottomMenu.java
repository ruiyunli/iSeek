package com.izzz.iseek.tools;

import com.example.iseek.R;
import com.izzz.iseek.SMS.SMSsender;
import com.izzz.iseek.app.IseekApplication;
import com.izzz.iseek.base.BaseMapMain;
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
	

	
	
	
	public BottomMenu(Context mContext, ImageButton btnMenuCall,
			Button btnMenuRefresh, ImageButton btnMenuSettings ) {
		super();
		this.mContext = mContext;
		this.btnMenuCall = btnMenuCall;
		this.btnMenuRefresh = btnMenuRefresh;
		this.btnMenuSettings = btnMenuSettings;
		
		btnMenuCall.setOnClickListener(new MenuBottomOnClickListner());
		btnMenuRefresh.setOnClickListener(new MenuBottomOnClickListner());
		btnMenuSettings.setOnClickListener(new MenuBottomOnClickListner());
	}

	public void SetVisible()
	{
		btnMenuCall.setVisibility(View.VISIBLE);
		btnMenuRefresh.setVisibility(View.VISIBLE);
		btnMenuSettings.setVisibility(View.VISIBLE);
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
//				MenuRefresh();
				BaseMapMain.gpsLocate.RefreshLocation();
				
			}
			else if(v.getId() == btnMenuSettings.getId())
			{
				MenuSettings();
			}
		}		
	}	
	
	
	
	
}
