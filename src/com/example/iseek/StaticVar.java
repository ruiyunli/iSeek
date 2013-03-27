package com.example.iseek;

import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.widget.Toast;

public class StaticVar {

	//�˵�order
	public static final int MENU_REFRESH  = 100;
	public static final int MENU_SETTINGS = 200;
	public static final int MENU_TEST     = 300;
	public static final int MENU_EXIT     = 400;
	
	//���Ͷ���
	public static final String SMS_GEO_REQU = "w000000,051";//����λ��
	public static final String SMS_SET_SOS = "w000000,003,2,1,";//����sos����
	
	//���ܶ���Ȩ������
	public static final String SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED";
		
	//���ô洢�ļ��ӿ�
	public static SharedPreferences prefs = null;
	
	//�ٶȵ�ͼ	
	public static MapController mMapController = null;
	public static MyLocationOverlay myLocationOverlay = null;
	public static LocationData tarLocData = null;
	
	//�ؼ���Ӧ��key�ַ�������
	public static String prefTargetPhoneKey = null;
	public static String prefSosNumberKey   = null;
	public static String prefSaveAllKey     = null;
	public static String prefAboutKey       = null;	
	
	//����ͷ�����ַ���
	public static final String SMS_Header_LOC_SUCCESS = "W00,051";
	
	//���ŷ��ͺ���
	public static void SendMessage(Context context, String mesContext)
	{
		//��ȡ�ֻ�����		
		
		String strDestAddress = StaticVar.prefs.getString(prefTargetPhoneKey, "unset");

		if(strDestAddress.equals("unset"))
		{
			Toast.makeText(context, "Please Set Phone Number" , Toast.LENGTH_SHORT).show();
			return ;
		}
		
		//Ĭ��ָ�gps�ش����Ⱥ�γ��
	    String strMessage = mesContext; 		  
	    SmsManager smsManager = SmsManager.getDefault();		     
	    try 
	    { 
	    	//���Ͷ���
		    PendingIntent mPI = PendingIntent.getBroadcast(context, 0, new Intent(), 0); 
		    smsManager.sendTextMessage(strDestAddress, null, strMessage, mPI, null);
	    } 
	    catch(Exception e) 
	    {
	    	e.printStackTrace();
	    }
	    Toast.makeText(context, "�ͳ��ɹ�!!" , Toast.LENGTH_SHORT).show();
	}
	
	public static void setNewPosition(double Latitude, double Longitude)
	{
		System.out.println("Latitude:" + Latitude + "  Longitude:" + Longitude);
		
		
		tarLocData.latitude = Latitude;
		tarLocData.longitude = Longitude;
		tarLocData.direction = 2.0f;		
			
//		mMapController.setCenter(new GeoPoint((int)(Latitude* 1E6),(int)(Longitude* 1E6)));//���õ�ͼ���ĵ�
		
		myLocationOverlay.setData(StaticVar.tarLocData);
		MainActivity.mMapView.getOverlays().add(StaticVar.myLocationOverlay);
		mMapController.setZoom(18);//���õ�ͼzoom����
		MainActivity.mMapView.refresh();
		mMapController.animateTo(new GeoPoint((int)(Latitude* 1E6),(int)(Longitude* 1E6)));
		
	}
		
}