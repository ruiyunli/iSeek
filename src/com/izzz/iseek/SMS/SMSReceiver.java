package com.izzz.iseek.SMS;

import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.izzz.iseek.R;
import com.izzz.iseek.app.IseekApplication;
import com.izzz.iseek.maplocate.GPSLocate;
import com.izzz.iseek.vars.PrefHolder;
import com.izzz.iseek.vars.StaticVar;
import com.izzz.iseek.view.LogDialog;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;



/* �Զ���̳���BroadcastReceiver��,����ϵͳ����㲥����Ϣ */
public class SMSReceiver extends BroadcastReceiver 
{ 
	private GPSLocate  gpsLocate = null;
	
	private LogDialog logDialog = null;

	public SMSReceiver(GPSLocate gpsLocate) {
		super();
		this.gpsLocate = gpsLocate;
		this.logDialog = gpsLocate.DialogLocate;
	}
	
	public SMSReceiver(LogDialog logDialog) {
		super();
		this.logDialog = logDialog;
	}

	@Override 
	public void onReceive(Context context, Intent intent) 
	{ 
		// TODO Auto-generated method stub 	
		
		//ϵͳ���յ����ţ�����
		if (intent.getAction().equals(StaticVar.SYSTEM_SMS_ACTION)) 
		{ 
			ReceiveMsgCase(context, intent);
		}
		
		//���յ�refresh����״̬�㲥
		else if (intent.getAction().equals(StaticVar.COM_SMS_SEND_REFRESH))
		{
			if( getResultCode() == Activity.RESULT_OK )
				DialogRefresh(logDialog, R.string.DialogSendOK, StaticVar.COM_SMS_SEND_REFRESH);
			else
			{
				DialogRefresh(logDialog, R.string.DialogSendFail, StaticVar.COM_SMS_SEND_REFRESH);
				gpsLocate.alarmHandler.Stop();
			}
		}
		//���յ�refresh���ͻ�ִ�㲥
		else if (intent.getAction().equals(StaticVar.COM_SMS_DELIVERY_REFRESH))
		{
			if( getResultCode() == Activity.RESULT_OK )
				DialogRefresh(logDialog, R.string.DialogDeliveryOK, StaticVar.COM_SMS_DELIVERY_REFRESH);
			else
			{
				DialogRefresh(logDialog, R.string.DialogDeliveryFail, StaticVar.COM_SMS_DELIVERY_REFRESH);
				gpsLocate.alarmHandler.Stop();
			}
		}
		//���յ�refresh���ӹ㲥
		else if(intent.getAction().equals(StaticVar.COM_ALARM_REFRESH))
		{
			DialogRefresh(logDialog, R.string.DialogAlarmGot, StaticVar.COM_ALARM_REFRESH);
		}
		//���յ�sos���÷���״̬�㲥
		else if(intent.getAction().equals(StaticVar.COM_SMS_SEND_SOS_GPS))
		{
			if(getResultCode() == Activity.RESULT_OK)
				DialogRefresh(logDialog, R.string.DialogSosSendGpsOK, StaticVar.COM_SMS_SEND_SOS_GPS);
			else
				DialogRefresh(logDialog, R.string.DialogSosSendGpsFail, StaticVar.COM_SMS_SEND_SOS_GPS);
		}
		//���յ�sos���÷��ͻ�ִ�㲥
		else if(intent.getAction().equals(StaticVar.COM_SMS_DELIVERY_SOS_GPS))
		{
			if(getResultCode() == Activity.RESULT_OK)
				DialogRefresh(logDialog, R.string.DialogSosDeliveryGpsOK, StaticVar.COM_SMS_DELIVERY_SOS_GPS);
			else
				DialogRefresh(logDialog, R.string.DialogSosDeliveryGpsFail, StaticVar.COM_SMS_DELIVERY_SOS_GPS);
		}
		//���յ�sos�ֻ��ŵ�֪ͨ����״̬�㲥
		else if(intent.getAction().equals(StaticVar.COM_SMS_SEND_SOS_TAR))
		{
			if(getResultCode() == Activity.RESULT_OK)
				DialogRefresh(logDialog, R.string.DialogSosSendTarOK, StaticVar.COM_SMS_DELIVERY_SOS_TAR);
			else
				DialogRefresh(logDialog, R.string.DialogSosSendTarFail, StaticVar.COM_SMS_DELIVERY_SOS_TAR);
		}
		//���յ�sos�ֻ��ŵ�֪ͨ��ִ�㲥
		else if(intent.getAction().equals(StaticVar.COM_SMS_DELIVERY_SOS_TAR))
		{
			if(getResultCode() == Activity.RESULT_OK)
				DialogRefresh(logDialog, R.string.DialogSosDeliveryTarOK, StaticVar.COM_SMS_DELIVERY_SOS_TAR);
			else
				DialogRefresh(logDialog, R.string.DialogSosDeliveryTarFail, StaticVar.COM_SMS_DELIVERY_SOS_TAR);
		}
	}
		
