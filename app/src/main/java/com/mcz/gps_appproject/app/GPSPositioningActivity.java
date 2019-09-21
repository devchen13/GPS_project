package com.mcz.gps_appproject.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.mcz.gps_appproject.R;

public class GPSPositioningActivity extends AppCompatActivity implements OnGetGeoCoderResultListener {

    private String Latitude="";
    private String Longitude="";
    private String token="";
    SharedPreferences sp;
    private MapView mapView;
    private BaiduMap mBaiduMap;
    private LatLng p1;
    private TextView positioninformation;
    GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_gpspositioning);
        mapView=(MapView)findViewById(R.id.GPS_PmapView) ;
        mBaiduMap = mapView.getMap();
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        Intent intent=getIntent();
        Latitude = intent.getStringExtra("Latitude");
        Longitude = intent.getStringExtra("Longitude");
        positioninformation = (TextView) findViewById(R.id.positioninformation);
        token=sp.getString("token","");
        //mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(p1));
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

    }

    //判断能否转换为Double
    private boolean pandun(String str,String str2){
        boolean ret = true;
        try{
            double d = Double.parseDouble(str);
            double d2 = Double.parseDouble(str2);
            ret = true;
        }catch(Exception ex){
            ret = false;
        }
        return ret;
    }
    @Override
    protected void onPause() {
        super.onPause();

        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadDataAsyncTask3 loadDataAsyncTask3=new LoadDataAsyncTask3(this);
        loadDataAsyncTask3.execute();//查询所有
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        mSearch.destroy();
    }
    private void setMarker(LatLng point) {
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_gcoding);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
//        //在地图上添加Marker，并显示
        Marker mMarker = (Marker) mBaiduMap.addOverlay(option);

        //显示到我的位置
        MapStatusUpdate mapStatusUpdate= MapStatusUpdateFactory.newLatLng(point);
        mBaiduMap.animateMapStatus(mapStatusUpdate);

        //设置缩放比例
        mapStatusUpdate=MapStatusUpdateFactory.zoomTo(16f);
        mBaiduMap.animateMapStatus(mapStatusUpdate);
        /////////////////////////////////////////////////

        //定义Maker坐标点
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {

        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(GPSPositioningActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        mBaiduMap.clear();
        mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.arrow_down)));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
                .getLocation()));
        String strInfo = String.format("纬度：%f 经度：%f",
                result.getLocation().latitude, result.getLocation().longitude);
        Toast.makeText(GPSPositioningActivity.this, strInfo, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(GPSPositioningActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();

            return;
        }
        mBaiduMap.clear();
        mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_gcoding)));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
                .getLocation()));
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("\n");
        stringBuilder.append("country:     ").append("\r");
        stringBuilder.append(result.getAddressDetail().countryName).append("\n");
        stringBuilder.append("province:        ").append("\r");
        stringBuilder.append(result.getAddressDetail().province).append("\n");
        stringBuilder.append("city:                  ").append("\r");
        stringBuilder.append(result.getAddressDetail().city).append("\n");
        stringBuilder.append("district:         ").append("\r");
        stringBuilder.append(result.getAddressDetail().district).append("\n");
        stringBuilder.append("street:          ").append("\r");
        stringBuilder.append(result.getAddressDetail().street).append("\n");
        stringBuilder.append("streetNumber:    ").append("\r");
        stringBuilder.append(result.getAddressDetail().streetNumber).append("\n");
        stringBuilder.append("adcode:         ").append("\r");
        stringBuilder.append(result.getAddressDetail().adcode).append("\n");
        stringBuilder.append("countryCode:     ").append("\r");
        stringBuilder.append(result.getAddressDetail().countryCode).append("\n");
        stringBuilder.append("Latitude:         ");
        stringBuilder.append(p1.latitude).append("\n");
        stringBuilder.append("Longitude:       ");
        stringBuilder.append(p1.longitude).append("\n");
        positioninformation.setText(stringBuilder);
//        Toast.makeText(GPSPositioningActivity.this, result.getAddress()+" adcode: "+result.getAdcode(),
//                Toast.LENGTH_LONG).show();
    }

    private class LoadDataAsyncTask3 extends AsyncTask<Void, Void, Boolean> {
        private Context context;

        LoadDataAsyncTask3(Context context) {
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Thread.sleep(300);//耗时操作，如网络请求
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (GPSPositioningActivity.this.isFinishing()) {
                return null;
            }
            if (Longitude==null||Latitude==null) {
                Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show();
                return null;
            }

            try {
//                add_data();
                if(pandun(Latitude, Longitude))
                {
                    double La = Double.parseDouble(Latitude);
                    double Lo = Double.parseDouble(Longitude);
                    if(La>0&&Lo>0)
                    {
                        p1 = new LatLng(La, Lo);

                        CoordinateConverter coordinateConverter =new CoordinateConverter();
                        coordinateConverter.coord(p1);
                        coordinateConverter.from(CoordinateConverter.CoordType.GPS);
                        p1=coordinateConverter.convert();
                        mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                                .location(p1).newVersion(1));


                    }
                    else
                    {

                        Toast.makeText(context, "定位无效", Toast.LENGTH_SHORT).show();
                        return null;
                    }

                }
                else
                {
                    Toast.makeText(context, "定位无效", Toast.LENGTH_SHORT).show();
                    return null;
                }
                setMarker(p1);
                return true;
            } catch ( Exception e ) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * 完成时的方法
         */
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (GPSPositioningActivity.this.isFinishing()) {
                return;
            }

            MapStatusUpdate  mapStatusUpdate=MapStatusUpdateFactory.zoomTo(20f);
            mBaiduMap.animateMapStatus(mapStatusUpdate);
//            new  Thread  (new Runnable() {
//                @Override
//                public void run() {
//                    SystemClock.sleep(1000);
//                    new GPSPositioningActivity.LoadDataAsyncTask3().execute();//查询所有
//
//                }
//            }) .start();
        }


    }
}
