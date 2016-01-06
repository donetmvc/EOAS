package com.eland.android.eoas.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.eland.android.eoas.Model.Constant;
import com.eland.android.eoas.R;
import com.eland.android.eoas.Service.ApplyService;
import com.eland.android.eoas.Util.ConsoleUtil;
import com.eland.android.eoas.Util.ProgressUtil;
import com.eland.android.eoas.Util.SharedReferenceHelper;
import com.eland.android.eoas.Util.ToastUtil;
import com.eland.android.eoas.Views.stepView.StepsView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by liu.wenbin on 15/12/29.
 */
public class ApplyStateActivity extends AppCompatActivity implements ApplyService.IOnApplyListener{

    @Bind(R.id.applytoolbar)
    Toolbar applytoolbar;
    @Bind(R.id.step)
    StepsView step;

    private String TAG = "EOAS";

    private String applyId;
    private Dialog httpDialog;
    private String[] approves;// = {"审批人1", "审批人2", "审批人3"};
    private int approvePosition = 0;
    private String mUserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_apply_state);
        ButterKnife.bind(this);

        mUserId = SharedReferenceHelper.getInstance(this).getValue(Constant.LOGINID);
        initToolbar();
        initParames();

        initApproveState();
    }

    private void initToolbar() {
        applytoolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        applytoolbar.setTitle("批准流程");
        setSupportActionBar(applytoolbar);
        applytoolbar.setNavigationIcon(R.mipmap.icon_back);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        applytoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initParames() {
        Intent intent = getIntent();

        if (null != intent) {
            applyId = intent.getStringExtra("VACATIONNO");
            ConsoleUtil.i(TAG, applyId);
        }
    }

    private void initApproveState() {
        httpDialog = ProgressUtil.showHttpLoading(this);

        ApplyService.searchApprogressList(mUserId, applyId, this);
    }

    @Override
    public void onSuccess(JSONObject obj) {

    }

    @Override
    public void onSuccess(JSONArray array) {
        if(null != array && array.length() > 0) {
            approves = new String[array.length()];
            try {

                for(int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    approves[i] = obj.getString("ApproveUserName");
                    String approveState = obj.getString("ApproveStateName");
                    if(approveState.equals("批准")) {
                        approvePosition++;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            step.setLabels(approves)
                    .setBarColor(Color.GRAY)
                    .setLabelColor(Color.GRAY)
                    .setColorIndicator(getResources().getColor(R.color.md_red_500))
                    .setCompletedPosition(approvePosition == 0 ? 0 : approvePosition - 1);
        }

        if(httpDialog.isShowing()) {
            httpDialog.dismiss();
        }
    }

    @Override
    public void onSuccess(String diffDays) {

    }

    @Override
    public void onSuccess(int type, String msg) {

    }

    @Override
    public void onFailure(int type, int code, String msg) {
        ToastUtil.showToast(this, msg, Toast.LENGTH_LONG);
        if(httpDialog.isShowing()) {
            httpDialog.dismiss();
        }
    }
}
