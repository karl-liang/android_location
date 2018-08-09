package com.lh.android.location.activities;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.example.administrator.myapplication.R;
import com.lh.android.location.Http.HttpRequestUtil;
import com.lh.android.location.fragment.MyFragmentPagerAdapter;
import com.lh.android.location.fragment.OneFragment;
import com.lh.android.location.fragment.TrailFragment;
import com.lh.android.location.fragment.TreeFragment;
import com.lh.android.location.fragment.TwoFragment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements OneFragment.OnFragmentInteractionListener, TwoFragment.OnFragmentInteractionListener,TreeFragment.OnFragmentInteractionListener,TrailFragment.OnFragmentInteractionListener{

    TabLayout tab_layout;// 标题栏
    ViewPager view_pager;// 可滑动页面
    private ArrayList<Fragment> fragmentList;
    private ArrayList<String> titleList;
    private MyFragmentPagerAdapter mMyFragmentPagerAdapter;
    private LocationManager locationManager;
    private String locationProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tab_layout = this.findViewById(R.id.tab_layout);
        view_pager = this.findViewById(R.id.view_pager);

        titleList = new ArrayList<>();
        titleList.add("TODO");
        titleList.add("TODO");
        titleList.add("TODO");
        titleList.add("轨迹");
        
        // 初始化fragment集合
        fragmentList = new ArrayList<>();
        fragmentList.add(new OneFragment());
        fragmentList.add(new TwoFragment());
        fragmentList.add(new TreeFragment());
        fragmentList.add(new TrailFragment());
        // 添加标题
        tab_layout.addTab(tab_layout.newTab().setText(titleList.get(0)));
        tab_layout.addTab(tab_layout.newTab().setText(titleList.get(1)));
        tab_layout.addTab(tab_layout.newTab().setText(titleList.get(2)));
        tab_layout.addTab(tab_layout.newTab().setText(titleList.get(3)));

        // 设置viewPager适配器
        mMyFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), titleList, fragmentList);
        view_pager.setAdapter(mMyFragmentPagerAdapter);

        // 绑定viewPager与其联动
        tab_layout.setupWithViewPager(view_pager);

        // 设置打开应用时当前viewPager是第一个
        view_pager.setCurrentItem(0);

        // tabLayout添加标题选择事件
        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()){
                    case 0:
                        fragmentList.get(0);
                        break;
                    case 1:
                        fragmentList.get(1);
                        break;
                    case 2:
                        fragmentList.get(2);
                        break;
                    case 3:
                        fragmentList.get(3);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // 未选中tab
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // 再次选中tab
            }
        });
        startPostLocaitionThread();
        //获取地理位置管理器
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);
        if(providers.contains(LocationManager.NETWORK_PROVIDER)){
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        }else  if(providers.contains(LocationManager.GPS_PROVIDER)){
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        }else {
            Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
            return ;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private boolean isLocationEquals(Location lLocation, Location nLocation){
        double lLongitude = new BigDecimal(lLocation.getLongitude()).setScale(5).doubleValue();
        double lLatitude = new BigDecimal(lLocation.getLatitude()).setScale(5).doubleValue();

        double nLongitude = new BigDecimal(nLocation.getLongitude()).setScale(5).doubleValue();
        double nLatitude = new BigDecimal(nLocation.getLatitude()).setScale(5).doubleValue();

        if(lLongitude == nLongitude && lLatitude == nLatitude){
            return true;
        }else{
            return false;
        }
    }

    private static Location lastLocation = null;
    public void startPostLocaitionThread(){
        final String path = "http://120.78.82.133:8080/cnicg-code/v1/trail";
        new Thread() {//创建子线程进行网络访问的操作
            public void run() {
                try {
                    try{

                        //获取Location
                        Location location = locationManager.getLastKnownLocation(locationProvider);
                        if(location!=null && (lastLocation ==null || !isLocationEquals(location,lastLocation))){
                            //不为空,显示地理位置经纬度
                            postLocationToServer(location,path);
                            lastLocation = location;
                        }
                        Thread.sleep(5*60*1000);
                        //监视地理位置变化
                        //            locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
                    }catch(SecurityException e){
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startPostLocaitionThread();
            }
        }.start();
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
            parameters.put("person","11111111");
        }
        Map<String,String> headers = new HashMap<String,String>();
        headers.put("Content-Type", "application/json; charset=utf-8");

        HttpRequestUtil.sendPostRequest(url,parameters,headers);
    }
}
