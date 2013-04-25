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
	
	public LocationClient mLocationClient = null;		//定位相关

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
//		option.setAddrType("all");//返回的定位结果包含地址信息
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(100);//设置发起定位请求的间隔时间为5000ms
		option.disableCache(true);//禁止启用缓存定位
//		option.setPoiNumber(5);	//最多返回POI个数	
//		option.setPoiDistance(1000); //poi查询距离		
		option.setPoiExtraInfo(false); //是否需要POI的电话和地址等详细信息
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
