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
	
	//��ʼ��
	public void Init( MKOfflineMapListener mkOfflineMapListener)
	{
		mOffline.init(mMapController,mkOfflineMapListener);
	}
	
	//��ѯ���ж�Ӧid
	public ArrayList<MKOLSearchRecord>  searchCity(String cityName)
	{
		return mOffline.searchCity(cityName);
	}
	
	//����
	public boolean download(int cityId)
	{
		return mOffline.start(cityId);
	}
	
	//��ȡ����������Ϣ
	public MKOLUpdateElement getUpdateInfo(int state)
	{
		return mOffline.getUpdateInfo(state);
	}
	
}
