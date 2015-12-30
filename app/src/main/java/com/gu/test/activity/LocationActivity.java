package com.gu.test.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.gu.test.R;
import com.gu.test.adapter.NearAddressAdapter;
import com.gu.test.adapter.SearchAddressAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @author Nate
 * @desc 服务地点选择界面
 * @date 2015-12-20
 */
public class LocationActivity extends AppCompatActivity
        implements OnGetGeoCoderResultListener, OnGetPoiSearchResultListener {

    @InjectView(R.id.mapView)
    MapView mMapView;
    @InjectView(R.id.near_list_empty_ll)
    LinearLayout near_list_empty_ll;
    @InjectView(R.id.near_address_list)
    ListView near_address_list;
    @InjectView(R.id.search_address_list_view)
    ListView search_address_list_view;
    @InjectView(R.id.search_et)
    EditText search_et;
    @InjectView(R.id.search_empty_tv)
    TextView search_empty_tv;
    @InjectView(R.id.search_ll)
    LinearLayout search_ll;
    private GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    private BaiduMap mBaiduMap = null;
    private MyLocationListenner myListener = new MyLocationListenner();
    private LocationMode mCurrentMode;
    private boolean isFirstLoc = true;// 是否首次定位
    private BitmapDescriptor mCurrentMarker;
    private LocationClient mLocClient;// 定位相关
    private PoiSearch mPoiSearch = null;
    private String cityName = "";

    private NearAddressAdapter nearAddressAdapter = null;
    private SearchAddressAdapter searchAddressAdapter = null;
    private List<PoiInfo> nearAddresses = new ArrayList<PoiInfo>();
    private List<PoiInfo> searchAddresses = new ArrayList<PoiInfo>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_location);
        ButterKnife.inject(this);
        initViewsAndEvents();
    }

    protected void initViewsAndEvents() {
        cityName = "南京";//这个可以通过定位获取
        // 隐藏比例尺和缩放图标
        mMapView.showScaleControl(false);
        mMapView.showZoomControls(false);
        mBaiduMap = mMapView.getMap();
        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        // 初始化搜索模块，注册搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        mSearch.setOnGetGeoCodeResultListener(this);
        mBaiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {

            @Override
            public void onMapStatusChangeStart(MapStatus arg0) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus arg0) {
                search_ll.setVisibility(View.GONE);
                mBaiduMap.clear();
                mBaiduMap.addOverlay(new MarkerOptions().position(arg0.target)
                        .icon(mCurrentMarker));
                // 反Geo搜索
                mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                        .location(arg0.target));
            }

            @Override
            public void onMapStatusChange(MapStatus arg0) {

            }
        });

        search_et.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int start, int before,
                                      int count) {
                if (cs == null || cs.length() <= 0) {
                    search_ll.setVisibility(View.GONE);
                    return;
                }
                /**
                 * 使用建议搜索服务获取建议列表
                 */
                mPoiSearch.searchInCity((new PoiCitySearchOption())
                        .city(cityName).keyword(cs.toString()).pageNum(0)
                        .pageCapacity(20));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.mipmap.marker_icon);
        mCurrentMode = LocationMode.NORMAL;
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                mCurrentMode, true, mCurrentMarker));
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(false);
        // 定位初始化
        mLocClient = new LocationClient(this);
        // 设置地图缩放级别为15
        mBaiduMap.setMapStatus(MapStatusUpdateFactory
                .newMapStatus(new MapStatus.Builder().zoom(15).build()));
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(false);// 打开gpss
        option.setCoorType("bd09ll"); // 设置坐标类型 取值有3个： 返回国测局经纬度坐标系：gcj02
        // 返回百度墨卡托坐标系 ：bd09 返回百度经纬度坐标系 ：bd09ll
        option.setScanSpan(1000);// 扫描间隔 单位毫秒
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        mLocClient.setLocOption(option);
        mLocClient.start();

        nearAddressAdapter = new NearAddressAdapter(this,
                R.layout.item_near_address, nearAddresses);
        near_address_list.setAdapter(nearAddressAdapter);
        near_address_list.setEmptyView(near_list_empty_ll);

        near_address_list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                PoiInfo poiInfo = nearAddresses.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("Ing", poiInfo.location.longitude + "");
                bundle.putString("Iat", poiInfo.location.latitude + "");
                bundle.putString("Address", poiInfo.name);
                bundle.putString("DetailedAddress", poiInfo.address);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        searchAddressAdapter = new SearchAddressAdapter(this,
                R.layout.item_search_address, searchAddresses);
        search_address_list_view.setAdapter(searchAddressAdapter);
        search_address_list_view.setEmptyView(search_empty_tv);

        search_address_list_view
                .setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        PoiInfo poiInfo = searchAddresses.get(position);
                        Bundle bundle = new Bundle();
                        bundle.putString("Ing", poiInfo.location.longitude + "");
                        bundle.putString("Iat", poiInfo.location.latitude + "");
                        bundle.putString("Address", poiInfo.name);
                        bundle.putString("DetailedAddress", poiInfo.address);
                        Intent intent = new Intent();
                        intent.putExtras(bundle);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        mSearch.destroy();
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView = null;
        super.onDestroy();
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult arg0) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(LocationActivity.this, "抱歉，未能找到结果",
                    Toast.LENGTH_LONG).show();
            return;
        }

        cityName = result.getAddressDetail().city;

        List<PoiInfo> list = result.getPoiList();
        if (list != null && list.size() > 0) {
            nearAddresses.clear();
            nearAddresses.addAll(list);
            nearAddressAdapter.notifyDataSetChanged();
        }

    }

    // 定位图标点击，重新设置为初次定位
    @OnClick(value = R.id.location_iv)
    public void reLocation(View view) {
        Toast.makeText(LocationActivity.this, "正在定位中...", Toast.LENGTH_SHORT).show();
        isFirstLoc = true;
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory
                        .newLatLng(ll);
                mBaiduMap.animateMapStatus(mapStatusUpdate);
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult arg0) {

    }

    @Override
    public void onGetPoiResult(PoiResult result) {
        if (result == null
                || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            List<PoiInfo> list = result.getAllPoi();
            search_ll.setVisibility(View.VISIBLE);
            if (list != null && list.size() > 0) {
                searchAddresses.clear();
                searchAddresses.addAll(list);
                searchAddressAdapter.notifyDataSetChanged();
            }
        }
    }

}
