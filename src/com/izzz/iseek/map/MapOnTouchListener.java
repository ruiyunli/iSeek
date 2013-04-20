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
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		// 获得屏幕点击的位置
		
		
		if(BaseMapMain.correction.CORRECTION_ENABLE)
		{
			
            int x = (int) event.getX();
            int y = (int) event.getY();

            // 将像素坐标转为地址坐标
            
            Projection getProjection = BaseMapMain.correction.mMapView.getProjection();
            GeoPoint pt = getProjection.fromPixels(x, y);
            
            String longitude = Integer.toString(pt.getLongitudeE6());
            String latitude = Integer.toString(pt.getLatitudeE6());	           
            
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            	BaseMapMain.logText.setText("x: " + x + " y: " + y
                        + "\n" + " latitude: " + latitude
                        +" longitude: " + longitude);
            	
            	corrPoint = new GeoPoint(pt.getLatitudeE6(), pt.getLongitudeE6());
        		corrItem = new OverlayItem(corrPoint, "title", "snippet");
        		//corrItem.setMarker(getResources().getDrawable(R.drawable.icon_marka));
        		//初次调用，添加校准层
        		if(!BaseMapMain.correction.ADD_LAYER_FLAG)	
        		{
        			BaseMapMain.correction.AddOverlay();
        			BaseMapMain.correction.ADD_LAYER_FLAG = true;
        		}
        		BaseMapMain.correction.correctionOverlay.refreshItem(corrItem);
        		BaseMapMain.correction.mMapView.refresh();
        		
        		BaseMapMain.correction.corrPoint = corrPoint;
        		BaseMapMain.correction.corrItem = corrItem;
        		
                break;
            }
            
			
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "keyDown");
		
		}
		
		return false;
	}

}
