package com.izzz.iseek.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;

import com.example.iseek.R;
import com.izzz.iseek.base.BaseMapMain;

public class LogDialog {

	private ProgressDialog proLogDialog;
	private String proMessage;
	private Context mContext;	
	
	public LogDialog(Context context, int msgHeaderId, int dialogTitleId) {
		
		this.mContext = context;
		
		proMessage = (String) context.getResources().getString(msgHeaderId);
		proLogDialog = new ProgressDialog(context);		
		proLogDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		proLogDialog.setMessage(proMessage);
		proLogDialog.setTitle(context.getResources().getString(dialogTitleId));
		proLogDialog.setCancelable(false);
	}	
	
	/**
     * add a keylistener for progress dialog
     */
    public static OnKeyListener onKeyListener = new OnKeyListener() {
    	
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//	                dismissDialog();
            	BaseMapMain.baseProDialog.dismiss();
            }
            return false;
        }
		
    };
}
