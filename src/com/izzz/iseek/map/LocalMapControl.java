package com.izzz.iseek.map;

import java.util.ArrayList;

import com.baidu.mapapi.map.MKOLSearchRecord;
import com.baidu.mapapi.map.MKOLUpdateElement;
import com.baidu.mapapi.map.MKOfflineMap;
import com.baidu.mapapi.map.MKOfflineMapListener;
import com.baidu.mapapi.map.MapController;


public class LocalMapControl {

	private MKOfflineMap mOffline = null;
	
	private MapController mMapController = null;

	public LocalMapControl(MapController mapController) {
		super();
		this.mMapController = mapController;
		mOffline = new MKOfflineMap();     
		
	}	
	
	//初始化
	public void Init( MKOfflineMapListener mkOfflineMapListener)
	{
		mOffline.init(mMapController,mkOfflineMapListener);
	}
	
	//查询城市对应id
	public ArrayList<MKOLSearchRecord>  searchCity(String cityName)
	{
		return mOffline.searchCity(cityName);
	}
	
	//下载
	public boolean download(int cityId)
	{
		return mOffline.start(cityId);
	}
	
	//获取正在下载信息
	public MKOLUpdateElement getUpdateInfo(int state)
	{
		return mOffline.getUpdateInfo(state);
	}
	
}
