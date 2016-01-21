package com.eland.android.eoas.Fragment;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.eland.android.eoas.R;
import com.eland.android.eoas.Service.RegWorkInfoService;
import com.eland.android.eoas.Service.ScheduleService;
import com.eland.android.eoas.Util.ConsoleUtil;
import com.eland.android.eoas.Util.SystemMethodUtil;
import com.eland.android.eoas.Util.ToastUtil;

import java.io.IOException;
import java.util.GregorianCalendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by liu.wenbin on 15/11/30.
 */
public class RegistScheduleFragment extends Fragment implements AnimationListener, ScheduleService.IScheduleListener {

    @Bind(R.id.img_gif)
    GifImageView imgGif;
    @Bind(R.id.txt_distance)
    TextView txtDistance;
    private String TAG = "EOAS";
    private Context context;
    private View rootView;
    private GifDrawable gifDrawable = null;
    private Boolean autoRegist = false;
    private ScheduleService scheduleService;
    TelephonyManager telephonyManager;
    private String imei;
    private int am_pm;
    private String isAM;
    private RegWorkInfoService regWorkInfoService;
    public int CANREGIST = 0;
    public int NOTREGIST = 1;

    public RegistScheduleFragment() {
        super();
    }

    public static RegistScheduleFragment newInstance(Bundle args) {
        RegistScheduleFragment f = new RegistScheduleFragment();
        f.setArguments(args);
        return f;
    }

    @SuppressLint("ValidFragment")
    public RegistScheduleFragment(Context context) {
        //this.context = context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.registschedule_fragment, null);
        ButterKnife.bind(this, rootView);

        this.context = getContext();

        initFragment();

        if (autoRegist) {
            setActiveImg();
        }

        return rootView;
    }

    private void initFragment() {
        scheduleService = new ScheduleService();
        scheduleService.setOnScheduleListener(this);
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        imei = telephonyManager.getDeviceId();
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        am_pm = gregorianCalendar.get(GregorianCalendar.AM_PM);
        if (am_pm == 0) {
            isAM = "AM";
            ConsoleUtil.i(TAG, "AM");
        } else {
            isAM = "PM";
            ConsoleUtil.i(TAG, "PM");
        }
    }

    @OnClick(R.id.img_gif)
    void regist() {
        if (gifDrawable != null) {
            setDisableImg();
        } else {
            setActiveImg();
        }
    }

    private void setActiveImg() {
        try {
            gifDrawable = new GifDrawable(getContext().getResources(), R.drawable.schedule_active);
            imgGif.setImageDrawable(gifDrawable);
            gifDrawable.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        startRegist();
    }

    private void startRegist() {

        if (isAM.equals("AM")) {
            Intent intent = new Intent(context, RegWorkInfoService.class);
            context.bindService(intent, conn, Context.BIND_AUTO_CREATE);
        } else {
            scheduleService.regSchedulePM(imei, isAM);
        }
    }

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //返回一个MsgService对象
            regWorkInfoService = ((RegWorkInfoService.myBinder) service).getService();
            regWorkInfoService.startService(imei, isAM, "MANUAL");
            regWorkInfoService.setOnLocationLinstener(new RegWorkInfoService.ILocationListener() {
                @Override
                public void onLocationSuccess(float distance) {
                    ConsoleUtil.i(TAG, String.valueOf(distance));

                    Message msg = new Message();
                    if (distance < 300.00f) {
                        msg.what = CANREGIST;
                        handler.sendMessage(msg);
                    } else {
                        msg.what = NOTREGIST;
                        msg.obj = distance;
                        handler.sendMessage(msg);
                    }
                }
            });
        }
    };

    public Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == CANREGIST) {
                context.unbindService(conn);
                scheduleService.regScheduleAM(imei, isAM);
                txtDistance.setVisibility(View.GONE);
            } else {
                float distance = (float)msg.obj;
                txtDistance.setVisibility(View.VISIBLE);
                txtDistance.setText("距离考勤点还有" + String.valueOf(distance) + "米");
            }
        }
    };

    private void setDisableImg() {
        if(null != gifDrawable && gifDrawable.isPlaying()) {
            //gifDrawable.stop();
            gifDrawable.recycle();
            imgGif.setImageDrawable(context.getResources().getDrawable(R.drawable.schedule_nomor));
        }
        stopRegist();
    }

    private void stopRegist() {
        if(SystemMethodUtil.isWorked(getContext(), "RegWorkInfoService")) {
            ConsoleUtil.i(TAG, "---------------Service is running,you can stop it.--------------");
            if(null != conn) {
                context.unbindService(conn);
                ConsoleUtil.i(TAG, "---------------stop success.--------------");
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);

//        if(null != conn) {
//            context.unbindService(conn);
//        }
    }

    @Override
    public void onAnimationCompleted(final int loopNumber) {
        final View view = getView();
        if (view != null) {
            //ToastUtil.showToast(context, "完事了啊", Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onScheduleSuccess() {
        ToastUtil.showToast(context, "打卡成功.", Toast.LENGTH_LONG);
        setDisableImg();
    }

    @Override
    public void onScheduleFailure(int code, String msg) {
        ToastUtil.showToast(context, msg, Toast.LENGTH_LONG);
        setDisableImg();
    }
}
