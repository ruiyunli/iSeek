package com.izzz.iseek.view;

import com.izzz.iseek.R;
import com.izzz.iseek.activity.BaseMapMain;
import com.izzz.iseek.maplistener.MapOnLongClickListener;
import com.izzz.iseek.vars.StaticVar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class WarnDialog {
	private Context mContext = null;

	public AlertDialog.Builder alertDialog = null;

	private TextView dialogMsg = null;

	private CheckBox dialogCheckBox = null;
	
	private int type = 0;
	
	public static boolean THEME_HAS_CHECKBOX = true;
	public static boolean THEME_NO_CHECKBOX = false;

//	public static int TYPE_NORMAL 	= 10;
//	public static int TYPE_NO_LOC 	= 20;
//	public static int TYPE_USE_CORR = 30;

	public WarnDialog(Context mContext, boolean hasCheckBox) {
		super();

		this.mContext = mContext;
		this.type = type;
		
		alertDialog = new AlertDialog.Builder(mContext);
		
		//�������ݲ���
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_context, null);
		alertDialog.setView(view);

		dialogMsg = (TextView) view.findViewById(R.id.dialogMSg);
		dialogCheckBox = (CheckBox) view.findViewById(R.id.dialogCheckBox);
		
		if(hasCheckBox)
			dialogCheckBox.setOnCheckedChangeListener(new checkBoxOnChangeListener());
		
		if(!hasCheckBox)
			dialogCheckBox.setVisibility(view.GONE);
		
	}

	 /**���ñ���*/
	 public void setTitle(int titleId)
	 {
	 alertDialog.setTitle(titleId);
	 }
	 /**������Ϣ����*/
	 public void setMessage(int msgId)
	 {
	 dialogMsg.setText(msgId);
	 }
	 /**��ʾ*/
	 public void show()
	 {
	 alertDialog.show();
	 }
	 /**����ok��Ӧ*/
	 public void setPositiveButton(OnClickListener listener)
	 {
		alertDialog.setPositiveButton(R.string.OK_CH, listener);
	 }
	 /**����cancle��Ӧ*/
	 public void setNegativeButton()
	 {
		alertDialog.setNegativeButton(R.string.CANCLE_CH, null);
	 }
	 
	 
	 class checkBoxOnChangeListener implements OnCheckedChangeListener{

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "onclick:type-"+type);
			
			if(isChecked)
			{
				//δ��λģʽ�¹رվ���
				
//				MapOnLongClickListener.FLAG_NOLOC_SHOW_ENABLE = false;
				if(StaticVar.DEBUG_ENABLE)	StaticVar.logPrint('D', "FLAG_NOLOC_SHOW_ENABLE:false");					
				
			}
		}
		 
	 }	
	

		
	

	
}
