package com.example.iseek.sms;

import com.baidu.mapapi.utils.CoordinateConver;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.iseek.MainActivity;
import com.example.iseek.R;
import com.example.iseek.setting.SettingActivity;
import com.example.iseek.vars.StaticVar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;



/* �Զ���̳���BroadcastReceiver��,����ϵͳ����㲥����Ϣ */
public class SMSreceiver extends BroadcastReceiver 
{ 
	
	@Override 
	public void onReceive(Context context, Intent intent) 
	{ 
		// TODO Auto-generated method stub 	
		
		//ϵͳ���յ����ţ�����
		if (intent.getAction().equals(StaticVar.SYSTEM_SMS_ACTION)) 
		{ 
			ReceiveMessageCase(context, intent);
		}		
		//���յ�refresh����״̬�㲥
		else if (intent.getAction().equals(StaticVar.COM_SMS_SEND_REFRESH))
		{
			MainActivity.mainLogMessage = ReceiveStateCase(MainActivity.mainProDialog,MainActivity.mainLogMessage, 
					(String)context.getResources().getText(R.string.DialogSendOK), StaticVar.COM_SMS_SEND_REFRESH);
		}
		//���յ�refresh���ͻ�ִ�㲥
		else if (intent.getAction().equals(StaticVar.COM_SMS_DELIVERY_REFRESH))
		{
			MainActivity.mainLogMessage = ReceiveStateCase(MainActivity.mainProDialog, MainActivity.mainLogMessage, 
					(String)context.getResources().getText(R.string.DialogDeliveryOK), StaticVar.COM_SMS_DELIVERY_REFRESH);
		}
		//���յ�sos���÷���״̬�㲥
		else if(intent.getAction().equals(StaticVar.COM_SMS_SEND_SOS))
		{
			SettingActivity.setLogMessage = ReceiveStateCase(SettingActivity.setProDialog, SettingActivity.setLogMessage, 
					(String)context.getResources().getText(R.string.DialogSosSendOK), StaticVar.COM_SMS_SEND_SOS);
		}
		//���յ�sos���÷��ͻ�ִ�㲥
		else if(intent.getAction().equals(StaticVar.COM_SMS_DELIVERY_SOS))
		{
			SettingActivity.setLogMessage = ReceiveStateCase(SettingActivity.setProDialog, SettingActivity.setLogMessage,
					(String)context.getResources().getText(R.string.DialogSosDeliveryOK),StaticVar.COM_SMS_DELIVERY_SOS);
		}
	}
	
