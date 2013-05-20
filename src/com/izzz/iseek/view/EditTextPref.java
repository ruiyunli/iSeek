package com.izzz.iseek.view;

import com.izzz.iseek.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/**
 * 
 * @author Administrator
 * 
 * 为EditTextPreference自定义的弹出dialog
 * 1、具备输入框
 * 2、具备联系人选择
 *
 */
public class EditTextPref {
	
	private Context mContext = null;
	
	private EditText dialogEditText = null;
	
	private ImageButton dialogButton = null;
	
	private AlertDialog.Builder setAlert = null;
	
	private int titleId;
	
	private View.OnClickListener contactListener = null;
	
	private DialogInterface.OnClickListener okListener = null;

	public EditTextPref(Context mContext) {
		super();
		this.mContext = mContext;
		
		setAlert = new AlertDialog.Builder(mContext);
	}
	
	/**
	 * 功能：更新界面UI
	 * 原因：子控件在setView函数调用时，必须每次重新初始化
	 */
	public void RefreshView()
	{
		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.dialog_contact_picker, null);
        dialogEditText = (EditText)layout.findViewById(R.id.dialogEditText);
        dialogButton= (ImageButton)layout.findViewById(R.id.dialogButton);
		
		setAlert.setView(layout);		
		
	}
	
	/**对话框的标题*/
	public void setTitle(int titleId)
	{
		setAlert.setTitle(titleId);
	}
	
	/**设置EditText中的文字内容*/
	public void setText(String str)
	{
		dialogEditText.setText(str);
	}
	
	/**获取EditText中的文字内容*/
	public String getText()
	{
		return dialogEditText.getText().toString().trim();
	}
	
	/**显示对话框*/
	public void show()
	{
		dialogButton.setOnClickListener(contactListener);
		setAlert.setPositiveButton(R.string.OK_CH, okListener);
		setAlert.setNegativeButton(R.string.CANCLE_CH, null);
		setAlert.create();
		setAlert.show();
	}
	
	/**设置联系人选择按钮响应*/
	public void setContactPickerListener(View.OnClickListener listener)
	{
		contactListener = listener;
	}
	
	/**设置确定按钮响应*/
	public void setPositiveButton(DialogInterface.OnClickListener listener)
	{
		okListener = listener;
	}
	
	


}
