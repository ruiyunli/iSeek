package com.izzz.iseek.SMS;

import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.izzz.iseek.R;
import com.izzz.iseek.app.IseekApplication;
import com.izzz.iseek.correction.AlarmControl;
import com.izzz.iseek.maplocate.GPSLocate;
import com.izzz.iseek.vars.PrefHolder;
import com.izzz.iseek.vars.StaticVar;
import com.izzz.iseek.view.LogDialog;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.CursorJoiner.Result;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;



/* �Զ���̳���BroadcastReceiver��,����ϵͳ����㲥����Ϣ */
public class SMSReceiverBaseMap extends BroadcastReceiver 
{ 
	private GPSLocate  gpsLocate = null;

	public SMSReceiverBaseMap(GPSLocate gpsLocate) {
		super();
		this.gpsLocate = gpsLocate;
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
				DialogRefresh(gpsLocate.DialogLocate, R.string.DialogSendOK, StaticVar.COM_SMS_SEND_REFRESH);
			else
			{
				DialogRefresh(gpsLocate.DialogLocate, R.string.DialogSendFail, StaticVar.COM_SMS_SEND_REFRESH);
				gpsLocate.alarmHandler.Stop();
			}
		}
		//���յ�refresh���ͻ�ִ�㲥
		else if (intent.getAction().equals(StaticVar.COM_SMS_DELIVERY_REFRESH))
		{
			if( getResultCode() == Activity.RESULT_OK )
				DialogRefresh(gpsLocate.DialogLocate, R.string.DialogDeliveryOK, StaticVar.COM_SMS_DELIVERY_REFRESH);
			else
			{
				DialogRefresh(gpsLocate.DialogLocate, R.string.DialogDeliveryFail, StaticVar.COM_SMS_DELIVERY_REFRESH);
				gpsLocate.alarmHandler.Stop();
			}
		}
		//���յ�refresh���ӹ㲥
		else if(intent.getAction().equals(StaticVar.COM_ALARM_REFRESH))
		{
			DialogRefresh(gpsLocate.DialogLocate, R.string.DialogAlarmGot, StaticVar.COM_ALARM_REFRESH);
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
					ReceiveMsgCaseLocOK(mesContext, gpsLocate.DialogLocate);
				}
				//����ͷ--gpsû����������
				else if(mesContext.substring(0, 7).equals(StaticVar.SMS_Header_GPS_NOT_FIX))
				{
					ReceiveMstCaseGpsNotFix(context,mesContext,gpsLocate.DialogLocate);
				}
				else
				{
					//����ͷ��ƥ��--Ϊ�˵��Է��㣬���ڽ�Ҫɾ��
					//main�е�ɾ��������setting�еı���
//					if(StaticVar.DEBUG_ENABLE)
//						Toast.makeText(context, "main:unknown sms", Toast.LENGTH_SHORT).show();
				}
				
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
		
		//For example:
		//W00,051,34.234442N,108.913805E,1.574Km/h,13-03-21,16:04:43
		int indexTmp = msgContext.indexOf("N");
		String Latitude = msgContext.substring(8, 16);//ԭ����8-17
		
		//��N���濪ʼ��ȡ����Ϊ����
		String Longitude = msgContext.substring(indexTmp+2, indexTmp+10);//ԭ����19-29
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "OnReceive--Latitude:" + Latitude + " Longitude:" + Longitude);
		
		//����MainActivity�еľ�̬���������õ�ͼ
		if(isValidGeo(Longitude) && isValidGeo(Latitude))
		{
			int LatitudeInt;
			int LongitudeInt;
			
			LatitudeInt = (int)(Double.parseDouble(Latitude) * 1E6);	//�洢��ǰ��λ���
			LongitudeInt = (int)(Double.parseDouble(Longitude) * 1E6);
			
			PrefHolder.prefsEditor.putString(PrefHolder.prefLastLatitudeKey, Integer.toString(LatitudeInt)).commit();
			PrefHolder.prefsEditor.putString(PrefHolder.prefLastLongitudeKey, Integer.toString(LongitudeInt)).commit();
			
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
	
	//���ھ�γ�ȵĺϷ��Լ��
		public boolean isValidGeo(String geoStr)
		{
			boolean isValid = false;
			
			double tmp;
			tmp = Double.parseDouble(geoStr);
			
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "isValidGeo--str:" + geoStr + " tmp:" + tmp);
			
			if((tmp>0) && (tmp<140) )
				isValid = true;		
			return isValid;
		}
	
	
} 




