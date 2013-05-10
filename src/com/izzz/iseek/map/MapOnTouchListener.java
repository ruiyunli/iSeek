package com.izzz.iseek.map;

import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.baidu.platform.comapi.map.Projection;
import com.izzz.iseek.base.BaseMapMain;
import com.izzz.iseek.vars.StaticVar;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class MapOnTouchListener implements OnTouchListener{

	private GeoPoint corrPoint;
	private OverlayItem corrItem;
	
//	private long timeBreak = 0;
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		// �����Ļ�����λ��
		
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
			if(BaseMapMain.corrView.CORRECTION_START)
			{				
	            int x = (int) event.getX();
	            int y = (int) event.getY();
	
	            // ����������תΪ��ַ����
	            
	            Projection getProjection = BaseMapMain.corrView.mMapView.getProjection();
	            GeoPoint pt = getProjection.fromPixels(x, y);
	            
	            String longitude = Integer.toString(pt.getLongitudeE6());
	            String latitude = Integer.toString(pt.getLatitudeE6());	           
	            
	            if(StaticVar.DEBUG_ENABLE)
	            {
	            	StaticVar.logPrint('D', "x: " + x + " y: " + y
	                        + "\n" + " latitude: " + latitude
	                        +" longitude: " + longitude);
	            	BaseMapMain.logText.setText("x: " + x + " y: " + y
	                        + "\n" + " latitude: " + latitude
	                        +" longitude: " + longitude);
	            }
            	corrPoint = new GeoPoint(pt.getLatitudeE6(), pt.getLongitudeE6());
        		corrItem = new OverlayItem(corrPoint, "title", "snippet");
        		//corrItem.setMarker(getResources().getDrawable(R.drawable.icon_marka));
        		//���ε��ã����У׼��
        		if(!BaseMapMain.corrView.ADD_LAYER_FLAG)	
        		{
        			BaseMapMain.corrView.AddOverlay();
        			BaseMapMain.corrView.ADD_LAYER_FLAG = true;
        		}
        		BaseMapMain.corrView.correctionOverlay.refreshItem(corrItem);
        		BaseMapMain.corrView.mMapView.refresh();
        		
        		BaseMapMain.corrView.corrPoint = corrPoint;
        		BaseMapMain.corrView.corrItem = corrItem;
				
				if(StaticVar.DEBUG_ENABLE)
					StaticVar.logPrint('D', "keyDown");
			
			}
			else
			{
//				timeBreak = System.currentTimeMillis();
			}
			break;
		 
//        case MotionEvent.ACTION_UP:
//        	long timeNow = System.currentTimeMillis();
//        	
//        	if(StaticVar.DEBUG_ENABLE)
//        		StaticVar.logPrint('D', "time slide:" + (timeNow-timeBreak));
//        	break;
        }
		
		return false;
	}
	
	

}
