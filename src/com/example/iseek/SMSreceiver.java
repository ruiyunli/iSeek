package com.example.iseek;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;


/* �Զ���̳���BroadcastReceiver��,����ϵͳ����㲥����Ϣ */
public class SMSreceiver extends BroadcastReceiver 
{ 
	/*������̬�ַ���,��ʹ��android.provider.Telephony.SMS_RECEIVED��ΪActionΪ���ŵ�����*/
	private static final String mACTION = "android.provider.Telephony.SMS_RECEIVED";
	private static final String tarPhoneNum = "13669271404";
	  
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
				  
				//ȡ�ô���ѶϢ��BODY 
				mesContext = new String(messages[0].getDisplayMessageBody());
				
				System.out.println("mesNumber:" + mesNumber);
				System.out.println("mesContext:" + mesContext);
			}    
   
			//Toast.makeText(context, sb.toString(), Toast.LENGTH_LONG).show(); 
		} 
	} 
	
	public boolean isValidGeo(String sb)
	{
		boolean isValid = false;
		
		
		
		return isValid;
	}
} 




