package com.eland.android.eoas.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.eland.android.eoas.Activity.MainActivity;
import com.eland.android.eoas.Model.ApproveListInfo;
import com.eland.android.eoas.Model.Constant;
import com.eland.android.eoas.Model.WetherInfo;
import com.eland.android.eoas.R;
import com.eland.android.eoas.Receiver.DeskCountChangeReceiver;
import com.eland.android.eoas.Service.ApproveListService;
import com.eland.android.eoas.Service.WetherService;
import com.eland.android.eoas.Util.ConsoleUtil;
import com.eland.android.eoas.Util.ImageLoaderOption;
import com.eland.android.eoas.Util.SharedReferenceHelper;
import com.eland.android.eoas.Util.ToastUtil;
import com.eland.android.eoas.Views.ScrollTextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liu.wenbin on 15/11/27.
 */
public class MainFragment extends Fragment implements ApproveListService.IOnApproveListListener, AMapLocationListener, WetherService.IOnGetWetherListener {

    @Bind(R.id.img_registSchedule)
    LinearLayout imgRegistSchedule;
    @Bind(R.id.txt_main_scroll)
    ScrollTextView txtMainScroll;
    @Bind((R.id.img_wether_icon))
    ImageView imgWetherIcon;
    @Bind(R.id.temperature)
    TextView temperature;
    @Bind(R.id.weather)
    TextView weather;
    @Bind(R.id.hight_low)
    TextView hightLow;
    @Bind(R.id.windAndSD)
    TextView windAndSD;
    private Context context;
    private View rootView;
    private FragmentManager fragmentManager;
    private RegistScheduleFragment registScheduleFragment;
    private ContactFragment contactFragment;
    private static final int SCHEDULE_REGISTER = 10;    //打卡
    private String TAG = "EOAS";
    private String mUserId;
    private ApproveListService approveListService;
    private WetherService wetherService;

    private DisplayImageOptions options;
    private ImageLoader imageLoader;

    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;

    private double latitude;
    private double longitude;

    private int count = 0;


    public MainFragment() {
        super();
    }

    public static MainFragment newInstance(Bundle args) {
        MainFragment f = new MainFragment();
        f.setArguments(args);
        return f;
    }

    @SuppressLint("ValidFragment")
    public MainFragment(Context context) {
//        this.context = context;
//        this.fragmentManager = getFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.main_fragment, null);

        ButterKnife.bind(this, rootView);

        this.context = getContext();
        this.fragmentManager = getFragmentManager();
        approveListService = new ApproveListService(context);
        wetherService = new WetherService(context);

        options =  ImageLoaderOption.getOptionsById(R.mipmap.default_wether);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
        mUserId = SharedReferenceHelper.getInstance(context).getValue(Constant.LOGINID);
        setApproveCount();
        setTitler();
        setWehter();

        return rootView;
    }

    private void setApproveCount() {
        approveListService.searchApproveList(mUserId, this);
    }

    private void setTitler() {
        txtMainScroll.setTextContent("用户ID: " + mUserId + "   欢迎使用移动OA办公系统!" + "  今天是   " + StringData());
        txtMainScroll.setTextSize(22);
        txtMainScroll.setTextColor(context.getResources().getColor(R.color.text_color));
        txtMainScroll.setBackgroundColor(Color.WHITE);
        //imageLoader.displayImage("http://app1.showapi.com/weather/icon/night/02.png", imgWetherIcon, options);
    }

    private void setWehter() {
        getLocation();
    }

    private  void getLocation() {
        initMap();
    }

