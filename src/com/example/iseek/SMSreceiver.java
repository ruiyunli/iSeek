package com.example.iseek;

import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;


/* 自定义继承自BroadcastReceiver类,监听系统服务广播的信息 */
public class SMSreceiver extends BroadcastReceiver 
{ 
	/*声明静态字符串,并使用android.provider.Telephony.SMS_RECEIVED作为Action为短信的依据*/
	private static final String mACTION = "android.provider.Telephony.SMS_RECEIVED";
	private static final String tarPhoneNum = "13669271404";	
	SharedPreferences prefSmsRecv = null;
	  
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
				  
				//此处需要获取之前设置的目的手机号码，然后判断
				prefSmsRecv = PreferenceManager.getDefaultSharedPreferences(context);
				String targetPhone = prefSmsRecv.getString("targetPhone","unset");
				if(targetPhone.equals(mesNumber))
				{
					//取得传来讯息的BODY 
					mesContext = new String(messages[0].getDisplayMessageBody());
					
					System.out.println("mesNumber:" + mesNumber);
					System.out.println("mesContext:" + mesContext);
					System.out.println(mesContext.substring(0, 7));
					
					if(mesContext.substring(0, 7).equals("W00,051"))
					{
						String Latitude = mesContext.substring(8, 15);//原来是8-17
						String Longitude = mesContext.substring(19, 27);//原来是19-29
						System.out.println("Latitude:" + Latitude + "Longitude:" + Longitude);
						
						MainActivity.setNewPosition(Double.parseDouble(Latitude),Double.parseDouble(Longitude));
//						GeoPoint point =new GeoPoint((int)(34.234* 1E6),(int)(108.913* 1E6));		
//						mMapController.setCenter(point);//设置地图中心点
//						mMapController.setZoom(16);//设置地图zoom级别

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
	
	//用于手机号码输入的合法性检测，正则表达式
	public boolean isValidGeo(String sb)
	{
		boolean isValid = false;
		
		
		
		return isValid;
	}
} 




