package com.izzz.iseek.map;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.utils.CoordinateConvert;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.iseek.R;
import com.izzz.iseek.SMS.SMSsender;
import com.izzz.iseek.base.BaseMapMain;
import com.izzz.iseek.tools.AlarmControl;
import com.izzz.iseek.tools.LogDialog;
import com.izzz.iseek.vars.StaticVar;

public class GPSLocate {
	
	private Context mContext = null;

	private MapView mMapView = null;
	
	private MyLocationOverlay myLocationOverlay = null; // �ٶȵ�ͼ

	private LocationData tarLocData = null; // �ٶȵ�ͼ
	
	public LogDialog DialogLocate = null;
	
	public SMSsender SenderLocate = null;
	
	public AlarmControl alarmHandler = null;
	
	public GPSLocate(Context mContext, MapView mMapView) {
		super();
		
		this.mContext = mContext;
		
		this.mMapView = mMapView;		
	
		tarLocData = new LocationData();
		
		myLocationOverlay = new MyLocationOverlay(mMapView);
		
		myLocationOverlay.setData(tarLocData);
		
		mMapView.getOverlays().add(myLocationOverlay);
		
		myLocationOverlay.enableCompass();
		
		mMapView.refresh();
		
		DialogLocate = new LogDialog(mContext,R.string.DialogMsgHeader, R.string.DialogTitle);
		
		SenderLocate = new SMSsender(mContext);
		
		alarmHandler = new AlarmControl(mContext, StaticVar.COM_ALARM_REFRESH);
		
	}

	// �����µ�����
	public void animateTo(GeoPoint newPoint) {
		// gps����ϵ���ٶ�����ϵ��ת��
		GeoPoint baiduPoint = CoordinateConvert.fromWgs84ToBaidu(newPoint);

		if (StaticVar.DEBUG_ENABLE)
		{
			StaticVar.logPrint('D', "Latitude:" + baiduPoint.getLatitudeE6() + "  Longitude:" + baiduPoint.getLongitudeE6());
			BaseMapMain.logText.setText("Latitude:" + baiduPoint.getLatitudeE6() + "  Longitude:" + baiduPoint.getLongitudeE6());
		}
		tarLocData.latitude = baiduPoint.getLatitudeE6() / (1E6);
		tarLocData.longitude = baiduPoint.getLongitudeE6() / (1E6);
		tarLocData.accuracy = (float) 31.181425;
		tarLocData.direction = -1.0f;

		myLocationOverlay.setData(tarLocData);
		mMapView.getController().setZoom(16);
		mMapView.refresh();
		mMapView.getController().animateTo(baiduPoint);

	}
	
	public void RefreshLocation()
	{
		//����gpsλ���������
		if(SenderLocate.SendMessage( null, StaticVar.SMS_GEO_REQU, StaticVar.COM_SMS_SEND_REFRESH, 
				StaticVar.COM_SMS_DELIVERY_REFRESH))
		{

			DialogLocate .proMessage = (String) mContext.getResources().getString(R.string.DialogMsgHeader);
			DialogLocate.proLogDialog.setMessage(DialogLocate.proMessage);
			DialogLocate.enable();
			DialogLocate.showLog();
			/*
			alarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(StaticVar.COM_ALARM_REFRESH);
			alarmPI = PendingIntent.getBroadcast(mContext,0,intent,0);
			alarmManager.set(AlarmManager.RTC_WAKEUP, 
					System.currentTimeMillis() + StaticVar.ALARM_TIME, alarmPI);
			*/
			
			alarmHandler.Start();
			
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "alarm for refresh set ok!");
		}
	}

	
	
	
}
