package com.izzz.iseek.map;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.izzz.iseek.base.BaseMapMain;
import com.izzz.iseek.vars.StaticVar;

public class PhoneLocation {
	
	Context mContext = null;
	
	public LocationClient mLocationClient = null;		//��λ���

	public PhoneLocation(Context mContext) {
		super();
		this.mContext = mContext;
		
		mLocationClient = new LocationClient( mContext );
		mLocationClient.registerLocationListener( new MyLocationListenner());
	}
	
	
	public void InitLocationClient()
	{
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
//		option.setAddrType("all");//���صĶ�λ���������ַ��Ϣ
		option.setCoorType("bd09ll");//���صĶ�λ����ǰٶȾ�γ��,Ĭ��ֵgcj02
		option.setScanSpan(100);//���÷���λ����ļ��ʱ��Ϊ5000ms
		option.disableCache(true);//��ֹ���û��涨λ
//		option.setPoiNumber(5);	//��෵��POI����	
//		option.setPoiDistance(1000); //poi��ѯ����		
		option.setPoiExtraInfo(false); //�Ƿ���ҪPOI�ĵ绰�͵�ַ����ϸ��Ϣ
		option.setPriority(LocationClientOption.GpsFirst);
		mLocationClient.setLocOption(option);
		
//		mLocationClient.start();
	}
	
	public void Start()
	{
		mLocationClient.start();
	}
	
	public void Stop()
	{
		if(mLocationClient.isStarted())
			mLocationClient.stop();
		
	}
	
	public void requestLocation()
	{
		if (mLocationClient != null && mLocationClient.isStarted())
			mLocationClient.requestLocation();
		else
		{
			StaticVar.logPrint('D', "locClient is null or not started");
		}
	}
	
	public class MyLocationListenner implements BDLocationListener
	{

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			String locInfo = "Loc||type:" + location.getLocType() + 
					"\nlatitude:" + location.getLatitude() + " longitude:" + location.getLongitude();
			
			BaseMapMain.logText.setText(locInfo);
			if(StaticVar.DEBUG_ENABLE)
			{
				StaticVar.logPrint('D', locInfo);
			}
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}
