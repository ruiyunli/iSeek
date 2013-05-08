package com.izzz.iseek.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.iseek.R;
import com.izzz.iseek.corr.CorrListAdapter;
import com.izzz.iseek.corr.CorrListAdapter.ViewHolder;

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
	}	
	
	
	
	@Override
	public void setContentView(int layoutResID) {
		// TODO Auto-generated method stub
		
		boolean isCustom = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); //����ʹ���Զ������  
		super.setContentView(layoutResID);
		if(isCustom)
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);//�Զ��岼�ָ�ֵ   
		
		ImageButton btnTitleBar = (ImageButton)findViewById(R.id.btnTitleBar);
		TextView textTitleBar = (TextView)findViewById(R.id.textTitleBar);
		
		//�˴��޸�һ�£���Ӧҳ������ֱ���
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
			for(int i = 0; i<adapter.getCount(); i++)
				if(CorrListAdapter.isSelected.get(i))
					adapter.remove(i);
//			CorrList.updateViewLayout(v, null);
		}		
	}
	
	public class ListViewOnClickListener implements OnItemClickListener{    
        @Override    
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {  
        	
        	//ȫѡ��ȫ��ѡ
        	if(position == 0)
            {
	            if(CorrListAdapter.isSelected.get(0)==true)
	            	setAllCheckBox(false);
	            else
	            	setAllCheckBox(true);
	            return;
            }
        	//����ѡ��
        	else
        	{
	            ViewHolder vHollder = (ViewHolder) view.getTag();    
	            vHollder.corr_isSelect.toggle();    
	            CorrListAdapter.isSelected.put(position, vHollder.corr_isSelect.isChecked()); 
	            
	            //��ȫѡ������ѡ
	            if((!CorrListAdapter.isSelected.get(position)) && (CorrListAdapter.isSelected.get(0)))
	            {
	            	CorrListAdapter.isSelected.put(0, false);
	        		adapter.getView(0, view, parent);
	            }
	            	
        	}
            
        }  
        
        //ȫѡ��ȫ��ѡ
        public void setAllCheckBox(boolean isSelected)
        {
        	for(int i = 0; i<adapter.getCount();i++)
        		CorrListAdapter.isSelected.put(i, isSelected);        	
        	CorrList.refreshDrawableState();        	
        }
    }
	
	
	
}
