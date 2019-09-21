package com.mcz.gps_appproject.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.mcz.gps_appproject.R;
import com.mcz.gps_appproject.app.model.DataInfo;
import com.mcz.gps_appproject.app.utils.Config;
import com.mcz.gps_appproject.app.utils.DataManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Gpstrack_Activity extends AppCompatActivity  {

    private String deviceId="";
    private String gatewayId="";
    private String token="";
    SharedPreferences sp;
    private MapView mapView;
    private BaiduMap mBaiduMap;
    private int j;
    List<LatLng> points = new ArrayList<LatLng>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_gpstrack_);
        Log.i("=======","activity_gps_");
        mapView=(MapView)findViewById(R.id.bmapView) ;

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        Intent intent=getIntent();
        deviceId = intent.getStringExtra("deviceId");
        gatewayId = intent.getStringExtra("gatewayId");
        Log.i("ccccccccccccccccccccc","deviceId            "+deviceId);
        Log.i("ccccccccccccccccccccc","gatewayId            "+gatewayId);
        token=sp.getString("token","");
        Log.i("ccccccccccccccccccccc","token            "+token);



    }
    private void add_data() throws Exception {
        String login_appid = sp.getString("appId","");
        Log.i("ccccccccccccccccccccc","login_appid            "+login_appid);
        String add_url = Config.all_url + "/iocm/app/data/v1.1.0/deviceDataHistory?deviceId="+deviceId+"&gatewayId="+gatewayId;
        String json = DataManager.Txt_REQUSET(Gpstrack_Activity.this, add_url, login_appid, token);

        /////////////////////////解析
        JSONObject jo = new JSONObject(json);
        JSONArray jsonArray = jo.getJSONArray("deviceDataHistoryDTOs");

        for (int i = 0; i < jsonArray.length(); i++) {
            DataInfo dataInfo = new DataInfo();
            String timestamp=jsonArray.getJSONObject(i).getString("timestamp");
            String ser_data = jsonArray.getJSONObject(i).getString("data");
            dataInfo.setDevicetimestamp(timestamp);
            //  Log.i("ccccccccccccccccccccc","data     "+ser_data);
            JSONObject jsonObject = new JSONObject(ser_data);
            dataInfo.setDeviceLatitude(jsonObject.optString("Latitude"));
            dataInfo.setDeviceLongitude(jsonObject.optString("Longitude"));


            dataInfo.setDevicehumidity(jsonObject.optString("Humidity"));
            dataInfo.setDevicetemperature(jsonObject.optString("Temperature"));




            //  Log.i("ccccccccccccccccccccc","Latitude     "+dataInfo.getDeviceLatitude());
            //  Log.i("ccccccccccccccccccccc","Longitude     "+dataInfo.getDeviceLongitude());
            Double Latitude;
            Double Longitude;


            boolean ss=  pandun(dataInfo.getDeviceLatitude(),dataInfo.getDeviceLongitude());
            if (ss==true)
            {

                Latitude  = Double.parseDouble(dataInfo.getDeviceLatitude());
                Longitude  = Double.parseDouble(dataInfo.getDeviceLongitude());
                if(Latitude>10&&Longitude>100)
                {
                    j++;
                    LatLng p1 = new LatLng(Latitude, Longitude);
                    CoordinateConverter coordinateConverter =new CoordinateConverter();
                    coordinateConverter.coord(p1);
                    coordinateConverter.from(CoordinateConverter.CoordType.GPS);
                    p1=coordinateConverter.convert();
                    points.add(p1);
                }

            }
            else
            {

            }
            if(j==200)
            {
                j=0;
                return;
            }

        }
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
        new LoadDataAsyncTask3().execute();//查询所有
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    private void addline(List<LatLng> points)
    {

        OverlayOptions ooPolyline = new PolylineOptions().width(6)
                .color(0xAAFF0000).points(points);
        Polyline mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);

        MapStatusUpdate mapStatusUpdate= MapStatusUpdateFactory.newLatLng(points.get(2));
        mBaiduMap.animateMapStatus(mapStatusUpdate);

        mapStatusUpdate=MapStatusUpdateFactory.zoomTo(28f);
        mBaiduMap.animateMapStatus(mapStatusUpdate);
    }
    private class LoadDataAsyncTask3 extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mBaiduMap = mapView.getMap();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (Gpstrack_Activity.this.isFinishing()) {
                return null;
            }

            try {
                add_data();
                addline(points);
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

            if (Gpstrack_Activity.this.isFinishing()) {
                return;
            }
            points.clear();
            new  Thread  (new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(3000);
                    new LoadDataAsyncTask3().execute();//查询所有

                }
            }) .start();
        }


    }
}
