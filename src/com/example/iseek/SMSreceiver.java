package com.example.iseek;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.widget.Toast;


/* �Զ���̳���BroadcastReceiver��,����ϵͳ����㲥����Ϣ */
public class SMSreceiver extends BroadcastReceiver 
{ 
	//������̬�ַ���,��ʹ��android.provider.Telephony.SMS_RECEIVED��ΪActionΪ���ŵ�����
	private static final String mACTION = "android.provider.Telephony.SMS_RECEIVED";
	//����sharedPreferences
	SharedPreferences prefSmsRecv = null;
	
	//�ط�����ͷ�ж��ַ���
//	private static final String GEO_GET_SUCCESS = "W00,051";//��ȡ��γ�ȳɹ�
//	private static final String GEO_SET_SUCCESS = "W01,003";//���óɹ�
//	private static final String GEO_SET_FAILRUE = "W02,003";//����ʧ��
	//�ж϶���ͷ����������FLAG
//	private static final int
	  
	@Override 
	public void onReceive(Context context, Intent intent) 
	{ 
		// TODO Auto-generated method stub 
		// �жϴ���Intent�Ƿ�Ϊ����
		if (intent.getAction().equals(mACTION)) 
		{ 
			//����һ�ַ��������ϱ���sb			
			String mesNumber;
			String mesContext;
			
			//������Intent����������
			Bundle bundle = intent.getExtras(); 
			
			//�ж�Intent��������
			if (bundle != null) 
			{ 
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
				prefSmsRecv = PreferenceManager.getDefaultSharedPreferences(context);
				String targetPhone = prefSmsRecv.getString("targetPhone","unset");
				
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
					if(mesContext.substring(0, 7).equals("W00,051"))
					{
						//������γ��
						//For example:
						//W00,051,34.234442N,108.913805E,1.574Km/h,13-03-21,16:04:43
						int indexTmp = mesContext.indexOf("N");
						String Latitude = mesContext.substring(8, 15);//ԭ����8-17
						//��N���濪ʼ��ȡ����Ϊ����
						String Longitude = mesContext.substring(indexTmp+2, indexTmp+10);//ԭ����19-29
						System.out.println("OnReceive--Latitude:" + Latitude + " Longitude:" + Longitude);
						//����MainActivity�еľ�̬���������õ�ͼ
						if(isValidGeo(Longitude) && isValidGeo(Latitude))
							MainActivity.setNewPosition(Double.parseDouble(Latitude),Double.parseDouble(Longitude));						

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
	} 
	
	/*
	private int ProcessSmsHeader(String str)
	{
		if(str.equals(GEO_GET_SUCCESS))
		return 0;
	}
	*/
	
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




