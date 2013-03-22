package com.example.iseek;

import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;


/* �Զ���̳���BroadcastReceiver��,����ϵͳ����㲥����Ϣ */
public class SMSreceiver extends BroadcastReceiver 
{ 
	/*������̬�ַ���,��ʹ��android.provider.Telephony.SMS_RECEIVED��ΪActionΪ���ŵ�����*/
	private static final String mACTION = "android.provider.Telephony.SMS_RECEIVED";
	private static final String tarPhoneNum = "13669271404";	
	SharedPreferences prefSmsRecv = null;
	  
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
				if(targetPhone.equals(mesNumber))
				{
					//ȡ�ô���ѶϢ��BODY 
					mesContext = new String(messages[0].getDisplayMessageBody());
					
					System.out.println("mesNumber:" + mesNumber);
					System.out.println("mesContext:" + mesContext);
					System.out.println(mesContext.substring(0, 7));
					
					if(mesContext.substring(0, 7).equals("W00,051"))
					{
						String Latitude = mesContext.substring(8, 15);//ԭ����8-17
						String Longitude = mesContext.substring(19, 27);//ԭ����19-29
						System.out.println("Latitude:" + Latitude + "Longitude:" + Longitude);
						
						MainActivity.setNewPosition(Double.parseDouble(Latitude),Double.parseDouble(Longitude));
//						GeoPoint point =new GeoPoint((int)(34.234* 1E6),(int)(108.913* 1E6));		
//						mMapController.setCenter(point);//���õ�ͼ���ĵ�
//						mMapController.setZoom(16);//���õ�ͼzoom����

					}
				}
				else
				{
					System.out.println("SMSreceiver:unset targetPhone");
					
					mesContext = new String(messages[0].getDisplayMessageBody());
					System.out.println("unset-mesNumber:" + mesNumber);
					System.out.println("unset-mesContext:" + mesContext);
					return ;
				}
			}    
   
			//Toast.makeText(context, sb.toString(), Toast.LENGTH_LONG).show(); 
		} 
	} 
	
	//�����ֻ���������ĺϷ��Լ�⣬������ʽ
	public boolean isValidGeo(String sb)
	{
		boolean isValid = false;
		
		
		
		return isValid;
	}
} 




