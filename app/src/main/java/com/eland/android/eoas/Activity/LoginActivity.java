package com.eland.android.eoas.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.eland.android.eoas.Model.Constant;
import com.eland.android.eoas.Model.LoginInfo;
import com.eland.android.eoas.R;
import com.eland.android.eoas.Service.LoginService;
import com.eland.android.eoas.Util.CacheInfoUtil;
import com.eland.android.eoas.Util.ConsoleUtil;
import com.eland.android.eoas.Util.ProgressUtil;
import com.eland.android.eoas.Util.SharedReferenceHelper;
import com.eland.android.eoas.Util.ToastUtil;
import com.eland.android.eoas.Views.EditTextView;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.rey.material.widget.Button;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by liu.wenbin on 15/11/10.
 */
public class LoginActivity extends AppCompatActivity implements
        LoginService.ISignInListener, ProgressUtil.IOnDialogConfirmListener{

    @Bind(R.id.edit_username)
    EditTextView editUsername;
    @Bind(R.id.edit_password)
    EditTextView editPassword;
    @Bind(R.id.btn_login)
    Button btnLogin;
    private String TAG = "EOAS";
    private Dialog progressDialog;
    private Context context;
    private LoginService loginService;
    private TelephonyManager telephonyManager;
    private String loginId,password = "";
    private int loginFailCount = 0;

    private UpdateManagerListener updateManagerListener;
    private String downUri;
    private ProgressUtil dialogUtil;
    private MaterialDialog updateDialog;
    private String theme = "";

    private static final int REQUEST_CODE_WRITEEXTERNAL_STORAGE = 1;
    private static final int REQUECT_CODE_READPHONESTATE = 2;
    private static final int REQEST_CODE_ACCESS_FINE_LOCATION = 3;
    private static final int REQEST_CODE_RECORD_AUDIO = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //read phone state
        if (!MPermissions.shouldShowRequestPermissionRationale(LoginActivity.this,
                Manifest.permission.READ_PHONE_STATE, REQUECT_CODE_READPHONESTATE)) {
            MPermissions.requestPermissions(LoginActivity.this, REQUECT_CODE_READPHONESTATE, Manifest.permission.READ_PHONE_STATE);
        }

        theme = SharedReferenceHelper.getInstance(this).getValue(Constant.EOAS_THEME);
        if(!theme.isEmpty()) {
            if(theme.equals("RED")) {
                setTheme(R.style.LoginThemeRed);
            }
            else {
                setTheme(R.style.LoginThemeBlue);
            }
        }
        else {
            setTheme(R.style.LoginThemeRed);
        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        context = this;
        ButterKnife.bind(this);

        initActivity();
        initUpdate();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @PermissionGrant(REQUEST_CODE_WRITEEXTERNAL_STORAGE)
    public void requestExternalSuccess()
    {
        //access gps
        if (!MPermissions.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION, REQEST_CODE_ACCESS_FINE_LOCATION))
        {
            MPermissions.requestPermissions(LoginActivity.this, REQEST_CODE_ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @PermissionDenied(REQUEST_CODE_WRITEEXTERNAL_STORAGE)
    public void requestExternalFailed()
    {
        //Toast.makeText(this, "DENY ACCESS SDCARD!", Toast.LENGTH_SHORT).show();
    }

    @PermissionGrant(REQUECT_CODE_READPHONESTATE)
    public void requestPhoneStateSuccess()
    {
        //access EXTERNAL
        if (!MPermissions.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_CODE_WRITEEXTERNAL_STORAGE))
        {
            MPermissions.requestPermissions(LoginActivity.this, REQUEST_CODE_WRITEEXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @PermissionDenied(REQUECT_CODE_READPHONESTATE)
    public void requestPhoneStateFailed()
    {
        //Toast.makeText(this, "DENY ACCESS SDCARD!", Toast.LENGTH_SHORT).show();
    }

    @PermissionGrant(REQEST_CODE_ACCESS_FINE_LOCATION)
    public void requestLocationSuccess()
    {
        //Toast.makeText(this, "GRANT ACCESS SDCARD!", Toast.LENGTH_SHORT).show();
        //access Mic
        if (!MPermissions.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.RECORD_AUDIO, REQEST_CODE_RECORD_AUDIO))
        {
            MPermissions.requestPermissions(LoginActivity.this, REQEST_CODE_RECORD_AUDIO, Manifest.permission.RECORD_AUDIO);
        }
    }

    @PermissionDenied(REQUECT_CODE_READPHONESTATE)
    public void requestMicFailed()
    {
        //Toast.makeText(this, "DENY ACCESS SDCARD!", Toast.LENGTH_SHORT).show();
    }

    @PermissionGrant(REQEST_CODE_RECORD_AUDIO)
    public void requestMicSuccess()
    {
        //Toast.makeText(this, "GRANT ACCESS SDCARD!", Toast.LENGTH_SHORT).show();
    }

    @PermissionDenied(REQEST_CODE_RECORD_AUDIO)
    public void requestLocationFailed()
    {
        //Toast.makeText(this, "DENY ACCESS SDCARD!", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_login)
    void login() {
        Boolean isGo = invalidateInput();

        if(isGo) {
            progressDialog = ProgressUtil.showProgress(context);
            loginService.signIn(loginId, password, "", telephonyManager.getDeviceId());
        }
    }

    private void initActivity() {
        //如果进入登录页面说明还未登录，暂不能接收push
        JPushInterface.stopPush(getApplicationContext());

        loginService = new LoginService(context);
        loginService.setOnSignInListener(this);
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        dialogUtil = new ProgressUtil();
        dialogUtil.setOnDialogConfirmListener(this);
    }

    private void initUpdate() {
        updateManagerListener = new UpdateManagerListener() {
            @Override
            public void onNoUpdateAvailable() {

            }

            @Override
            public void onUpdateAvailable(String result) {
                AppBean appBean = getAppBeanFromString(result);
                String message = appBean.getReleaseNote();
                downUri = appBean.getDownloadURL();

                updateDialog = dialogUtil.showDialogUpdate(message, getResources().getString(R.string.hasupdate), context);
                updateDialog.show();
            }
        };

        //检测更新
        PgyUpdateManager.register(this, updateManagerListener);
    }

    private Boolean invalidateInput() {
        loginId = editUsername.getText().toString();
        password = editPassword.getText().toString();

        if(loginId.isEmpty() && password.isEmpty()) {
            editUsername.setShakeAnimation();
            editPassword.setShakeAnimation();

            ToastUtil.showToast(this, "用户名或密码不能为空", Toast.LENGTH_LONG);
            return false;
        }

        if(loginId.isEmpty()) {
            editUsername.setShakeAnimation();

            ToastUtil.showToast(this, "用户名不能为空", Toast.LENGTH_LONG);
            return false;
        }

        if(password.isEmpty()) {
            editPassword.setShakeAnimation();

            ToastUtil.showToast(this, "密码不能为空", Toast.LENGTH_LONG);
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
    }

    @Override
    public void onSignInSuccess(LoginInfo info) {
        if(progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        SharedReferenceHelper.getInstance(context).setValue(Constant.LOGINID, loginId);
        SharedReferenceHelper.getInstance(context).setValue(Constant.LOGIN_SUCCESS, "TRUE");
        SharedReferenceHelper.getInstance(context).setValue(Constant.LOGINID_CELLNO, info.cellNo);
        SharedReferenceHelper.getInstance(context).setValue(Constant.LOGINID_EMAIL, info.email);
        SharedReferenceHelper.getInstance(context).setValue(Constant.LOGINID_USERNAME, info.userName);

        //登录系统后开启接收push,当然需要判断下用户是否设置了接收与否
        if(CacheInfoUtil.loadIsReceive(this, loginId)) {
            JPushInterface.resumePush(getApplicationContext());
        }

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSignInFailure(int code, String msg) {
        String message = "";

        if(code == 99999) {
            message = "服务器异常";
        }
        else {
            message = msg;
        }

        if(loginFailCount == 1) {
            ToastUtil.showToast(context, message, Toast.LENGTH_LONG);
            if(progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            SharedReferenceHelper.getInstance(context).setValue(Constant.LOGINID, "");
            SharedReferenceHelper.getInstance(context).setValue(Constant.LOGIN_SUCCESS, "");
            SharedReferenceHelper.getInstance(context).setValue(Constant.LOGINID_CELLNO, "");
            SharedReferenceHelper.getInstance(context).setValue(Constant.LOGINID_EMAIL, "");
            SharedReferenceHelper.getInstance(context).setValue(Constant.LOGINID_USERNAME, "");
        }
        else {
            loginFailCount++;
            ConsoleUtil.i(TAG, "--------try login again------------");
            loginService.signIn(loginId, password, "", telephonyManager.getDeviceId());
        }
    }

    @Override
    public void OnDialogConfirmListener() {
        updateDialog.dismiss();
        updateManagerListener.startDownloadTask(LoginActivity.this, downUri);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginService.cancel();
    }
}
