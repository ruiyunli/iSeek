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
 * ΪEditTextPreference�Զ���ĵ���dialog
 * 1���߱������
 * 2���߱���ϵ��ѡ��
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
	 * ���ܣ����½���UI
	 * ԭ���ӿؼ���setView��������ʱ������ÿ�����³�ʼ��
	 */
	public void RefreshView()
	{
		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.dialog_contact_picker, null);
        dialogEditText = (EditText)layout.findViewById(R.id.dialogEditText);
        dialogButton= (ImageButton)layout.findViewById(R.id.dialogButton);
		
		setAlert.setView(layout);		
		
	}
	
	/**�Ի���ı���*/
	public void setTitle(int titleId)
	{
		setAlert.setTitle(titleId);
	}
	
	/**����EditText�е���������*/
	public void setText(String str)
	{
		dialogEditText.setText(str);
	}
	
	/**��ȡEditText�е���������*/
	public String getText()
	{
		return dialogEditText.getText().toString().trim();
	}
	
	/**��ʾ�Ի���*/
	public void show()
	{
		dialogButton.setOnClickListener(contactListener);
		setAlert.setPositiveButton(R.string.OK_CH, okListener);
		setAlert.setNegativeButton(R.string.CANCLE_CH, null);
		setAlert.create();
		setAlert.show();
	}
	
	/**������ϵ��ѡ��ť��Ӧ*/
	public void setContactPickerListener(View.OnClickListener listener)
	{
		contactListener = listener;
	}
	
	/**����ȷ����ť��Ӧ*/
	public void setPositiveButton(DialogInterface.OnClickListener listener)
	{
		okListener = listener;
	}
	
	


}
