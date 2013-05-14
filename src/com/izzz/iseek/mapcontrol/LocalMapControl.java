package com.izzz.iseek.mapcontrol;

import java.util.ArrayList;

import com.baidu.mapapi.map.MKOLSearchRecord;
import com.baidu.mapapi.map.MKOLUpdateElement;
import com.baidu.mapapi.map.MKOfflineMap;
import com.baidu.mapapi.map.MKOfflineMapListener;
import com.baidu.mapapi.map.MapController;
import com.izzz.iseek.activity.OfflineManage;


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
	
	//��ȡ���еĸ�����Ϣ
	public ArrayList<MKOLUpdateElement> getAllUpdateInfo()
	{
		return  mOffline.getAllUpdateInfo();
	}
	
	public boolean remove(int cityId)
	{
		return mOffline.remove(cityId);
	}
	
}
