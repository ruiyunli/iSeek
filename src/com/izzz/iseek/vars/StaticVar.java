package com.izzz.iseek.vars;

import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.utils.CoordinateConvert;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.izzz.iseek.map.IseekApplication;
import com.izzz.iseek.map.MainActivity;
import com.izzz.iseek.sms.SMSreceiver;

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

	//百度Map Key
	public static final String BaiduMapKey = "3200A096EA79B20773CAB5CBD68C2E1ADDDE22BB";
	
	//调试设置
	public static boolean DEBUG_ENABLE = true;
	

	
	//菜单order
	public static final int MENU_REFRESH   = 100;
	public static final int MENU_PHONECALL = 200;
	public static final int MENU_SETTINGS  = 300;	
	public static final int MENU_TEST      = 400;
	public static final int MENU_EXIT      = 500;
	
	//发送短信
	public static final String SMS_GEO_REQU = "w000000,051";//请求位置
	public static final String SMS_TEST     = "CXGPRS";
	public static final String SMS_SET_SOS  = "w000000,003,3,1,";//设置sos号码
	public static final String SMS_SET_SOS_TAR  = "用户已经将您的号码绑定为SOS紧急呼叫号码<iSeek>";//设置sos号码
		
	//接受短信权限设置-发送成功广播
	public static final String SYSTEM_SMS_ACTION           	= "android.provider.Telephony.SMS_RECEIVED";
	public static final String COM_SMS_SEND_REFRESH        	= "com.izzz.iseek.sms_send_refresh";
	public static final String COM_SMS_DELIVERY_REFRESH    	= "com.izzz.iseek.sms_delivery_refresh";
	public static final String COM_SMS_SEND_SOS            	= "com.izzz.iseek.sms_send_sos";
	public static final String COM_SMS_DELIVERY_SOS        	= "com.izzz.iseek.sms_delivery_sos";
	public static final String COM_SMS_SEND_SOS_TAR        	= "com.izzz.iseek.sms_send_sos_tar";
	public static final String COM_SMS_DELIVERY_SOS_TAR    	= "com.izzz.iseek.sms_delivery_sos_tar";
	public static final String COM_ALARM_REFRESH   			= "com.izzz.iseek.alarm_refresh";
	public static final String COM_ALARM_SOS_SET   			= "com.izzz.iseek.alarm_sos_set";
	
	//闹钟时间
	public static final int ALARM_TIME = 5*1000;
		
	
	
	
	
	
	
	//短信头解析字符串
	public static final String SMS_Header_LOC_SUCCESS = "W00,051";
	public static final String SMS_Header_GPS_NOT_FIX = "W12,051";
	public static final String SMS_Header_SET_SOS_OK  = "W01,003";
	
	//短信体
	public static final String SMS_BODY_SET_SOS_OK  = "Set phone number  OK";//居然是两个空格，坑
	
	
	//读取通讯录
	public static final int PICK_CONTACT_REQUEST_TargetPhone = 1;
	public static final int PICK_CONTACT_REQUEST_SosPhone    = 2;
	
	//调试TAG
	public static final String LOG_TAG = "iSeekD";

	/*
	//短信发送函数
	//当destNumber为null时，表示发送到targetPhone
	public static boolean SendMessage(Context context, String destNumber, String mesContext, String sentIntentStr , String deliveryIntentStr)
	{	
		//获取手机号码	
		String strDestAddress = null;
		
		if(destNumber != null)
		{
			strDestAddress = destNumber;
		}
		else
		{
			strDestAddress = IseekApplication.getInstance().prefs.getString(IseekApplication.getInstance().prefTargetPhoneKey, "unset");
	
			if(strDestAddress.equals("unset"))
			{				
				Toast.makeText(context, "Please Set Phone Number" , Toast.LENGTH_SHORT).show();				
				return false;
			}
		}
		
		//默认指令，gps回传经度和纬度
	    String strMessage = mesContext; 		  
	    SmsManager smsManager = SmsManager.getDefault();		     
	    try 
	    { 
	    	//发送短信
	    	PendingIntent mPIsend = null;
	    	PendingIntent mPIdelivery = null;
	    	if(sentIntentStr != null)
		        mPIsend = PendingIntent.getBroadcast(context, 0, new Intent(sentIntentStr), 0);
	    	if(deliveryIntentStr != null)
	    		mPIdelivery = PendingIntent.getBroadcast(context, 0, new Intent(deliveryIntentStr), 0);
	    	
		    smsManager.sendTextMessage(strDestAddress, null, strMessage, mPIsend, mPIdelivery);
		    
		    logPrint('D', "number:" + strDestAddress + " context:" + strMessage);
		    logPrint('D', "send message success");
		    return true;
	    } 
	    catch(Exception e) 
	    {
	    	e.printStackTrace();
	    }
	    return false;
	    //Toast.makeText(context, "送出成功!!" , Toast.LENGTH_SHORT).show();
	}
*/
	//调试信息输出
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
