package com.izzz.iseek.SMS;

import com.izzz.iseek.R;
import com.izzz.iseek.activity.SettingActivity;
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
public class SMSReceiverSetting extends BroadcastReceiver 
{ 
	
	@Override 
	public void onReceive(Context context, Intent intent) 
	{ 
		// TODO Auto-generated method stub 	
		
		//ϵͳ���յ����ţ�����
		if (intent.getAction().equals(StaticVar.SYSTEM_SMS_ACTION)) 
		{ 
			ReceiveMsgCase(context, intent);
		}
		//���յ�sos���÷���״̬�㲥
		else if(intent.getAction().equals(StaticVar.COM_SMS_SEND_SOS_GPS))
		{
			DialogRefresh(SettingActivity.settingDialog, R.string.DialogSosSendGpsOK, StaticVar.COM_SMS_SEND_SOS_GPS);
			
		}
		//���յ�sos���÷��ͻ�ִ�㲥
		else if(intent.getAction().equals(StaticVar.COM_SMS_DELIVERY_SOS_GPS))
		{
			DialogRefresh(SettingActivity.settingDialog, R.string.DialogSosDeliveryGpsOK, StaticVar.COM_SMS_DELIVERY_SOS_GPS);
		}
		//���յ�sos�ֻ��ŵ�֪ͨ����״̬�㲥
		else if(intent.getAction().equals(StaticVar.COM_SMS_SEND_SOS_TAR))
		{
			DialogRefresh(SettingActivity.settingDialog, R.string.DialogSosSendTarOK, StaticVar.COM_SMS_DELIVERY_SOS_TAR);
		}
		//���յ�sos�ֻ��ŵ�֪ͨ��ִ�㲥
		else if(intent.getAction().equals(StaticVar.COM_SMS_DELIVERY_SOS_TAR))
		{
			DialogRefresh(SettingActivity.settingDialog, R.string.DialogSosDeliveryTarOK, StaticVar.COM_SMS_DELIVERY_SOS_TAR);
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
		if( getResultCode() == Activity.RESULT_OK || 
			strCase == StaticVar.COM_ALARM_REFRESH ||
			strCase == StaticVar.COM_ALARM_SOS_SET )
		{
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "receive sucess in " + strCase);
			logDialog.DialogUpdate(strAppendId);
		}
		else
		{
			
			if(StaticVar.DEBUG_ENABLE)
			{
				StaticVar.logPrint('D', "error in receive:" + strCase);
				StaticVar.logPrint('D', "result:" + Activity.RESULT_OK);
			}
		}
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
				//����ͷ--����sos�����gps�ظ���Ϣ
				if(mesContext.substring(0, 3).equals("W01"))
				{
					if(StaticVar.DEBUG_ENABLE)
						StaticVar.logPrint('D', "set sos ok case!");
					ReceiveMsgCaseSetSosOK(context, mesContext , SettingActivity.settingDialog );
				}
				else
				{
					//����ͷ��ƥ��--Ϊ�˵��Է��㣬���ڽ�Ҫɾ��
					if(StaticVar.DEBUG_ENABLE)
					{
						StaticVar.logPrint('D', "set sos error case!");
					
						Toast.makeText(context, "setting:unknown sms", Toast.LENGTH_SHORT).show();
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
	
	//����sos����ɹ�
	private void ReceiveMsgCaseSetSosOK(Context context, String msgContext, LogDialog logDialog)
	{
		
		if(msgContext.substring(8).equals(StaticVar.SMS_BODY_SET_SOS_OK))
		{
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "sos number set sucess!");
			
//			SettingActivity.alarmHandler.Stop();
			DialogRefresh(SettingActivity.settingDialog, R.string.DialogSosFeedBackGpsOK, StaticVar.SMS_BODY_SET_SOS_OK);
			SettingActivity.settingDialog.disable();
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