    private void initMap() {
        mLocationClient = new AMapLocationClient(getContext());
        mLocationClient.setLocationListener(this);
        initOption();
        mLocationClient.setLocationOption(mLocationOption);

        mLocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if(count == 5) {
                mLocationClient.stopLocation();
                getWetherData();
            }
            else {
                if (amapLocation.getErrorCode() == 0) {
                    latitude = amapLocation.getLatitude();//获取纬度
                    longitude = amapLocation.getLongitude();//获取经度
                    count++;
                }
            }
        }
    }

    private void getWetherData() {
        if(null != wetherService) {

        }
        else {
            wetherService = new WetherService(context);
        }

        wetherService.getWether(String.valueOf(longitude), String.valueOf(latitude), this);
    }

    private void initOption() {
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
    }

    @Override
    public void onStart() {
        super.onStart();
        setWehter();
        ConsoleUtil.i(TAG, "-------Fragment onStart---------------");
    }

    @Override
    public void onResume() {
        super.onResume();
        ConsoleUtil.i(TAG, "-------Fragment onResume---------------");
    }

    @Override
    public void onPause() {
        super.onPause();
        ConsoleUtil.i(TAG, "-------Fragment onPause---------------");
    }

    @Override
    public void onStop() {
        super.onStop();
        ConsoleUtil.i(TAG, "-------Fragment onStop---------------");
    }

    @OnClick(R.id.img_registSchedule)
    void goRegistSchedule() {
//        registScheduleFragment = new RegistScheduleFragment(context);
//        Fragment mainFragment = fragmentManager.getFragments().get(1);

        Fragment ft = fragmentManager.findFragmentByTag("RegistFragment");
        Bundle args = new Bundle();
        registScheduleFragment = ft == null ? RegistScheduleFragment.newInstance(args) : (RegistScheduleFragment) ft;

        //contactFragment = new ContactFragment(context);
        Fragment mainFragment = fragmentManager.findFragmentByTag("MainFragment");

        goView(mainFragment, registScheduleFragment, "RegistFragment");
    }

    @OnClick(R.id.img_contact)
    void goContact() {
        Fragment ft = fragmentManager.findFragmentByTag("ContactFragment");
        Bundle args = new Bundle();
        contactFragment = ft == null ? ContactFragment.newInstance(args) : (ContactFragment) ft;

        //contactFragment = new ContactFragment(context);
        Fragment mainFragment = fragmentManager.findFragmentByTag("MainFragment");
        goView(mainFragment, contactFragment, "ContactFragment");
    }

    @OnClick(R.id.img_searchSchedule)
    void goSearchSchedule() {
//        SearchScheduleFragment searchScheduleFragment = new SearchScheduleFragment(context);
//        Fragment mainFragment = fragmentManager.getFragments().get(1);
        Fragment ft = fragmentManager.findFragmentByTag("SearchScheduleFragment");
        Bundle args = new Bundle();
        SearchScheduleFragment searchScheduleFragment = ft == null ? SearchScheduleFragment.newInstance(args) : (SearchScheduleFragment) ft;

        //contactFragment = new ContactFragment(context);
        Fragment mainFragment = fragmentManager.findFragmentByTag("MainFragment");
        goView(mainFragment, searchScheduleFragment, "SearchScheduleFragment");
    }

    @OnClick(R.id.img_applyList)
    void goApplyList() {
//        ApplyListFragment applyListFragment = new ApplyListFragment(context);
//        Fragment mainFragment = fragmentManager.getFragments().get(1);

        Fragment ft = fragmentManager.findFragmentByTag("ApplyListFragment");
        Bundle args = new Bundle();
        ApplyListFragment applyListFragment = ft == null ? ApplyListFragment.newInstance(args) : (ApplyListFragment) ft;

        //contactFragment = new ContactFragment(context);
        Fragment mainFragment = fragmentManager.findFragmentByTag("MainFragment");
        goView(mainFragment, applyListFragment, "ApplyListFragment");
    }

    @OnClick(R.id.img_approveList)
    void goApproveList() {
//        ApproveListFragment approveListFragment = new ApproveListFragment(context);
//        Fragment mainFragment = fragmentManager.getFragments().get(1);

        Fragment ft = fragmentManager.findFragmentByTag("ApproveListFragment");
        Bundle args = new Bundle();
        ApproveListFragment approveListFragment = ft == null ? ApproveListFragment.newInstance(args) : (ApproveListFragment) ft;

        //contactFragment = new ContactFragment(context);
        Fragment mainFragment = fragmentManager.findFragmentByTag("MainFragment");
        goView(mainFragment, approveListFragment, "ApproveListFragment");
    }

    private void goView(Fragment from, Fragment to, String tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.setCustomAnimations(R.anim.push_up_in, R.anim.push_up_out);
        //hideFragments(transaction);
        if (from.isAdded()) {
            transaction.hide(from).add(R.id.main_content, to, tag).addToBackStack(null).commit();
            //transaction.add(R.id.main_content, registScheduleFragment);
        } else {
            transaction.hide(from).show(to).commit();
            //transaction.show(registScheduleFragment);
        }
//        transaction.addToBackStack(null);
//        transaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {
        //if (registScheduleFragment != null) {
        transaction.hide(this);
        //}
    }


    public String StringData() {

        String mYear;
        String mMonth;
        String mDay;
        String mWay;

        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(mWay)) {
            mWay = "天";
        } else if ("2".equals(mWay)) {
            mWay = "一";
        } else if ("3".equals(mWay)) {
            mWay = "二";
        } else if ("4".equals(mWay)) {
            mWay = "三";
        } else if ("5".equals(mWay)) {
            mWay = "四";
        } else if ("6".equals(mWay)) {
            mWay = "五";
        } else if ("7".equals(mWay)) {
            mWay = "六";
        }
        return mYear + "年" + mMonth + "月" + mDay + "日" + "   星期" + mWay;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSuccess(List<ApproveListInfo> list) {
        Intent intent = new Intent(getActivity(), DeskCountChangeReceiver.class);
        intent.setAction("EOAS_COUNT_CHANGED");
        intent.putExtra("COUNT", String.valueOf(list.size() == 0 ? 0 : list.size()));
        getContext().sendBroadcast(intent);
    }

    @Override
    public void onFailure(int code, String msg) {

    }

    @Override
    public void onGetSucess(WetherInfo info) {
        temperature.setText(info.temperature);
        weather.setText(info.weather);
        hightLow.setText(info.hightTemp + "℃/" + info.lowTemp + "℃");
        windAndSD.setText(info.wind_direction + "  " + info.wind_power + "  湿度  " + info.sd
        );
        imageLoader.displayImage(info.weather_pic, imgWetherIcon, options);
    }

    @Override
    public void onGetFailure(int code, String message) {
        if(null != wetherService) {
            wetherService.cancelRequest();
        }
    }
}
