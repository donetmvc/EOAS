package com.eland.android.eoas.Activity;

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
import com.eland.android.eoas.Util.ConsoleUtil;
import com.eland.android.eoas.Util.ProgressUtil;
import com.eland.android.eoas.Util.SharedReferenceHelper;
import com.eland.android.eoas.Util.ToastUtil;
import com.eland.android.eoas.Views.EditTextView;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.rey.material.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        ButterKnife.bind(this);

        initActivity();
        initUpdate();
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
}
