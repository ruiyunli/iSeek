package com.izzz.iseek.tools;

import com.izzz.iseek.vars.StaticVar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmControl {
	
	public Context mContext = null;
	
	public AlarmManager alarmManager = null;
	
	public Intent alarmIntent = null;
	
	public PendingIntent alarmPI = null;
	
	private boolean RunFlag = false;

	public AlarmControl(Context mContext, String IntentActionStr) {
		super();
		this.mContext = mContext;
		// TODO Auto-generated constructor stub
		
		alarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
		
		alarmIntent = new Intent(IntentActionStr);
		
		alarmPI = PendingIntent.getBroadcast(mContext,0,alarmIntent,0);
		
		RunFlag = false;
	}
	
	public void Start()
	{
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + StaticVar.ALARM_TIME, alarmPI);
		RunFlag = true;
	}
	
	public void Stop()
	{
		if(RunFlag)
		{
			alarmManager.cancel(alarmPI);
			RunFlag = false;
		}
	}
	
}
