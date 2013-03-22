package com.example.iseek;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;


/* 自定义继承自BroadcastReceiver类,监听系统服务广播的信息 */
public class SMSreceiver extends BroadcastReceiver 
{ 
	/*声明静态字符串,并使用android.provider.Telephony.SMS_RECEIVED作为Action为短信的依据*/
	private static final String mACTION = "android.provider.Telephony.SMS_RECEIVED";
	private static final String tarPhoneNum = "13669271404";
	  
	@Override 
	public void onReceive(Context context, Intent intent) 
	{ 
		// TODO Auto-generated method stub 
		// 判断传来Intent是否为短信
		if (intent.getAction().equals(mACTION)) 
		{ 
			//建构一字符串集集合变量sb			
			String mesNumber;
			String mesContext;
			
			//接收由Intent传来的数据
			Bundle bundle = intent.getExtras(); 
			
			//判断Intent是有资料
			if (bundle != null) 
			{ 
				System.out.println("bundle is not null!");
				
				//pdus为 android内建短信参数 identifier
				//透过bundle.get("")并传一个包含pdus的对象
				Object[] myOBJpdus = (Object[]) bundle.get("pdus");
				
				//建构短信对象array,并依据收到的对象长度来建立array的大小
				SmsMessage[] messages = new SmsMessage[myOBJpdus.length];	          
	          	messages[0] = SmsMessage.createFromPdu ((byte[]) myOBJpdus[0]);          
	            
				
				//来讯者的电话号码 
				mesNumber = new String(messages[0].getDisplayOriginatingAddress());  
				  
				//取得传来讯息的BODY 
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




