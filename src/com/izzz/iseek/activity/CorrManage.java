package com.izzz.iseek.activity;

import java.text.DecimalFormat;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.izzz.iseek.R;
import com.izzz.iseek.correction.CorrCalcK;
import com.izzz.iseek.correction.CorrListAdapter;
import com.izzz.iseek.correction.CorrPointManager;
import com.izzz.iseek.correction.CorrListAdapter.ViewHolder;
import com.izzz.iseek.maplocate.GPSLocate;
import com.izzz.iseek.vars.StaticVar;

public class CorrManage extends Activity{

	private ListView CorrList 		= null;
	
	private Button btnCorrDelete 	= null;
	
	private Button btnCorrCalc 		= null;
	
	private CorrListAdapter adapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_corr);	
		
		btnCorrCalc 	= (Button)findViewById(R.id.btnCorrCalc);
		btnCorrDelete 	= (Button)findViewById(R.id.btnCorrDelete);
		
		btnCorrDelete.setOnClickListener(new btnDeleteOnClickListener());
		btnCorrCalc.setOnClickListener(new btnCalcOnClickListener());
		
		InitListView();	
	}
	
	private void InitListView()
	{
		CorrList=(ListView)findViewById(R.id.corrlist);    
        adapter=new CorrListAdapter(this);    
        CorrList.setAdapter(adapter);    
        CorrList.setItemsCanFocus(false);    
        CorrList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);    
        CorrList.setOnItemClickListener(new ListViewOnClickListener());
        
        setButtonUnable();
	}	
	
	/**按钮不可用*/
	public void setButtonUnable()
	{
		btnCorrCalc.setEnabled(false);
		btnCorrDelete.setEnabled(false);
	}
	
	/**按钮不可用*/
	public void setButtonEnable()
	{
		btnCorrCalc.setEnabled(true);
		btnCorrDelete.setEnabled(true);
	}
	
	@Override
	public void setContentView(int layoutResID) {
		// TODO Auto-generated method stub
		
		boolean isCustom = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); //声明使用自定义标题  
		super.setContentView(layoutResID);
		if(isCustom)
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);//自定义布局赋值   
		
		ImageButton btnTitleBar = (ImageButton)findViewById(R.id.btnTitleBar);
		TextView textTitleBar = (TextView)findViewById(R.id.textTitleBar);
		
		//此处修改一下，对应页面的文字标题
		textTitleBar.setText(R.string.TitleBarCorr);
		
		btnTitleBar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	
	public class btnDeleteOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			int[] arrDel = new int[CorrPointManager.MAX_POINT_SIZE];
			int k = 0;
			for(int i = adapter.getCount()-1; i>0; i--)
				if(CorrListAdapter.isSelected.get(i))
					{
						arrDel[k++] = i;
						if(StaticVar.DEBUG_ENABLE)
							StaticVar.logPrint('D', "delete:" + i);
					}
			
//			if(k == 0)
//			{
//				Toast.makeText(CorrManage.this, R.string.ToastCorrDeletedNull, Toast.LENGTH_LONG).show();
//			}
//			else
//			{
				adapter.remove(arrDel, k);
				Toast.makeText(CorrManage.this, R.string.ToastCorrDeletedOK, Toast.LENGTH_LONG).show();
//			}
//			CorrList.updateViewLayout(v, null);
		}		
	}
	
	public class btnCalcOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			double[] XX = new double[CorrPointManager.MAX_POINT_SIZE];
			double[] YY = new double[CorrPointManager.MAX_POINT_SIZE];
			
			int k=0;
			double[] AB = new double[2];
			
			for(int i=1; i<CorrListAdapter.isSelected.size(); i++)
			{
				if(CorrListAdapter.isSelected.get(i))
				{
					XX[k] 	= adapter.corrPM.getOriginData(i-1);
					YY[k] = adapter.corrPM.getTargetData(i-1);
					
					if(StaticVar.DEBUG_ENABLE)
					{
						DecimalFormat df6 = new DecimalFormat("0.000000");
						String str = "XX[" + k + "]:" + XX[k] + " YY[" + k + "]:" + YY[k];
						StaticVar.logPrint('D', str);
					}
					
					k++;
				}
			}
			
//			if(k == 0)
//			{
//				Toast.makeText(CorrManage.this, R.string.ToastCoefCalcNull, Toast.LENGTH_LONG).show();
//			}
//			else
//			{
				AB = CorrCalcK.from_xy_to_ab(XX, YY, k);	
				adapter.corrPM.saveCoef(AB[0], AB[1]);
				GPSLocate.InitCoef();
				Toast.makeText(CorrManage.this, R.string.ToastCoefCreated, Toast.LENGTH_LONG).show();
//			}
		}
	}
	
	public class ListViewOnClickListener implements OnItemClickListener{    
        @Override    
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {  
        	
        	//全选或全不选
        	if(position == 0)
            {
	            if(CorrListAdapter.isSelected.get(0)==true)
	            	setAllCheckBox(false);
	            else
	            	setAllCheckBox(true);	            
            }
        	//单个选择
        	else
        	{
	            ViewHolder vHollder = (ViewHolder) view.getTag();    
	            vHollder.corr_isSelect.toggle();    
	            CorrListAdapter.isSelected.put(position, vHollder.corr_isSelect.isChecked()); 
	            
	            //从全选到单个选
	            if((!CorrListAdapter.isSelected.get(position)) && (CorrListAdapter.isSelected.get(0)))
	            {
	            	CorrListAdapter.isSelected.put(0, false);
	        		adapter.getView(0, view, parent);
	            }
	            	
        	}
        	
        	//设置button的显示效果
        	if(isSelectedNull())
        		setButtonUnable();
        	else
        		setButtonEnable();
            
        }  
        
        //全选或全不选
        public void setAllCheckBox(boolean isSelected)
        {
        	for(int i = 0; i<adapter.getCount();i++)
        		CorrListAdapter.isSelected.put(i, isSelected);        	
        	CorrList.refreshDrawableState();        	
        }
        
        public boolean isSelectedNull()
        {
        	for(int i = 1; i<adapter.getCount();i++)
        		if(CorrListAdapter.isSelected.get(i))
        			return false;
        	return true;
        }
    }
	
	
	
}
