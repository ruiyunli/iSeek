package com.izzz.iseek.corr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.iseek.R;
import com.izzz.iseek.base.CorrManage;
import com.izzz.iseek.tools.PrefHolder;
import com.izzz.iseek.vars.StaticVar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class CorrListAdapter extends BaseAdapter {   
	
	private Context mContext = null;
	
    private LayoutInflater mInflater;    
    
    private List<Map<String, String>> mData;    
    
    public static Map<Integer, Boolean> isSelected; 
    
    private CorrPointManager corrPM = null;
    
    public CorrListAdapter(Context mContext) {    
    	
    	this.mContext = mContext;
    	
        mInflater = LayoutInflater.from(mContext);
        
        corrPM = new CorrPointManager(mContext, PrefHolder.prefs, PrefHolder.prefsEditor);
        
        InitData();    
    }    
    
    //初始化    
    private void InitData() {  
    	
    	int pointSize 		= corrPM.getSize();
    	int[] originData 	= new int[pointSize];
    	int[] targetData 		= new int[pointSize];
    	
    	originData 	= corrPM.getOriginData();
    	targetData 	= corrPM.getTargetData();
    	
    	mData=new ArrayList<Map<String, String>>();
    	isSelected = new HashMap<Integer, Boolean>(); 
    	
    	//第一项，目录
    	Map<String, String> map0 = new HashMap<String, String>();    
        map0.put("corr_origin", mContext.getResources().getString(R.string.CorrListOrigin));    
        map0.put("corr_target", mContext.getResources().getString(R.string.CorrListTarget));
        map0.put("corr_diff", mContext.getResources().getString(R.string.CorrListDiff)); 
        mData.add(map0);         
        isSelected.put(0, false); 
        
    	//第二项往后
		for(int i = 0 ; i<pointSize ; i++)
		{
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "origin-" + i + ":" + originData[i] +
										"  corr-" + i + ":" + targetData[i]);
			
			Map<String, String> map = new HashMap<String, String>();    
            map.put("corr_origin", Integer.toString(originData[i]));    
            map.put("corr_target", Integer.toString(targetData[i]));
            map.put("corr_diff", Integer.toString(Math.abs(originData[i]-targetData[i]))); 
            mData.add(map); 
            
            isSelected.put(i+1, false);  
		}            
    }    
    
    @Override    
    public int getCount() {    
        return mData.size();    
    }    
    
    @Override    
    public Object getItem(int position) {    
        return null;    
    }    
    
    @Override    
    public long getItemId(int position) {    
        return 0;    
    }    
    
    @Override    
    public View getView(int position, View convertView, ViewGroup parent) {    
        ViewHolder holder = null;    
        //convertView为null的时候初始化convertView。    
        if (convertView == null) {    
        	
        	System.out.println("convertView is null");
            holder = new ViewHolder();    
            convertView = mInflater.inflate(R.layout.listview_corr, null);    
            holder.corr_origin = (TextView) convertView.findViewById(R.id.corr_origin);    
            holder.corr_target = (TextView) convertView.findViewById(R.id.corr_target);
            holder.corr_diff = (TextView) convertView.findViewById(R.id.corr_diff);
            holder.corr_isSelect = (CheckBox) convertView.findViewById(R.id.corr_isSelect);    
            convertView.setTag(holder);    
        } else {         	
        	System.out.println("convertView is not null");
            holder = (ViewHolder) convertView.getTag();    
        }  
        
        if(position == 0)
        {
        	holder.corr_origin.setText(R.string.CorrListOrigin);    
	        holder.corr_target.setText(R.string.CorrListTarget);
	        holder.corr_diff.setText(R.string.CorrListDiff); 
	        holder.corr_isSelect.setChecked(isSelected.get(position));
	        
        }
        else
        {
	        holder.corr_origin.setText(mData.get(position).get("corr_origin").toString());    
	        holder.corr_target.setText(mData.get(position).get("corr_target").toString());
	        holder.corr_diff.setText(mData.get(position).get("corr_diff").toString()); 
	        holder.corr_isSelect.setChecked(isSelected.get(position));
        }
        return convertView;    
    }    
    
    public final class ViewHolder {    
    	public TextView corr_origin;
        public TextView corr_target;
        public TextView corr_diff;
        public CheckBox corr_isSelect;    
    } 
    
    public void remove(int position)
    {
    	corrPM.remove(position);
    	InitData(); 
    	notifyDataSetChanged();
    }
}    