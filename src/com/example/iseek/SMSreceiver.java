package com.example.iseek;

import com.baidu.mapapi.utils.CoordinateConver;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.view.inputmethod.CorrectionInfo;
import android.widget.Toast;


/* �Զ���̳���BroadcastReceiver��,����ϵͳ����㲥����Ϣ */
public class SMSreceiver extends BroadcastReceiver 
{ 
	
	@Override 
	public void onReceive(Context context, Intent intent) 
	{ 
		// TODO Auto-generated method stub 
		// �жϴ���Intent�Ƿ�Ϊ����
		
		System.out.println("OnReceive-Action:" + intent.getAction());
		if (intent.getAction().equals(StaticVar.SYSTEM_SMS_ACTION)) 
		{ 
			String mesNumber;
			String mesContext;
			
			//������Intent����������
			Bundle bundle = intent.getExtras(); 
			
			//�ж�Intent��������
			if (bundle != null) 
			{ 
				//���ڲ��ԣ�����ɾ��----�ɹ���~
//				MainActivity.logDialog.dismiss();
				
				System.out.println("bundle is not null!");
				
				//pdusΪ android�ڽ����Ų��� identifier
				//͸��bundle.get("")����һ������pdus�Ķ���
				Object[] myOBJpdus = (Object[]) bundle.get("pdus");
				
				//�������Ŷ���array,�������յ��Ķ��󳤶�������array�Ĵ�С
				SmsMessage[] messages = new SmsMessage[myOBJpdus.length];	          
	          	messages[0] = SmsMessage.createFromPdu ((byte[]) myOBJpdus[0]);             
				
				//��Ѷ�ߵĵ绰���� 
				mesNumber = new String(messages[0].getDisplayOriginatingAddress());  
				  
				//�˴���Ҫ��ȡ֮ǰ���õ�Ŀ���ֻ����룬Ȼ���ж�
				String targetPhone = StaticVar.prefs.getString(StaticVar.prefTargetPhoneKey,"unset");
				
				System.out.println("mesNumber:" + mesNumber + " targetPhone:" + targetPhone);
				
				//�е��ֻ���ȡ�������"+86"�������еĲ����У��ж��Ƿ��������������
				if(mesNumber.equals(targetPhone) || mesNumber.equals("+86" + targetPhone))
				{
					//��ȡ��������
					mesContext = new String(messages[0].getDisplayMessageBody());
					
					System.out.println("mesNumber:" + mesNumber);
					System.out.println("mesContext:" + mesContext);
					System.out.println(mesContext.substring(0, 7));
					
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
						System.out.println("OnReceive--Latitude:" + Latitude + " Longitude:" + Longitude);
						//����MainActivity�еľ�̬���������õ�ͼ
						if(isValidGeo(Longitude) && isValidGeo(Latitude))
						{
							//����Ҫ����ر�logDialog
							StaticVar.logDialog.dismiss();
							//WGS84����ת��Ϊ�ٶ�����
							GeoPoint tmpPoint = CoordinateConver.fromWgs84ToBaidu(
									new GeoPoint((int)(Double.parseDouble(Latitude)* 1E6),(int)(Double.parseDouble(Longitude)* 1E6)));
							//��ͼ����
							StaticVar.setNewPosition(tmpPoint.getLatitudeE6()/(1E6),tmpPoint.getLongitudeE6()/(1E6));
						}

					}
					else
					{
						//����ͷ��ƥ��--Ϊ�˵��Է��㣬���ڽ�Ҫɾ��
						Toast.makeText(context, "SMS-header Error", Toast.LENGTH_LONG).show();
					}
				}
				else
				{
					//�ֻ����벻ƥ��
					System.out.println("SMSreceiver:different targetPhone");					
					mesContext = new String(messages[0].getDisplayMessageBody());
					System.out.println("diff-mesNumber:" + mesNumber);
					System.out.println("diff-mesContext:" + mesContext);
					return ;
				}
			}
			else
			{
				//bundle��Ϊ��
				System.out.println("bundle is null");
			}   
		} 
		
		
		
		//��һ�δ���ܶ��ַ�������Ҫ�Լ�ȥ��string�и㶨	
		
		
		else if (intent.getAction().equals(StaticVar.COM_SMS_SEND))
		{
			if(getResultCode()== Activity.RESULT_OK)
			{

//			    Toast.makeText(context, "�ͳ��ɹ�!!" , Toast.LENGTH_SHORT).show();
			    StaticVar.logMessage = StaticVar.logMessage + "\n" + context.getResources().getText(R.string.DialogSendOK);
			    StaticVar.logDialog.setMessage(StaticVar.logMessage);			    
			}
			else
			{
//				Toast.makeText(context, "�ͳ�ʧ��!!" , Toast.LENGTH_SHORT).show();
				StaticVar.logMessage = StaticVar.logMessage + "\n" + context.getResources().getText(R.string.DialogSendOK);
			    StaticVar.logDialog.setMessage(StaticVar.logMessage);
			}
		}
		else if (intent.getAction().equals(StaticVar.COM_SMS_DELIVERY))
		{
			System.out.println("�յ����ͻ�ִ");
			if(getResultCode()== Activity.RESULT_OK)
			{
//			    Toast.makeText(context, "�Է��Ѿ��ɹ�����!!" , Toast.LENGTH_SHORT).show();
			    StaticVar.logMessage = StaticVar.logMessage + "\n" + context.getResources().getText(R.string.DialogDeliveryOK);
			    StaticVar.logDialog.setMessage(StaticVar.logMessage);
			}
			else
			{
//				Toast.makeText(context, "�Է�����ʧ��!!" , Toast.LENGTH_SHORT).show();
				StaticVar.logMessage = StaticVar.logMessage + "\n" + context.getResources().getText(R.string.DialogDeliveryNO);
			    StaticVar.logDialog.setMessage(StaticVar.logMessage);
			}
		}
	} 
	

	
	//���ھ�γ�ȵĺϷ��Լ��
	public boolean isValidGeo(String geoStr)
	{
		boolean isValid = false;
		
		double tmp;
		tmp = Double.parseDouble(geoStr);
		
		System.out.println("isValidGeo--str:" + geoStr + " tmp:" + tmp);
		
		if((tmp>0) && (tmp<140) )
			isValid = true;		
		return isValid;
	}
} 




