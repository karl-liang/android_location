package com.example.administrator.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private TextView postionView;

    private LocationManager locationManager;
    private String locationProvider;

    private static long lastUpdate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //获取显示地理位置信息的TextView
        postionView = (TextView) findViewById(R.id.positionView);
        //获取地理位置管理器
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);
        if(providers.contains(LocationManager.GPS_PROVIDER)){
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        }else if(providers.contains(LocationManager.NETWORK_PROVIDER)){
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        }else{
            Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
            return ;
        }
        try{
            //获取Location
            Location location = locationManager.getLastKnownLocation(locationProvider);
            if(location!=null){
                //不为空,显示地理位置经纬度
                showLocation(location);
            }
            //监视地理位置变化
            locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
        }catch(SecurityException e){

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(locationManager!=null){
            //移除监听器
            locationManager.removeUpdates(locationListener);
        }
    }
    /**
     * 显示地理位置经度和纬度信息
     * @param location
     */
    private void showLocation(final Location location){

        String locationStr = "维度：" + location.getLatitude() +"\n"
                + "经度：" + location.getLongitude();
        postionView.setText(locationStr);

        if(lastUpdate == 0 || (System.currentTimeMillis() - lastUpdate > 1*60*1000)){
            final String path = "http://120.78.82.133:8080/cnicg-code/v1/trail";
            new Thread() {//创建子线程进行网络访问的操作
                public void run() {
                    try {
                        postLocationToServer(location,path);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();

            lastUpdate = System.currentTimeMillis();
        }
     }
    private void postLocationToServer(Location location, String url) {
        Map<String, String> parameters = new HashMap<String,String>();

        parameters.put("point",location.getLongitude()+","+location.getLatitude());
        try{
            TelephonyManager tm = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
            String phoneNumber1 = tm.getLine1Number();
            if(phoneNumber1.length() == 0)
                phoneNumber1 = tm.getSimSerialNumber();
            parameters.put("person",phoneNumber1);
        }catch (SecurityException e){
            parameters.put("person","1382628");
        }



        Map<String,String> headers = new HashMap<String,String>();
        headers.put("Content-Type", "application/json; charset=utf-8");

        HttpRequestUtil.sendPostRequest(url,parameters,headers);
    }

    /**
     * LocationListern监听器
     * 参数：地理位置提供器、监听位置变化的时间间隔、位置变化的距离间隔、LocationListener监听器
     */

    LocationListener locationListener =  new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle arg2) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {
            //如果位置发生变化,重新显示
            showLocation(location);

        }
    };
}
