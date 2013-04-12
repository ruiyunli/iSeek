package com.izzz.iseek.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;

public class LogDialog {

	public ProgressDialog proLogDialog;
	public String proMessage;
	public Context mContext;
	public boolean SHOW_FLAG = false;
	
	/**
	 * 
	 * @param context
	 * @param msgHeaderId		Log窗口消息头
	 * @param dialogTitleId		Log窗口标题栏
	 */
	public LogDialog(Context context, int msgHeaderId, int dialogTitleId) {
		
		this.mContext = context;
		
		proMessage = (String) context.getResources().getString(msgHeaderId);
		proLogDialog = new ProgressDialog(context);		
		proLogDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		proLogDialog.setMessage(proMessage);
		proLogDialog.setTitle(context.getResources().getString(dialogTitleId));
		proLogDialog.setCancelable(false);
		proLogDialog.setOnKeyListener(new LogDialogOnKeyListner());
	}	
	
	public void enable()
	{
		SHOW_FLAG = true;
	}
	
	public void disable()
	{
		SHOW_FLAG = false;
	}
	
	public void showLog()
	{
		if((!proLogDialog.isShowing()) && (SHOW_FLAG))
			proLogDialog.show();
	}
	
	public void dismissLog()
	{
		if(proLogDialog.isShowing())
			proLogDialog.dismiss();
	}
	
	/**
	 * 在log输出窗口中添加输出内容
	 * @param strAppendId
	 */
	public void DialogUpdate(int strAppendId )
	{			
		proMessage = proMessage + "\n" + mContext.getResources().getString(strAppendId);		
	    proLogDialog.setMessage(proMessage);
	    showLog();	
	}
	
	/**
     * 解决高版本中dialog外焦点触发退出界面问题
     */
    class LogDialogOnKeyListner implements OnKeyListener {
    	
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
	                dismissLog();
            }
            return false;
        }
		
    };
}
