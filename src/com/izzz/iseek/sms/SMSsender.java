package com.izzz.iseek.sms;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.izzz.iseek.map.IseekApplication;
import com.izzz.iseek.vars.StaticVar;

public class SMSsender {

	//���ŷ��ͺ���
	//��destNumberΪnullʱ����ʾ���͵�targetPhone
	public static boolean SendMessage(Context context, String destNumber, String mesContext, String sentIntentStr , String deliveryIntentStr)
	{	
		//��ȡ�ֻ�����	
		String strDestAddress = null;
		
		if(destNumber != null)
		{
			strDestAddress = destNumber;
		}
		else
		{
			strDestAddress = IseekApplication.getInstance().prefs.getString(IseekApplication.getInstance().prefTargetPhoneKey, "unset");
	
			if(strDestAddress.equals("unset"))
			{				
				Toast.makeText(context, "Please Set Phone Number" , Toast.LENGTH_SHORT).show();				
				return false;
			}
		}
		
		//Ĭ��ָ�gps�ش����Ⱥ�γ��
	    String strMessage = mesContext; 		  
	    SmsManager smsManager = SmsManager.getDefault();		     
	    try 
	    { 
	    	//���Ͷ���
	    	PendingIntent mPIsend = null;
	    	PendingIntent mPIdelivery = null;
	    	if(sentIntentStr != null)
		        mPIsend = PendingIntent.getBroadcast(context, 0, new Intent(sentIntentStr), 0);
	    	if(deliveryIntentStr != null)
	    		mPIdelivery = PendingIntent.getBroadcast(context, 0, new Intent(deliveryIntentStr), 0);
	    	
		    smsManager.sendTextMessage(strDestAddress, null, strMessage, mPIsend, mPIdelivery);
		    
		    if(StaticVar.DEBUG_ENABLE)
		    {
		    	StaticVar.logPrint('D', "number:" + strDestAddress + " context:" + strMessage);
		    	StaticVar.logPrint('D', "send message success");
		    }
		    return true;
	    } 
	    catch(Exception e) 
	    {
	    	e.printStackTrace();
	    }
	    return false;
	    //Toast.makeText(context, "�ͳ��ɹ�!!" , Toast.LENGTH_SHORT).show();
	}

}
