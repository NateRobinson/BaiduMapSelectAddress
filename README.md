# 模仿58到家百度地图地址选择效果 #

先上58到家的效果图：

![58到家的效果图](https://github.com/NateRobinson/BaiduMapSelectAddress/blob/master/imgs/58%E5%88%B0%E5%AE%B6%E6%95%88%E6%9E%9C.gif?raw=true)

刚好最近有个项目要实现此效果，于是我就去好好研究了一把百度地图API，不得不说，百度地图的类参考文档简直好啊，遇到思绪打不开的时候，看看他的类参考文档，瞬间思路大开[http://wiki.lbsyun.baidu.com/cms/androidsdk/doc/v3_6_1/](http://wiki.lbsyun.baidu.com/cms/androidsdk/doc/v3_6_1/ "http://wiki.lbsyun.baidu.com/cms/androidsdk/doc/v3_6_1/") 我这里用的3.6.1的，所以查看的对应版本的类参考。

下面再放上我这个demo实现的效果：

地图选择界面1:

![选择地图](https://github.com/NateRobinson/BaiduMapSelectAddress/blob/master/imgs/2.png?raw=true)

地图选择界面2：

![选择地图](https://github.com/NateRobinson/BaiduMapSelectAddress/blob/master/imgs/3.png?raw=true)

地图选择界面3：

![选择地图](https://github.com/NateRobinson/BaiduMapSelectAddress/blob/master/imgs/1.png?raw=true)


----------

## 几个关键点 ##

这里用到了几个关键的地方：

一：如何设置的marker一直在地图的中间,这里使用了BaiduMap的OnMapStatusChangeListener监听接口:

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

这个`arg0.target`就是当前map控件的中点位置的坐标，我们可以获取到，然后重新设置一下marker就可以了，不过在之前一定要记得` mBaiduMap.clear();`，不然会出现很多个marker。

二：如果通过中点的经纬度获取到周围的地点：使用到了GeoCoder类，这个类可以通过坐标反编译到地址，当然可以获取到这个地址周围的地址：

     public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
    if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
    Toast.makeText(LocationActivity.this, "抱歉，未能找到结果",
    Toast.LENGTH_LONG).show();
    return;
    }
    
	//这就是附近地址的列表
    List<PoiInfo> list = result.getPoiList();
    if (list != null && list.size() > 0) {
    nearAddresses.clear();
    nearAddresses.addAll(list);
    nearAddressAdapter.notifyDataSetChanged();
    }
    
    }


三：如何实现在输入框中一输入一个关键字，就出来一串地址列表：使用了PoiSearch类，这个类可以通过设置搜索监听事件，来搜索和输入关键字有关联的地址：

先给输入框加一个输入监听事件：

    search_et.addTextChangedListener(new TextWatcher() {
    
    @Override
    public void onTextChanged(CharSequence cs, int start, int before,
      int count) {
    if (cs == null || cs.length() <= 0) {
    search_ll.setVisibility(View.GONE);
    return;
    }
    
	//发起搜索请求
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

然后在搜索结果回调中处理结果并展示出来：

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

至此一个初步模仿58到家百度地图选择地址的效果就完成啦。通过此文希望和大家一起学习进步。