package com.eland.android.eoas.Fragment;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eland.android.eoas.Activity.LoginActivity;
import com.eland.android.eoas.Activity.MainActivity;
import com.eland.android.eoas.Application.EOASApplication;
import com.eland.android.eoas.Model.Constant;
import com.eland.android.eoas.Model.RegAutoInfo;
import com.eland.android.eoas.R;
import com.eland.android.eoas.Receiver.AutoReceiver;
import com.eland.android.eoas.Service.LogOutService;
import com.eland.android.eoas.Service.RegAutoService;
import com.eland.android.eoas.Service.UpdatePhoneNmService;
import com.eland.android.eoas.Service.UploadFileService;
import com.eland.android.eoas.Util.CacheInfoUtil;
import com.eland.android.eoas.Util.ChooseImageUtil;
import com.eland.android.eoas.Util.ConsoleUtil;
import com.eland.android.eoas.Util.FileUtil;
import com.eland.android.eoas.Util.ProgressUtil;
import com.eland.android.eoas.Util.SharedReferenceHelper;
import com.eland.android.eoas.Util.SystemMethodUtil;
import com.eland.android.eoas.Util.ToastUtil;
import com.eland.android.eoas.Views.CircleImageView;
import com.eland.android.eoas.Views.PhoneNumberView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.rey.material.widget.Switch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import me.drakeet.materialdialog.MaterialDialog;


/**
 * Created by liu.wenbin on 15/12/3.
 */