	//��ϵͳ���ն��Ž��й��˺ͽ���
	private void ReceiveMessageCase(Context context, Intent intent)
	{
		String mesNumber;
		String mesContext;
		
		//������Intent����������
		Bundle bundle = intent.getExtras(); 
		
		//�ж�Intent��������
		if (bundle != null) 
		{ 
		
			StaticVar.logPrint('D', "bundle is not null!");
			
			//pdusΪ android�ڽ����Ų��� identifier
			//͸��bundle.get("")����һ������pdus�Ķ���
			Object[] myOBJpdus = (Object[]) bundle.get("pdus");
			
			//�������Ŷ���array,�������յ��Ķ��󳤶�������array�Ĵ�С
			SmsMessage[] messages = new SmsMessage[myOBJpdus.length];	          
          	messages[0] = SmsMessage.createFromPdu ((byte[]) myOBJpdus[0]); 
			
			mesNumber = new String(messages[0].getDisplayOriginatingAddress()); 			  

			String targetPhone = StaticVar.prefs.getString(
					StaticVar.prefTargetPhoneKey,"unset");			
			StaticVar.logPrint('D', "mesNumber:" + mesNumber + " targetPhone:" + targetPhone);
			
			//�е��ֻ���ȡ�������"+86"�������еĲ����У��ж��Ƿ��������������
			if(mesNumber.equals(targetPhone) || mesNumber.equals("+86" + targetPhone))
			{
			
				mesContext = new String(messages[0].getDisplayMessageBody());				
				StaticVar.logPrint('D', "mesNumber:" + mesNumber);
				StaticVar.logPrint('D', "mesContext:" + mesContext);
				StaticVar.logPrint('D', "SMS header:" + mesContext.substring(0, 7));
				
				//����ͷ�жϣ��Ƿ�ƥ��
				if(mesContext.substring(0, 7).equals(StaticVar.SMS_Header_LOC_SUCCESS))
				{
					//������γ��
					//For example:
					//W00,051,34.234442N,108.913805E,1.574Km/h,13-03-21,16:04:43
					int indexTmp = mesContext.indexOf("N");
					String Latitude = mesContext.substring(8, 16);//ԭ����8-17
					
					//��N���濪ʼ��ȡ����Ϊ����
					String Longitude = mesContext.substring(indexTmp+2, indexTmp+10);//ԭ����19-29
					StaticVar.logPrint('D', "OnReceive--Latitude:" + Latitude + " Longitude:" + Longitude);
					
					//����MainActivity�еľ�̬���������õ�ͼ
					if(isValidGeo(Longitude) && isValidGeo(Latitude))
					{
						//����Ҫ����ر�logDialog
						MainActivity.mainProDialog.dismiss();
						
						//WGS84����ת��Ϊ�ٶ�����
//						CoordinateConver.fromGcjToBaidu   --  GCJ-20(���Ĺȸ��ͼ)���ٶ�����ϵ 
//						CoordinateConver.fromWgs84ToBaidu --  WGS81���ٶ�����ϵת��
						GeoPoint tmpPoint = CoordinateConver.fromGcjToBaidu(new GeoPoint((int)(Double.parseDouble(Latitude)* 1E6),
								(int)(Double.parseDouble(Longitude)* 1E6)));
						
						StaticVar.setNewPosition(tmpPoint.getLatitudeE6()/(1E6),tmpPoint.getLongitudeE6()/(1E6));
					}
				}
				else
				{
					//����ͷ��ƥ��--Ϊ�˵��Է��㣬���ڽ�Ҫɾ��
					Toast.makeText(context, "SMS-header Error", Toast.LENGTH_LONG).show();
				}
				abortBroadcast();
			}
			else
			{
				//�ֻ����벻ƥ��
				StaticVar.logPrint('D', "SMSreceiver:different targetPhone");					
				mesContext = new String(messages[0].getDisplayMessageBody());
				StaticVar.logPrint('D', "diff-mesNumber:" + mesNumber);
				StaticVar.logPrint('D', "diff-mesContext:" + mesContext);
				return ;
			}
		}
		else
		{
			//bundle��Ϊ��
			StaticVar.logPrint('D', "bundle is null");
		}   
	}
	
	//ʵ�ֶԶ��ŷ���״̬�Լ����Ż�ִ�Ĵ���
	private String ReceiveStateCase(ProgressDialog progressDialog, String logMessage, 
			String strLogAppend, String strCase)
	{
		
		if(getResultCode()== Activity.RESULT_OK)
		{
			StaticVar.logPrint('D', "receive success flag for " + strCase);
			logMessage = logMessage + "\n" + strLogAppend;
		    progressDialog.setMessage(logMessage);
		    progressDialog.show();
		}
		else
		{
			StaticVar.logPrint('E', "Error in case " + strCase);
		}
		return logMessage;
	}

	
	//���ھ�γ�ȵĺϷ��Լ��
	public boolean isValidGeo(String geoStr)
	{
		boolean isValid = false;
		
		double tmp;
		tmp = Double.parseDouble(geoStr);
		
		StaticVar.logPrint('D', "isValidGeo--str:" + geoStr + " tmp:" + tmp);
		
		if((tmp>0) && (tmp<140) )
			isValid = true;		
		return isValid;
	}
} 




