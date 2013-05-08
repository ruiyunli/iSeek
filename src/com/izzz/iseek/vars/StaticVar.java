package com.izzz.iseek.vars;

import android.util.Log;



public class StaticVar {
	
	public static final String BaiduMapKey = "3200A096EA79B20773CAB5CBD68C2E1ADDDE22BB";	//百度Map Key
	
	public static boolean DEBUG_ENABLE = true;		//调试设置
	
	//菜单order
	public static final int MENU_REFRESH   = 100;
	public static final int MENU_PHONECALL = 200;
	public static final int MENU_SETTINGS  = 300;	
	public static final int MENU_TEST      = 400;
	public static final int MENU_CORR      = 500;
	public static final int MENU_EXIT      = 600;
	
	//校准微调方向
	public static final int MOVE_CORR_UP     = 1;
	public static final int MOVE_CORR_DOWN   = 2;
	public static final int MOVE_CORR_LEFT   = 3;
	public static final int MOVE_CORR_RIGHT  = 4;
	
	//发送短信
	public static final String SMS_GEO_REQU = "w000000,051";//请求位置
	public static final String SMS_TEST     = "CXGPRS";
	public static final String SMS_SET_SOS  = "w000000,003,3,1,";//设置sos号码
	public static final String SMS_SET_SOS_TAR  = "用户已经将您的号码绑定为SOS紧急呼叫号码<iSeek>";//设置sos号码
		
	//接受短信权限设置-发送成功广播
	public static final String SYSTEM_SMS_ACTION           	= "android.provider.Telephony.SMS_RECEIVED";
	public static final String COM_SMS_SEND_REFRESH        	= "com.izzz.iseek.sms_send_refresh";
	public static final String COM_SMS_DELIVERY_REFRESH    	= "com.izzz.iseek.sms_delivery_refresh";
	public static final String COM_SMS_SEND_SOS_GPS         = "com.izzz.iseek.sms_send_sos";
	public static final String COM_SMS_DELIVERY_SOS_GPS     = "com.izzz.iseek.sms_delivery_sos";
	public static final String COM_SMS_SEND_SOS_TAR        	= "com.izzz.iseek.sms_send_sos_tar";
	public static final String COM_SMS_DELIVERY_SOS_TAR    	= "com.izzz.iseek.sms_delivery_sos_tar";
	public static final String COM_ALARM_REFRESH   			= "com.izzz.iseek.alarm_refresh";
	public static final String COM_ALARM_SOS_SET   			= "com.izzz.iseek.alarm_sos_set";
	public static final String COM_ALARM_BACK_EXIT 			= "com.izzz.iseek.alarm_back_exit";
	
	//闹钟时间
	public static final long ALARM_TIME = 1*60*1000;	
	
	//短信头解析字符串
	public static final String SMS_Header_LOC_SUCCESS = "W00,051";
	public static final String SMS_Header_GPS_NOT_FIX = "W12,051";
	public static final String SMS_Header_SET_SOS_OK  = "W01,003";
	
	//短信体
	public static final String SMS_BODY_SET_SOS_OK  = "Set phone number  OK";//居然是两个空格，坑
	public static final String SMS_BODY_GPS_NOT_FIX  = " GPS not fix";//这个待修改
	
	//读取通讯录
	public static final int PICK_CONTACT_REQUEST_TargetPhone = 1;
	public static final int PICK_CONTACT_REQUEST_SosPhone    = 2;
	
	//调试TAG
	public static final String LOG_TAG = "iSeekD";
	
	//校准微调步长
	public static final int CORR_STEP = 10;
	
	//离线地图下载标志
	public static final int OFFLINE_NULL	 = 0;
	public static final int OFFLINE_DOWNLOAD = 1;
	public static final int OFFLINE_UPDATE   = 2;
	
	//坐标类型-百度-wgs84
	public static final boolean GEO_WGS84 = false;
	public static final boolean GEO_BAIDU = true;
	
	//离线地图类型
	public static final int CITYTYPE_COUNTRY = 0;
	public static final int CITYTYPE_PROVIENCE = 1;
	public static final int CITYTYPE_CITY = 2;

	
	
	
	
	/**
	 * 调试信息输出，调用前使用SatticVar.DEBUG_ENABLE进行判断
	 * @param type			D:debug, E:error
	 * @param logContext	内容
	 */
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
