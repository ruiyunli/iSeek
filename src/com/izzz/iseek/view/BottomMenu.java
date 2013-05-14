package com.izzz.iseek.view;

import com.izzz.iseek.R;
import com.izzz.iseek.activity.SettingActivity;
import com.izzz.iseek.maplocate.GPSLocate;
import com.izzz.iseek.vars.PrefHolder;

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
	
	private GPSLocate gpsLocate = null;
	
	public BottomMenu(Context mContext, ImageButton btnMenuCall,
			Button btnMenuRefresh, ImageButton btnMenuSettings, GPSLocate gpsLocate ) {
		super();
		this.mContext = mContext;
		this.btnMenuCall = btnMenuCall;
		this.btnMenuRefresh = btnMenuRefresh;
		this.btnMenuSettings = btnMenuSettings;
		this.gpsLocate = gpsLocate;
		
		btnMenuCall.setOnClickListener(new MenuBottomOnClickListner());
		btnMenuRefresh.setOnClickListener(new MenuBottomOnClickListner());
		btnMenuSettings.setOnClickListener(new MenuBottomOnClickListner());
	}

	public void SetVisible(boolean isVisible)
	{
		if(isVisible)
		{
			btnMenuCall.setVisibility(View.VISIBLE);
			btnMenuRefresh.setVisibility(View.VISIBLE);
			btnMenuSettings.setVisibility(View.VISIBLE);
		}
		else
		{
			btnMenuCall.setVisibility(View.INVISIBLE);
			btnMenuRefresh.setVisibility(View.INVISIBLE);
			btnMenuSettings.setVisibility(View.INVISIBLE);
		}
	}
	
	private void MenuPhoneCall()
	{
		//调用系统打电话程序
		String targetPhone = PrefHolder.prefs.getString(PrefHolder.prefTargetPhoneKey, "unset");
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
				gpsLocate.RefreshLocation();
				
			}
			else if(v.getId() == btnMenuSettings.getId())
			{
				MenuSettings();
			}
		}		
	}	
	
	
	
	
}
