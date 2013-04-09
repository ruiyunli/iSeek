package com.izzz.iseek.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapPoi;
import com.izzz.iseek.vars.StaticVar;

public class BaseMKMapViewListener implements MKMapViewListener{

		Context mContext;
			
		public BaseMKMapViewListener(Context mContext) {
			super();
			this.mContext = mContext;
		}

		@Override
		public void onClickMapPoi(MapPoi mapPoiInfo) {
			// TODO Auto-generated method stub
			String title = "";
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "onClickMapPoi()");
			if (mapPoiInfo != null){
				title = mapPoiInfo.strText + "--Lat:" + mapPoiInfo.geoPt.getLatitudeE6() + " Lon:" +mapPoiInfo.geoPt.getLongitudeE6();
				Toast.makeText(mContext,title,Toast.LENGTH_LONG).show();
//				mMapController.animateTo(mapPoiInfo.geoPt);
			}
		}

		@Override
		public void onGetCurrentMap(Bitmap arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMapAnimationFinish() {
			// TODO Auto-generated method stub
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "onMapAnimationFinish()");
		}

		@Override
		public void onMapMoveFinish() {
			// TODO Auto-generated method stub
			// 在此处理地图移动完成消息回调
			if(StaticVar.DEBUG_ENABLE)
				StaticVar.logPrint('D', "onMapMoveFinish()");
		}
		
	}