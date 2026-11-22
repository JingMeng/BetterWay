package com.android.betterway.autoscheduleactivity.view;


import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.android.betterway.R;
import com.android.betterway.data.LocationBean;
import com.android.betterway.other.MapMarker;
import com.android.betterway.other.MarkerControl;
import com.android.betterway.utils.JsonUtil;
import com.android.betterway.utils.LogUtil;
import com.android.betterway.utils.ToastUtil;
import com.bigkoo.pickerview.OptionsPickerView;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class AutoScheduleAssistFragment extends Fragment implements AMap.OnMyLocationChangeListener,
        GeocodeSearch.OnGeocodeSearchListener {
    private static final String TAG = "AutoScheduleAssistFragment";
    private AMap aMap;
    private LatLng mLatLng;
    private List<Marker> mMarkerList = new ArrayList<Marker>();
    @BindView(R.id.location_text)
    TextView mLocationText;
    @BindColor(R.color.accent)
    int accent;
    @BindColor(R.color.primary)
    int primary;
    Unbinder unbinder;
    @BindView(R.id.mapview)
    MapView mMapview;
    private ArrayList<LocationBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initJson();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 鍒濆鍖栧湴鍥?
     */
    private void initMap() {
        if (aMap == null) {
            aMap = mMapview.getMap();
        }
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.setOnMyLocationChangeListener(this);
        acquirePower();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_auto_schedule_assist, container, false);
        unbinder = ButterKnife.bind(this, view);
        mMapview.onCreate(savedInstanceState);
        initMap();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMapview.onDestroy();
        unbinder.unbind();
    }

    /**
     * 鎸夐挳鐐瑰嚮鏃堕棿
     */
    @OnClick(R.id.location_text)
    public void onViewClicked() {
        showPickerView();
    }

    /**
     * 瑙ｆ瀽Json
     */
    private void initJson() {
        Observable.create(new ObservableOnSubscribe<ArrayList<LocationBean>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ArrayList<LocationBean>> e) throws Exception {
                ArrayList<LocationBean> locationBeanArrayList = JsonUtil.getJsonData(getContext(), "province.json");
                LogUtil.d(TAG, "initJson subser");
                e.onNext(locationBeanArrayList);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Consumer<ArrayList<LocationBean>>() {
                    @Override
                    public void accept(ArrayList<LocationBean> locationBeen) throws Exception {
                        options1Items = locationBeen;
                        for (int i = 0; i < locationBeen.size(); i++) { //閬嶅巻鐪佷唤
                            ArrayList<String> mCityList = new ArrayList<>(); //璇ョ渷鐨勫煄甯傚垪琛紙绗簩绾э級
                            for (int c = 0; c < locationBeen.get(i).getCity().size(); c++) { //閬嶅巻璇ョ渷浠界殑鎵€鏈夊煄甯?
                                String mCityName = locationBeen.get(i).getCity().get(c).getName();
                                mCityList.add(mCityName); //娣诲姞鍩庡競
                            }
                            options2Items.add(mCityList);
                        }
                        LogUtil.d(TAG, "initJson finish");
                    }
                });
    }

    /**
     * 寮瑰嚭鍦扮偣閫夋嫨鍣?
     */
    private void showPickerView() { // 寮瑰嚭閫夋嫨鍣?
        OptionsPickerView pvOptions = new OptionsPickerView.Builder(
                getContext(), new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //杩斿洖鐨勫垎鍒槸涓や釜绾у埆鐨勯€変腑浣嶇疆
                String city = options2Items.get(options1).get(options2);
                String tx = options1Items.get(options1).getPickerViewText()
                        + options2Items.get(options1).get(options2);
                mLocationText.setText(tx);
                sendSearchLocation(city);
                doSearchGeo(tx, city);
            }
        })
                .setSubmitText("纭畾")//纭畾鎸夐挳鏂囧瓧
                .setCancelText("鍙栨秷")//鍙栨秷鎸夐挳鏂囧瓧
                .setTitleText("璇烽€夋嫨鍦扮偣")//鏍囬
                .setTitleColor(primary)//鏍囬鏂囧瓧棰滆壊
                .setSubmitColor(accent)//纭畾鎸夐挳鏂囧瓧棰滆壊
                .setCancelColor(accent)//鍙栨秷鎸夐挳鏂囧瓧棰滆壊
                .isCenterLabel(true) //鏄惁鍙樉绀轰腑闂撮€変腑椤圭殑label鏂囧瓧锛宖alse鍒欐瘡椤筰tem鍏ㄩ儴閮藉甫鏈塴abel銆?
                .setCyclic(false, false, false)//寰幆涓庡惁
                .setSelectOptions(0, 1)  //璁剧疆榛樿閫変腑椤?
                .setOutSideCancelable(false)//鐐瑰嚮澶栭儴dismiss default true
                .isDialog(false)//鏄惁鏄剧ず涓哄璇濇鏍峰紡
                .build();
        pvOptions.setPicker(options1Items, options2Items); //浜岀骇閫夋嫨鍣?
        pvOptions.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapview.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapview.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapview.onSaveInstanceState(outState);
    }
    /**
     * 璇锋眰瀹氫綅鏉冮檺
     */
    private void acquirePower() {
        RxPermissions rxPermissions = new RxPermissions(getActivity());
        rxPermissions.requestEach(Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // 鐢ㄦ埛宸茬粡鍚屾剰璇ユ潈闄?
                            MyLocationStyle myLocationStyle;
                            myLocationStyle = new MyLocationStyle();
                            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
                            myLocationStyle.showMyLocation(false);
                            aMap.setMyLocationStyle(myLocationStyle); //璁剧疆瀹氫綅钃濈偣鐨凷tyle
                            aMap.setMyLocationEnabled(true);
                            aMap.moveCamera(CameraUpdateFactory.zoomTo(13));
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 鐢ㄦ埛鎷掔粷浜嗚鏉冮檺锛岄偅涔堜笅娆″啀娆″惎鍔ㄦ椂锛岃繕浼氭彁绀鸿姹傛潈闄愮殑瀵硅瘽妗?
                            ToastUtil.show(getContext(), "鎮ㄥ凡鎷掔粷瀹氫綅鐢宠锛屾棤娉曞畾浣嶅埌鎮ㄧ殑浣嶇疆");
                        } else {
                            // 鐢ㄦ埛鎷掔粷浜嗚鏉冮檺锛屽苟涓旈€変腑銆庝笉鍐嶈闂€忥紝鎻愰啋鐢ㄦ埛鎵嬪姩鎵撳紑鏉冮檺
                            ToastUtil.show(getContext(), "鏉冮檺琚嫆缁濓紝璇峰湪璁剧疆閲岄潰寮€鍚浉搴旀潈闄愶紝鍚﹀垯灏嗘棤娉曞畾浣嶅埌鎮ㄧ殑浣嶇疆");
                        }
                    }
                });
    }

    @Override
    public void onMyLocationChange(Location location) {
        LogUtil.e(TAG, "onMyLocationChange");
        GeocodeSearch geocoderSearch = new GeocodeSearch(getContext());
        geocoderSearch.setOnGeocodeSearchListener(this);
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
        LatLonPoint latLonPoint = new LatLonPoint(location.getLatitude(), location.getLongitude());
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 10000, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        LogUtil.e(TAG, "onRegeocodeSearched");
        String getLocation = regeocodeResult.getRegeocodeAddress().getProvince()
                    + regeocodeResult.getRegeocodeAddress().getCity();
        sendSearchLocation(regeocodeResult.getRegeocodeAddress().getCity());
        mLocationText.setText(getLocation);
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        LogUtil.e(TAG, "onGeocodeSearched");
        GeocodeAddress geocodeAddress = geocodeResult.getGeocodeAddressList().get(0);
        LatLng latLng = new LatLng(geocodeAddress.getLatLonPoint().getLatitude(),
                geocodeAddress.getLatLonPoint().getLongitude());
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
    }

    /**
     * 鐢卞煄甯傚悕鏌ヨ鍦扮悊浣嶇疆
     * @param address 鍦板潃
     * @param name 鍩庡競鍚嶅瓧
     */
    private void doSearchGeo(String address, String name) {
        GeocodeSearch geocoderSearch = new GeocodeSearch(getContext());
        geocoderSearch.setOnGeocodeSearchListener(this);
        GeocodeQuery query = new GeocodeQuery(address, name);
        geocoderSearch.getFromLocationNameAsyn(query);
    }

    /**
     * 鍙栧緱鑾峰緱鐨勫潗鏍囧苟绉诲姩鍦板浘鍒拌鍧愭爣
     * @param latLonPoint 鑾峰緱鐨勫潗鏍?
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onLatLngPointEvent(LatLonPoint latLonPoint) {
        LogUtil.e(TAG, "moveToLatLngPoint()");
        mLatLng = new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(mLatLng));
    }

    /**
     * 璁板綍鑾峰緱鐐圭殑鍧愭爣
     * @param mapMarker 璁板綍鐨勪俊鍙?
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onIntEvent(MapMarker mapMarker) {
        LogUtil.e(TAG, "addToMap()");
        if (mapMarker == MapMarker.ADD) {
            Marker marker = aMap.addMarker(new MarkerOptions());
            marker.setPosition(mLatLng);
            mMarkerList.add(marker);
            LogUtil.d(TAG, "ListLeng =" + mMarkerList.size());
        }
    }

    /**
     * 鍒犻櫎鍦板浘涓婄殑鏍囪
     * @param markerControl 浼犻€掓秷鎭殑瀵硅薄
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void deleteMarker(MarkerControl markerControl) {
        LogUtil.d(TAG, "code = " + markerControl.getControl());
        int code = markerControl.getControl();
        if (code == -100) {
            for (int i = 0; i <= mMarkerList.size() - 1; i++) {
                mMarkerList.get(i).destroy();
            }
            mMarkerList.clear();
        } else {
            Marker marker = mMarkerList.get(code);
            mMarkerList.remove(code);
            marker.destroy();
        }
    }
    /**
     * 鍙戦€侀€夋嫨鐨勫煄甯?
     * @param city 閫夋嫨鐨勫煄甯?
     */
    private void sendSearchLocation(String city) {
        EventBus.getDefault().post(city);
    }
}

