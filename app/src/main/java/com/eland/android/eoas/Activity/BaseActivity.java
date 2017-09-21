package com.eland.android.eoas.Activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

/**
 * Created by liuwenbin on 2017/2/8.
 */

public class BaseActivity extends AppCompatActivity{

    private static final int REQUECT_CODE_SDCARD = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!MPermissions.shouldShowRequestPermissionRationale(BaseActivity.this, Manifest.permission.READ_PHONE_STATE, REQUECT_CODE_SDCARD)) {
            MPermissions.requestPermissions(BaseActivity.this, REQUECT_CODE_SDCARD, Manifest.permission.READ_PHONE_STATE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @PermissionGrant(REQUECT_CODE_SDCARD)
    public void requestPhoneStateSuccess()
    {
        Toast.makeText(this, "GRANT ACCESS SDCARD!", Toast.LENGTH_SHORT).show();
    }

    @PermissionDenied(REQUECT_CODE_SDCARD)
    public void requestPhoneStateFailed()
    {
        Toast.makeText(this, "DENY ACCESS SDCARD!", Toast.LENGTH_SHORT).show();
    }
}