	/**
	 * ����log���ҳ���ͨ�ýӿ�--message����
	 * @param logDialog
	 * @param strAppendId
	 * @param strCase
	 */
	private void DialogRefresh(LogDialog logDialog, int strAppendId, String strCase)
	{
			if(StaticVar.DEBUG_ENABLE)
			{
				if(getResultCode() == Activity.RESULT_OK || strCase == StaticVar.COM_ALARM_REFRESH)
					StaticVar.logPrint('D', "receive sucess in  " + strCase);
				else
					StaticVar.logPrint('D', "receive fail in  " + strCase);
			}
			logDialog.DialogUpdate(strAppendId);		
	}	
	
	
	//��ϵͳ���ն��Ž��й��˺ͽ���
	private void ReceiveMsgCase(Context context, Intent intent)
	{
		String mesNumber;
		String mesContext;
		
		//������Intent����������
		Bundle bundle = intent.getExtras(); 
		
		//�ж�Intent��������
		if (bundle != null) 
		{ 
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "bundle is not null!");
			
			//pdusΪ android�ڽ����Ų��� identifier
			//͸��bundle.get("")����һ������pdus�Ķ���
			Object[] myOBJpdus = (Object[]) bundle.get("pdus");
			
			//�������Ŷ���array,�������յ��Ķ��󳤶�������array�Ĵ�С
			SmsMessage[] messages = new SmsMessage[myOBJpdus.length];	          
          	messages[0] = SmsMessage.createFromPdu ((byte[]) myOBJpdus[0]); 
			
			mesNumber = new String(messages[0].getDisplayOriginatingAddress()); 
			
			String targetPhone = PrefHolder.prefs.getString(
					PrefHolder.prefTargetPhoneKey,"unset");	
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "mesNumber:" + mesNumber + " targetPhone:" + targetPhone);
			
