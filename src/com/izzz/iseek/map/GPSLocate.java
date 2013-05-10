package com.izzz.iseek.map;

import android.content.Context;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.utils.CoordinateConvert;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.izzz.iseek.R;
import com.izzz.iseek.SMS.SMSsender;
import com.izzz.iseek.app.IseekApplication;
import com.izzz.iseek.base.BaseMapMain;
import com.izzz.iseek.corr.CorrPointManager;
import com.izzz.iseek.tools.AlarmControl;
import com.izzz.iseek.tools.LogDialog;
import com.izzz.iseek.tools.PrefHolder;
import com.izzz.iseek.vars.StaticVar;

public class GPSLocate {
	
	private Context mContext = null;

	private MapView mMapView = null;
	
	private MyLocationOverlay myLocationOverlay = null; // 百度地图

	private LocationData tarLocData = null; // 百度地图
	
	public LogDialog DialogLocate = null;
	
	public SMSsender SenderLocate = null;
	
	public AlarmControl alarmHandler = null;
	
	private double CorrCoefA = 1.0;
	
	private double CorrCoefB = 0.0;
	
	public boolean isHanCoef = false;
	
	public GPSLocate(Context mContext, MapView mMapView) {
		super();
		
		this.mContext = mContext;
		
		this.mMapView = mMapView;		
	
		InitMap();
		
		InitPlugin();
		
		isHanCoef = InitCoef();
		
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
	
	public boolean InitCoef()
	{
		double[] AB = new double[]{1.0, 0.0};
		AB = CorrPointManager.getCoef();
		CorrCoefA = AB[0];
		CorrCoefB = AB[1];
		
		if((CorrCoefA == 1.0) && (CorrCoefB == 0.0))
			return false;
		else
			return true;
		
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
		

		if(IseekApplication.CORRECTION_ENABLE)
		{
			//插入校准变换
			baiduPoint = CorrectionBaidu(baiduPoint);
			IseekApplication.CORRECTION_USED = true;
			
			if(StaticVar.DEBUG_ENABLE)
			{
				logStr = "corr - ";
				StaticVar.logPrint('D', "corr used");
			}
			
		}
		else
		{
			BaseMapMain.gpsPoint = baiduPoint;
			IseekApplication.CORRECTION_USED = false;
			
			if(StaticVar.DEBUG_ENABLE)
			{
				logStr = "uncorr - ";
				StaticVar.logPrint('D', "corr not used");
			}
		}
		
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
	
	private GeoPoint CorrectionBaidu(GeoPoint pt)
	{
		double longOrigin =  pt.getLongitudeE6()/1E6;
		
		double longNew =  CorrCoefA*longOrigin + CorrCoefB*longOrigin*longOrigin;
		
		if(StaticVar.DEBUG_ENABLE)
		{
			StaticVar.logPrint('D', "CorrCoefA:" + CorrCoefA + "CorrCoefB:" + CorrCoefB);
			StaticVar.logPrint('D', "long origin:" + longOrigin + " long new:" + longNew);
		}
		
		pt.setLongitudeE6((int)(longNew * 1E6));
		
		return pt;
	}

	
	
	
}
