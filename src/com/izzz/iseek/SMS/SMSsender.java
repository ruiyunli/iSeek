package com.izzz.iseek.SMS;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;
import com.izzz.iseek.R;
import com.izzz.iseek.vars.PrefHolder;
import com.izzz.iseek.vars.StaticVar;

public class SMSsender {

	private Context mContext = null;
	
	public SMSsender(Context mContext) {
		super();
		this.mContext = mContext;
	}

	/**
	 * 
	 * @param destNumberId			Ŀ���ֻ����룬��Ϊnullʱ�����͵�TargetPhone
	 * @param mesContext			��������
	 * @param sentIntentStr			����intent
	 * @param deliveryIntentStr		���ջ�ִintent
	 * @return						���ͳɹ�����true�����򷵻�false
	 */
	public boolean SendMessage(String destNumber, String mesContext, String sentIntentStr , String deliveryIntentStr)
	{
		if(destNumber == null)
		{
			/*
			destNumber = PrefHolder.prefs.getString(PrefHolder.prefTargetPhoneKey, "unset");
			if(destNumber.equals("unset"))
			{
				Toast.makeText(mContext, R.string.ToastTargetSetEmpty,	Toast.LENGTH_LONG).show();
				return false;
			}
			*/
			destNumber = PrefHolder.getTargetPhone();
			if(destNumber == null)
			{
				Toast.makeText(mContext, R.string.ToastTargetSetEmpty,	Toast.LENGTH_LONG).show();
				return false;
			}
				
		}
		
		return SendMessage(mContext, destNumber, mesContext, sentIntentStr, deliveryIntentStr);
	}


	private boolean SendMessage(Context context, String destNumber, String mesContext, String sentIntentStr , String deliveryIntentStr)
	{	
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
	    	
		    smsManager.sendTextMessage(destNumber, null, strMessage, mPIsend, mPIdelivery);
		    
		    if(StaticVar.DEBUG_ENABLE)
		    {
		    	StaticVar.logPrint('D', "number:" + destNumber + " context:" + strMessage);
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
	
	/**
	 * �ж��Ƿ�Ϊ��Ч�ֻ�����
	 * 1��130-139
	 * 2��180,185-189
	 * 3��150-153��155-159
	 * 4��147
	 * 5��10086--������
	 * 6��10001--������
	 * 7��5555--������
	 * 8��6666--������
	 * */
	public static boolean isMobileNumber(String mobiles){
		
		boolean result = false;
		
		if("".equals(mobiles.trim()))
			return false;
		
		if("+86".equals(mobiles.substring(0, 3)))
		{
			mobiles = mobiles.substring(3);
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "remove:+86 mobiles:" + mobiles);
		}
		
		Pattern p=Pattern.compile(
				"^(((13[0-9])|18[0,5-9]|15[0-3,5-9]|147)\\d{8})|(10086)|(10001)|(5555)|(5556)$");
		Matcher m=p.matcher(mobiles);
		
		result = m.matches();
		
		if(StaticVar.DEBUG_ENABLE)
			StaticVar.logPrint('D', "mobiles:" + mobiles +" result:" + result);
		return result;
	}

}