			//�е��ֻ���ȡ�������"+86"�������еĲ����У��ж��Ƿ��������������
			if(mesNumber.equals(targetPhone) || mesNumber.equals("+86" + targetPhone))
			{
			
				mesContext = new String(messages[0].getDisplayMessageBody());
				if(StaticVar.DEBUG_ENABLE)
				{
					StaticVar.logPrint('D', "mesNumber:" + mesNumber);
					StaticVar.logPrint('D', "mesContext:" + mesContext);
					StaticVar.logPrint('D', "SMS header:" + mesContext.substring(0, 7));
				}
				//����ͷ--��λ�ɹ�����
				if(mesContext.substring(0, 7).equals(StaticVar.SMS_Header_LOC_SUCCESS))
				{					
					ReceiveMsgCaseLocOK(mesContext, logDialog);
				}
				//����ͷ--gpsû����������
				else if(mesContext.substring(0, 7).equals(StaticVar.SMS_Header_GPS_NOT_FIX))
				{
					ReceiveMstCaseGpsNotFix(context,mesContext,logDialog);
				}
				//����ͷ--����sos�����gps�ظ���Ϣ
				else if(mesContext.substring(0,7).equals(StaticVar.SMS_Header_SET_SOS_OK))
				{
					if(StaticVar.DEBUG_ENABLE)
						StaticVar.logPrint('D', "set sos ok case!");
					ReceiveMsgCaseSetSosOK(context, mesContext , logDialog );
				}
				else
				{
					//����ͷ��ƥ��--Ϊ�˵��Է��㣬���ڽ�Ҫɾ��
					if(StaticVar.DEBUG_ENABLE)
					{
						StaticVar.logPrint('D', "set sos error case!");
					
						Toast.makeText(context, context.toString() + "unknown sms", Toast.LENGTH_SHORT).show();
					}
				}
				
				//���ٹ㲥��Ϣ��ȡ������
				if(!StaticVar.DEBUG_ENABLE)
					abortBroadcast();
				
			}
			else
			{
				//�ֻ����벻ƥ��
				mesContext = new String(messages[0].getDisplayMessageBody());
				if(StaticVar.DEBUG_ENABLE)
				{
					StaticVar.logPrint('D', "SMSreceiver:different targetPhone");					
					StaticVar.logPrint('D', "diff-mesNumber:" + mesNumber);
					StaticVar.logPrint('D', "diff-mesContext:" + mesContext);
				}
				return ;
			}
		}
		else
		{
			//bundle��Ϊ��
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "bundle is null");
		}   
	}
	
	//��λ�ɹ�
	private void ReceiveMsgCaseLocOK(String msgContext, LogDialog logDialog)
	{
		//������γ��
		
		/*
		//For example:
		//W00,051,34.234442N, 108.913805E,1.574Km/h,13-03-21,16:04:43
		int indexTmp = msgContext.indexOf("N");
		String Latitude = msgContext.substring(indexTmp-9, indexTmp);//ԭ����8-17
		
		//��N���濪ʼ��ȡ����Ϊ����
		String Longitude = msgContext.substring(indexTmp+3, indexTmp+13);//ԭ����19-29
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "OnReceive--Latitude:" + Latitude + " Longitude:" + Longitude);
		
		//�����Ч��
		double LatitudeDou = Double.parseDouble(Latitude);
		double LongitudeDou = Double.parseDouble(Longitude);
		*/
		
		
		//������Ե�Ƭ�����ݽ���
		String[] gprmc = msgContext.split(",");
//		System.out.println(gprmc[5]+":"+gprmc[7]);
		double latitudeData = Double.parseDouble(gprmc[5])/100;
		
		double LatitudeDou = (double) (Math.floor(latitudeData)+(latitudeData-Math.floor(latitudeData))*100/60);
//		System.out.println(LatitudeDou);
		
		double longitudeData=Double.parseDouble(gprmc[7])/100;
		double LongitudeDou = (double) (Math.floor(longitudeData)+(longitudeData-Math.floor(longitudeData))*100/60);
//		System.out.println(LongitudeDou);
		
		if(StaticVar.DEBUG_ENABLE)
		{
			StaticVar.logPrint('D', "latitude:" + LatitudeDou + " longitude:" + LongitudeDou);
		}
		
		
		if(isValidGeo(LatitudeDou) && isValidGeo(LongitudeDou))
		{
			int LatitudeInt;
			int LongitudeInt;
			
			LatitudeInt = (int)(LatitudeDou * 1E6);	
			LongitudeInt = (int)(LongitudeDou * 1E6);
			
			gpsLocate.alarmHandler.Stop();							//�ر�����
			
			logDialog.dismissLog();												//�Ի�����
			logDialog.disable();
			
			IseekApplication.GPS_LOCATE_OK = true;								//ȫ�ֱ�־
			
			GeoPoint tmpPoint = new GeoPoint(LatitudeInt, LongitudeInt);		//��ָ������	
			
			gpsLocate.animateTo(tmpPoint,StaticVar.GEO_WGS84);	
			
		}
	}
	
	private void ReceiveMstCaseGpsNotFix(Context context, String msgContext, LogDialog logDialog)
	{
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', msgContext.substring(8));
		if(msgContext.substring(8).equals(StaticVar.SMS_BODY_GPS_NOT_FIX))
		{
			DialogRefresh(logDialog, R.string.DialogGpsNotFix, msgContext.substring(8));
		}
	}
	
	//����sos����ɹ�
	private void ReceiveMsgCaseSetSosOK(Context context, String msgContext, LogDialog logDialog)
	{
		
		//if(msgContext.substring(8).equals(StaticVar.SMS_BODY_SET_SOS_OK))
		//{
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "sos number set sucess!");
			
//				SettingActivity.alarmHandler.Stop();
			DialogRefresh(logDialog, R.string.DialogSosFeedBackGpsOK, StaticVar.SMS_BODY_SET_SOS_OK);
			logDialog.disable();
	//	}
	}
	
	//���ھ�γ�ȵĺϷ��Լ��
	public boolean isValidGeo(double geoPt)
	{
//		boolean isValid = false;
		
		//double tmp;
//		tmp = Double.parseDouble(geoStr);
		
//		if(StaticVar.DEBUG_ENABLE)
//			StaticVar.logPrint('D', "isValidGeo--str:" + geoStr + " tmp:" + tmp);
		
		if((geoPt>0) && (geoPt<140) )
			return true;
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "not valid pt:" + geoPt);
		return false;
	}
	
	
} 