@SuppressLint("ValidFragment")
public class DrawerFragment extends Fragment implements ChooseImageUtil.IOnCarmerListener,
        ChooseImageUtil.IOnAlbumListener, UploadFileService.IOnUploadListener,
        PhoneNumberView.OnActionSheetSelected, UpdatePhoneNmService.IOnUpdateListener,
        ProgressUtil.IOnDialogConfirmListener {

    @Bind(R.id.img_photo)
    CircleImageView imgPhoto;
    @Bind(R.id.txt_name)
    TextView txtName;
    @Bind(R.id.txt_tel)
    TextView txtTel;
    @Bind(R.id.img_more)
    ImageView imgMore;
    @Bind(R.id.txt_email)
    TextView txtEmail;
    @Bind(R.id.user_info_layout)
    RelativeLayout userInfoLayout;
    @Bind(R.id.txt_logout)
    TextView txtLogout;
    @Bind(R.id.lin_logout)
    LinearLayout linLogout;
    @Bind(R.id.txt_versionname)
    TextView txtVersionname;
    @Bind(R.id.lin_update)
    LinearLayout linUpdate;
    private String TAG = "EOAS";
    private MainActivity mainActivity;
    private View rootView;
    private Switch aSwitch;
    private Switch bSwitch;
    private String loginId;
    private String imei;
    private String userName;
    private String cellNo;
    private String email;
    private ChooseImageUtil chooseImageUtil;

    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_IMAGE = 2;
    private static final int REQUEST_CODE_RESULT = 3;
    private String dateTime;
    private com.rey.material.app.Dialog imgDialog;
    private com.rey.material.app.Dialog httpDialog;
    private Dialog updateNumDialog;

    private String imgUri = "";//"http://182.92.65.253:30001/Eland.EOAS/images/chengcuicui.jpg";
    private String imageLocalPath = "";
    private String fileName = "";
    Bitmap bitmap = null;
    private UploadFileService uploadFileService;
    private Animation animation;
    private UpdatePhoneNmService updateService;

    //clock
    private PendingIntent pi;
    private AlarmManager alar;
    private Boolean running = false;

    //check update
    private UpdateManagerListener updateManagerListener;
    private String downUri;
    private ProgressUtil dialogUtil;
    private MaterialDialog updateDialog;

    //get app version info
    PackageManager packageManager;
    PackageInfo packageInfo;

    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.mipmap.eland_log)
            .showImageOnFail(R.mipmap.eland_log)
            .resetViewBeforeLoading(true)
            .cacheOnDisc(true)
            .cacheInMemory(true)
            .imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .considerExifParams(true)
            .displayer(new FadeInBitmapDisplayer(300))
            .build();

    public DrawerFragment() {
    }

    public DrawerFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_drawer, container, false);
        ButterKnife.bind(this, rootView);

        aSwitch = (Switch) rootView.findViewById(R.id.sw_auto);
        bSwitch = (Switch) rootView.findViewById(R.id.sw_receive);
        chooseImageUtil = new ChooseImageUtil();
        uploadFileService = new UploadFileService(getContext());
        updateService = new UpdatePhoneNmService();
        chooseImageUtil.setOnAlbumListener(this);
        chooseImageUtil.setOnCarmerListener(this);
        uploadFileService.setUploadListener(this);
        dialogUtil = new ProgressUtil();
        dialogUtil.setOnDialogConfirmListener(this);

        packageManager = getActivity().getPackageManager();

        try {
            packageInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        initView();
        return rootView;
    }

    private void initView() {
        loginId = SharedReferenceHelper.getInstance(getContext()).getValue(Constant.LOGINID);
        cellNo = SharedReferenceHelper.getInstance(getContext()).getValue(Constant.LOGINID_CELLNO);
        email = SharedReferenceHelper.getInstance(getContext()).getValue(Constant.LOGINID_EMAIL);
        userName = SharedReferenceHelper.getInstance(getContext()).getValue(Constant.LOGINID_USERNAME);
        animation = AnimationUtils.loadAnimation(getContext(), R.anim.image_circel);

        initClock();

        //设置用户名
        if (!loginId.isEmpty()) {
            fileName = loginId.replace(".", "");
            imgUri = EOASApplication.getInstance().photoUri + fileName + ".jpg";
        } else {
            txtName.setText("无名氏");
            fileName = "sysadmin";
        }
        //设置用户
        txtName.setText(userName);
        //设置邮箱地址
        txtEmail.setText(email);
        //设置电话号码
        txtTel.setText(cellNo);

        //设置自动出勤与否
        if (CacheInfoUtil.loadIsRegAuto(getContext(), loginId)) {
            aSwitch.setChecked(true);
        } else {
            aSwitch.setChecked(false);
        }

        //设置用户是否接收push
        if(CacheInfoUtil.loadIsReceive(getContext(), loginId)) {
            bSwitch.setChecked(true);
        } else {
            bSwitch.setChecked(false);
        }


        //设置头像
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getActivity()));
        ImageLoader.getInstance().displayImage(imgUri, imgPhoto, options);

        //设置版本信息
        txtVersionname.setText("当前版本(" + packageInfo.versionName + ")");

        aSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                ConsoleUtil.i(TAG, String.valueOf(checked));
                TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
                imei = telephonyManager.getDeviceId();

                ArrayList<RegAutoInfo> list = new ArrayList<RegAutoInfo>();
                RegAutoInfo reg = new RegAutoInfo();
                reg.userId = loginId;
                reg.imei = imei;

                if (checked) {
                    reg.isRegAuto = "TRUE";
                    list.add(reg);
                    CacheInfoUtil.saveIsRegAuto(getContext(), list);

                    startRegAutoService();
                } else {
                    reg.isRegAuto = "FALSE";
                    list.add(reg);
                    CacheInfoUtil.saveIsRegAuto(getContext(), list);

                    stopRegAutoService();
                }
            }
        });

        bSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {

                ArrayList<RegAutoInfo> list = new ArrayList<RegAutoInfo>();
                RegAutoInfo reg = new RegAutoInfo();
                reg.userId = loginId;

                if (checked) {
                    reg.isReceive = "TRUE";
                    list.add(reg);
                    CacheInfoUtil.saveIsReceive(getContext(), list);

                    JPushInterface.onResume(getContext().getApplicationContext());
                } else {
                    reg.isReceive = "FALSE";
                    list.add(reg);
                    CacheInfoUtil.saveIsReceive(getContext(), list);

                    JPushInterface.stopPush(getContext().getApplicationContext());
                }

            }
        });
    }

    private void initClock() {
        Intent intent = new Intent(getActivity(), AutoReceiver.class);
        intent.setAction("REG_AUTO");
        alar = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        pi = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void startRegAutoService() {
        //设置闹钟服务
        alar.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10 * 1000, pi);
    }

    private void stopRegAutoService() {
        //取消闹钟
        alar.cancel(pi);
        //check service is running
        running = checkRegServiceIsRunning();

        if (running) {
            //stop service
            Intent intent = new Intent(getActivity(), RegAutoService.class);
            getContext().stopService(intent);
        }
    }

    private Boolean checkRegServiceIsRunning() {
        running = SystemMethodUtil.isWorked(getContext(), "RegAutoService");
        return running;
    }

    @OnClick(R.id.img_photo)
    void showChooseImage() {
        imgDialog = chooseImageUtil.showChooseImageDialog(getContext());
    }

    @OnClick(R.id.img_more)
    void showMoreAction() {
        updateNumDialog = PhoneNumberView.showNumActionSheete(mainActivity, this);
        //dialog.se
    }

    @OnClick(R.id.lin_update)
    void checkUpdate() {
        updateManagerListener = new UpdateManagerListener() {
            @Override
            public void onNoUpdateAvailable() {
                ToastUtil.showToast(getContext(), "已经是最新版本啦", Toast.LENGTH_SHORT);
            }

            @Override
            public void onUpdateAvailable(String result) {
                AppBean appBean = getAppBeanFromString(result);
                String message = appBean.getReleaseNote();
                downUri = appBean.getDownloadURL();

                updateDialog = dialogUtil.showDialogUpdate(message, getResources().getString(R.string.hasupdate), mainActivity);
                updateDialog.show();
            }
        };

        //检测更新
        PgyUpdateManager.register(getActivity(), updateManagerListener);
    }

    @OnClick(R.id.lin_logout)
    void logOut() {
        clearCach();
        //关闭push服务
        JPushInterface.stopPush(getContext().getApplicationContext());
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);

        Intent serviceIntent = new Intent(getActivity(), LogOutService.class);
        getContext().startService(serviceIntent);

        getActivity().finish();
    }

    private void clearCach() {
        SharedReferenceHelper.getInstance(getContext()).clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != bitmap) {
            bitmap.recycle();
        }
        ButterKnife.unbind(this);
    }

    @Override
    public void onAlbumClick() {
        ConsoleUtil.i(TAG, "Album");
        Date date = new Date(System.currentTimeMillis());
        dateTime = date.getTime() + "";
        getPicFromAlbum();
        imgDialog.dismiss();
    }

    @Override
    public void onCarmerClick() {
        ConsoleUtil.i(TAG, "Carmer");
        Date date = new Date(System.currentTimeMillis());
        dateTime = date.getTime() + "";
        getPicFromCamere();
        imgDialog.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_RESULT:

                    break;
                case REQUEST_CODE_IMAGE:
                    //String fileName = null;
                    if (data != null) {
                        Uri originalUri = data.getData();
                        ContentResolver cr = getActivity().getContentResolver();
                        //FileUtil.compressImageFromFile(fileName);
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(cr, originalUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        imageLocalPath = FileUtil.saveToSdCard(bitmap, getContext(), fileName);
                        ConsoleUtil.d(TAG, "------imageLocalPath--------" + imageLocalPath);
                        //imgPhoto.setBackgroundDrawable(new BitmapDrawable(bitmap));
                        //imgPhoto.setImageBitmap(bitmap);

                        Message message = new Message();
                        message.obj = bitmap;

                        if (null != animation) {
                            imgPhoto.startAnimation(animation);
                        }

                        uploadFileService.uploadFile(imageLocalPath, fileName, message);
                    }
                    break;
                case REQUEST_CODE_CAMERA:
                    String files = FileUtil.getCacheDirectory(getContext(), true, "pic") + dateTime;
                    File file = new File(files);
                    if (file.exists()) {
                        Bitmap bitmap = FileUtil.compressImageFromFile(files);
                        imageLocalPath = FileUtil.saveToSdCard(bitmap, getContext(), fileName);
                        //imgPhoto.setImageBitmap(bitmap);
                        Message message = new Message();
                        message.obj = bitmap;

                        if (null != animation) {
                            imgPhoto.startAnimation(animation);
                        }

                        uploadFileService.uploadFile(imageLocalPath, fileName, message);
                    } else {

                    }
                    break;
                default:
                    break;
            }
        }
    }

    public Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    setBitmap((Bitmap) msg.obj);
                    break;
                case 1:
                    break;
                case 2:
                    txtTel.setText((String) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    private void setBitmap(Bitmap bitmap) {
        imgPhoto.setImageBitmap(bitmap);
    }

    /**
     * 图库
     */
    private void getPicFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_CODE_IMAGE);
    }

    /**
     * 照相机
     */
    private void getPicFromCamere() {
        File f = new File(FileUtil.getCacheDirectory(getContext(), true, "pic") + dateTime);
        if (f.exists()) {
            f.delete();
        }
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Uri uri = Uri.fromFile(f);
        Log.d(TAG, "---------------" + uri + "");

        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(camera, REQUEST_CODE_CAMERA);
    }

    @Override
    public void onUploadSuccess(Message message) {
        handler.sendMessage(message);
        clean();
        ToastUtil.showToast(getContext(), "上传头像成功", Toast.LENGTH_LONG);
    }

    @Override
    public void onUploadFailure(Message message) {
        handler.sendMessage(message);
        clean();
        ToastUtil.showToast(getContext(), "上传失败，请重试", Toast.LENGTH_LONG);
    }

    private void clean() {
        imgPhoto.clearAnimation();
        ImageLoader.getInstance().clearDiskCache();
        ImageLoader.getInstance().clearMemoryCache();
    }

    @Override
    public void onClick(String numValue) {
        //ConsoleUtil.i(TAG, numValue);
        if (numValue.isEmpty()) {
            ToastUtil.showToast(getContext(), "电话号码不能为空", Toast.LENGTH_LONG);
            return;
        }
        if (!numValue.isEmpty() && numValue.length() != 11) {
            ToastUtil.showToast(getContext(), "电话号码位数不正确", Toast.LENGTH_LONG);
            return;
        }
        httpDialog = ProgressUtil.showHttpLoading(getContext());

        goUpdatePhoneNumber(numValue);
    }

    private void goUpdatePhoneNumber(String numValue) {
        updateService.updateCellNo(loginId, numValue, this);
    }

    @Override
    public void onUpdateSuccess(String cellNo) {
        ToastUtil.showToast(getContext(), "电话号码修改成功", Toast.LENGTH_LONG);
        SharedReferenceHelper.getInstance(getContext()).setValue(Constant.LOGINID_CELLNO, cellNo);

        Message message = new Message();
        message.what = 2;
        message.obj = cellNo;
        handler.sendMessage(message);

        if (httpDialog.isShowing()) {
            httpDialog.dismiss();
        }
        if (updateNumDialog.isShowing()) {
            updateNumDialog.dismiss();
        }
    }

    @Override
    public void onUpdateFailure(int code, String msg) {
        ToastUtil.showToast(getContext(), "电话号码修改失败", Toast.LENGTH_LONG);
        if (httpDialog.isShowing()) {
            httpDialog.dismiss();
        }
    }

    @Override
    public void OnDialogConfirmListener() {
        if(null != updateDialog) {
            updateDialog.dismiss();
            updateManagerListener.startDownloadTask(getActivity(), downUri);
        }
    }
}
