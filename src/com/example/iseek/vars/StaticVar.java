package com.example.iseek.vars;

import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.iseek.MainActivity;
import com.example.iseek.sms.SMSreceiver;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class StaticVar {

	//�ٶ�Map Key
	public static final String BaiduMapKey = "3200A096EA79B20773CAB5CBD68C2E1ADDDE22BB";
	
	//�˵�order
	public static final int MENU_REFRESH   = 100;
	public static final int MENU_SETTINGS  = 200;
	public static final int MENU_PHONECALL = 300;
	public static final int MENU_TEST      = 400;
	public static final int MENU_EXIT      = 500;
	
	//���Ͷ���
	public static final String SMS_GEO_REQU = "w000000,051";//����λ��
	public static final String SMS_TEST     = "CXGPRS";
	public static final String SMS_SET_SOS  = "w000000,003,3,1,";//����sos����
		
	//���ܶ���Ȩ������-���ͳɹ��㲥
	public static final String SYSTEM_SMS_ACTION        = "android.provider.Telephony.SMS_RECEIVED";
	public static final String COM_SMS_SEND_REFRESH     = "com.example.iseek.sms_send_refresh";
	public static final String COM_SMS_DELIVERY_REFRESH = "com.example.iseek.sms_delivery_refresh";
	public static final String COM_SMS_SEND_SOS         = "com.example.iseek.sms_send_sos";
	public static final String COM_SMS_DELIVERY_SOS     = "com.example.iseek.sms_delivery_sos";
		
	//���ô洢�ļ��ӿ�
	public static SharedPreferences prefs = null;
	public static SharedPreferences.Editor prefsEditor = null;	//û���ϣ���������
	
	//�ٶȵ�ͼ	
	public static MapController mMapController = null;
	public static MyLocationOverlay myLocationOverlay = null;
	public static LocationData tarLocData = null;
	
	//�ؼ���Ӧ��key�ַ�������
	public static String prefTargetPhoneKey = null;
	public static String prefSosNumberKey   = null;
	public static String prefAboutKey       = null;	
	
	//����ͷ�����ַ���
	public static final String SMS_Header_LOC_SUCCESS = "W00,051";
	
	
	
	//��ȡͨѶ¼
	public static final int PICK_CONTACT_REQUEST_TargetPhone = 1;
	public static final int PICK_CONTACT_REQUEST_SosPhone    = 2;
	
	//����TAG
	public static final String LOG_TAG = "iSeekD";

	
	
	//���ŷ��ͺ���
	public static void SendMessage(Context context, String mesContext, String sentIntentStr , String deliveryIntentStr)
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
	    	PendingIntent mPIsend = null;
	    	PendingIntent mPIdelivery = null;
	    	if(sentIntentStr != null)
		        mPIsend = PendingIntent.getBroadcast(context, 0, new Intent(sentIntentStr), 0);
	    	if(deliveryIntentStr != null)
	    		mPIdelivery = PendingIntent.getBroadcast(context, 0, new Intent(deliveryIntentStr), 0);
	    	
		    smsManager.sendTextMessage(strDestAddress, null, strMessage, mPIsend, mPIdelivery);
		    
		    logPrint('D', "number:" + strDestAddress + " context:" + strMessage);
		    logPrint('D', "send message success");
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
	
	//������Ϣ���
	public static void logPrint(char type ,String logContext)
	{
		switch (type)
		{
			case 'D':
				Log.d(LOG_TAG, logContext);
				break;
			case 'E':
				Log.e(LOG_TAG, logContext);
			default :
				Log.e(LOG_TAG, "LOG_TAG Error!");
		}
	}
		
}
