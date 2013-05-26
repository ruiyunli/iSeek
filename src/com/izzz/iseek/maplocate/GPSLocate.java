package com.izzz.iseek.maplocate;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.utils.CoordinateConvert;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.izzz.iseek.R;
import com.izzz.iseek.SMS.SMSsender;
import com.izzz.iseek.activity.BaseMapMain;
import com.izzz.iseek.tools.AlarmControl;
import com.izzz.iseek.vars.PrefHolder;
import com.izzz.iseek.vars.StaticVar;
import com.izzz.iseek.view.LogDialog;

public class GPSLocate {
	
	private Context mContext = null;

	private MapView mMapView = null;
	
	private MyLocationOverlay myLocationOverlay = null; // 百度地图

	private LocationData tarLocData = null; // 百度地图
	
	public LogDialog DialogLocate = null;
	
	public SMSsender SenderLocate = null;
	
	public AlarmControl alarmHandler = null;
	
	public boolean isHanCoef = false;
	
	public GPSLocate(Context mContext, MapView mMapView) {
		super();
		
		this.mContext = mContext;
		
		this.mMapView = mMapView;		
	
		InitMap();
		
		InitPlugin();		
		
	}
	
	private void InitMap()
	{
		tarLocData = new LocationData();
		
		myLocationOverlay = new MyLocationOverlay(mMapView);
		
		myLocationOverlay.setData(tarLocData);
		
		mMapView.getOverlays().add(myLocationOverlay);
		
		myLocationOverlay.enableCompass();
		
		mMapView.refresh();
	}
	
	private void InitPlugin()
	{
		DialogLocate = new LogDialog(mContext,R.string.DialogMsgHeader, R.string.DialogTitle);
		
		SenderLocate = new SMSsender(mContext);
		
		alarmHandler = new AlarmControl(mContext, StaticVar.COM_ALARM_REFRESH);
	}
	

	// 设置新的坐标
	public void animateTo(GeoPoint newPoint, boolean GEO_TYPE) {
		// gps坐标系到百度坐标系的转化
		
		GeoPoint baiduPoint;
		String logStr = null;
		
		if(GEO_TYPE == StaticVar.GEO_BAIDU)
			baiduPoint = newPoint;
		else
			baiduPoint = CoordinateConvert.fromWgs84ToBaidu(newPoint);
		
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "OnReceive--LatitudeInt:" + baiduPoint.getLatitudeE6() + " LongitudeInt:" + baiduPoint.getLongitudeE6());
		
		
		//存储百度坐标系下的坐标经纬度
		PrefHolder.prefsEditor.putString(PrefHolder.prefLastLatitudeKey, Integer.toString(baiduPoint.getLatitudeE6()));
		PrefHolder.prefsEditor.putString(PrefHolder.prefLastLongitudeKey, Integer.toString(baiduPoint.getLongitudeE6())).commit();
		
//		BaseMapMain.gpsPoint = baiduPoint;
		
		if (StaticVar.DEBUG_ENABLE)
		{
			logStr = logStr + "Latitude:" + baiduPoint.getLatitudeE6() + "  Longitude:" + baiduPoint.getLongitudeE6();
			StaticVar.logPrint('D', logStr);
			BaseMapMain.logText.setText("Latitude:" + baiduPoint.getLatitudeE6() + "  Longitude:" + baiduPoint.getLongitudeE6());
		}
				
		tarLocData.latitude = baiduPoint.getLatitudeE6() / (1E6);
		tarLocData.longitude = baiduPoint.getLongitudeE6() / (1E6);
		tarLocData.accuracy = (float) 31.181425;
		tarLocData.direction = -1.0f;

		myLocationOverlay.setData(tarLocData);
		mMapView.getController().setZoom(19);
		mMapView.refresh();
		mMapView.getController().animateTo(baiduPoint);
		
		Vibrator  vv=(Vibrator) ((Activity) mContext).getApplication().getSystemService(Service.VIBRATOR_SERVICE);  
		vv.vibrate(500);//震半秒钟  

	}
	
	public void RefreshLocation()
	{
		//发送gps位置请求短信
		if(SenderLocate.SendMessage( null, StaticVar.SMS_GEO_REQU, StaticVar.COM_SMS_SEND_REFRESH, 
				StaticVar.COM_SMS_DELIVERY_REFRESH))
		{

			DialogLocate .proMessage = (String) mContext.getResources().getString(R.string.DialogMsgHeader);
			DialogLocate.proLogDialog.setMessage(DialogLocate.proMessage);
			DialogLocate.enable();
			DialogLocate.showLog();
			
			alarmHandler.Start();
			
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "alarm for refresh set ok!");
		}
	}
	
	

	
	
	
}
