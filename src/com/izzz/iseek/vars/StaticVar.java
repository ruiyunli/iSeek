package com.izzz.iseek.vars;

import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.utils.CoordinateConvert;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.izzz.iseek.app.IseekApplication;
import com.izzz.iseek.base.BaseMapMain;
import com.izzz.iseek.receiver.SMSreceiver;

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
	
	//��������
	public static boolean DEBUG_ENABLE = true;
	public static boolean CORRECTION_ENABLE = false;
	
	//��־λ-����У׼��
	public static boolean ADD_LAYER_FLAG = false;
	
	//�˵�order
	public static final int MENU_REFRESH   = 100;
	public static final int MENU_PHONECALL = 200;
	public static final int MENU_SETTINGS  = 300;	
	public static final int MENU_TEST      = 400;
	public static final int MENU_CORR      = 500;
	public static final int MENU_EXIT      = 600;
	
	//У׼΢������
	public static final int MOVE_CORR_UP     = 1;
	public static final int MOVE_CORR_DOWN   = 2;
	public static final int MOVE_CORR_LEFT   = 3;
	public static final int MOVE_CORR_RIGHT  = 4;
	
	//���Ͷ���
	public static final String SMS_GEO_REQU = "w000000,051";//����λ��
	public static final String SMS_TEST     = "CXGPRS";
	public static final String SMS_SET_SOS  = "w000000,003,3,1,";//����sos����
	public static final String SMS_SET_SOS_TAR  = "�û��Ѿ������ĺ����ΪSOS�������к���<iSeek>";//����sos����
		
	//���ܶ���Ȩ������-���ͳɹ��㲥
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
	
	//����ʱ��
	public static final long ALARM_TIME = 3*1000;
		
	
	
	
	
	
	
	//����ͷ�����ַ���
	public static final String SMS_Header_LOC_SUCCESS = "W00,051";
	public static final String SMS_Header_GPS_NOT_FIX = "W12,051";
	public static final String SMS_Header_SET_SOS_OK  = "W01,003";
	
	//������
	public static final String SMS_BODY_SET_SOS_OK  = "Set phone number  OK";//��Ȼ�������ո񣬿�
	
	
	//��ȡͨѶ¼
	public static final int PICK_CONTACT_REQUEST_TargetPhone = 1;
	public static final int PICK_CONTACT_REQUEST_SosPhone    = 2;
	
	//����TAG
	public static final String LOG_TAG = "iSeekD";
	
	//У׼΢������
	public static final int CORR_STEP = 10;

	
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