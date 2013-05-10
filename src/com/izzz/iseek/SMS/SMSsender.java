package com.izzz.iseek.SMS;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;
import com.izzz.iseek.R;
import com.izzz.iseek.tools.PrefHolder;
import com.izzz.iseek.vars.StaticVar;

public class SMSsender {

	private Context mContext = null;
	
	public SMSsender(Context mContext) {
		super();
		this.mContext = mContext;
	}

	/**
	 * 
	 * @param destNumberId			目标手机号码，设为null时，发送到TargetPhone
	 * @param mesContext			短信内容
	 * @param sentIntentStr			发送intent
	 * @param deliveryIntentStr		接收回执intent
	 * @return						发送成功返回true，否则返回false
	 */
	public boolean SendMessage(String destNumber, String mesContext, String sentIntentStr , String deliveryIntentStr)
	{
		if(destNumber == null)
		{
			destNumber = PrefHolder.prefs.getString(PrefHolder.prefTargetPhoneKey, "unset");
			if(destNumber.equals("unset"))
			{
				Toast.makeText(mContext, R.string.ToastTargetSetEmpty,	Toast.LENGTH_LONG).show();
				return false;
			}
		}
		
		return SendMessage(mContext, destNumber, mesContext, sentIntentStr, deliveryIntentStr);
	}


	private boolean SendMessage(Context context, String destNumber, String mesContext, String sentIntentStr , String deliveryIntentStr)
	{	
		//默认指令，gps回传经度和纬度
	    String strMessage = mesContext; 		  
	    SmsManager smsManager = SmsManager.getDefault();		     
	    try 
	    { 
	    	//发送短信
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
	    //Toast.makeText(context, "送出成功!!" , Toast.LENGTH_SHORT).show();
	}

}
