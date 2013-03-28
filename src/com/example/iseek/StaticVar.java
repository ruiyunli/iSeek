package com.example.iseek;

import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.widget.Toast;

public class StaticVar {

	//�˵�order
	public static final int MENU_REFRESH   = 100;
	public static final int MENU_SETTINGS  = 200;
	public static final int MENU_PHONECALL = 300;
	public static final int MENU_TEST      = 400;
	public static final int MENU_EXIT      = 500;
	
	//���Ͷ���
	public static final String SMS_GEO_REQU = "w000000,051";//����λ��
	public static final String SMS_SET_SOS = "w000000,003,3,1,";//����sos����
	
	//���ܶ���Ȩ������-���ͳɹ��㲥
	public static final String SYSTEM_SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	public static final String COM_SMS_SEND     = "com.example.iseek.sms_send";
	public static final String COM_SMS_DELIVERY = "com.example.iseek.sms_delivery";
		
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
	
	//logDialog�еı���
	public static ProgressDialog logDialog = null;
	public static String logMessage = null;
	
	
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
		    PendingIntent mPIsend     = PendingIntent.getBroadcast(context, 0, new Intent(COM_SMS_SEND), 0);
		    PendingIntent mPIdelivery = PendingIntent.getBroadcast(context, 0, new Intent(COM_SMS_DELIVERY), 0);
		    smsManager.sendTextMessage(strDestAddress, null, strMessage, mPIsend, mPIdelivery);
	    } 
	    catch(Exception e) 
	    {
	    	e.printStackTrace();
	    }
	    //Toast.makeText(context, "�ͳ��ɹ�!!" , Toast.LENGTH_SHORT).show();
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
