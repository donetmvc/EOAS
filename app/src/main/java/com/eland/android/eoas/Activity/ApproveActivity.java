package com.eland.android.eoas.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eland.android.eoas.Model.Constant;
import com.eland.android.eoas.R;
import com.eland.android.eoas.Service.ApplyService;
import com.eland.android.eoas.Service.ApproveService;
import com.eland.android.eoas.Util.ProgressUtil;
import com.eland.android.eoas.Util.SharedReferenceHelper;
import com.eland.android.eoas.Util.ToastUtil;
import com.eland.android.eoas.Views.stepView.StepsView;
import com.rey.material.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by liu.wenbin on 16/1/7.
 * 批准页面
 */
public class ApproveActivity extends AppCompatActivity implements
        ApplyService.IOnApplyListener, ApproveService.IOnApproveListener {

    @Bind(R.id.approvetoolbar)
    Toolbar approvetoolbar;
    @Bind(R.id.txt_applyId)
    TextView txtApplyId;
    @Bind(R.id.txt_content)
    TextView txtContent;
    @Bind(R.id.txt_remark)
    TextView txtRemark;
    @Bind(R.id.step)
    StepsView step;
    @Bind(R.id.btn_approve)
    Button btnApprove;
    @Bind(R.id.btn_refuse)
    Button btnRefuse;
    @Bind(R.id.edit_remark)
    EditText editRemark;
    private String mUserId, applyId;
    private Dialog httpDialog;
    private String[] approves;
    private int approvePosition = 0;
    private int approveType = 0;
    private ApproveService approveService;
    private ApplyService applyService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        String theme = SharedReferenceHelper.getInstance(this).getValue(Constant.EOAS_THEME);
        if(!theme.isEmpty()) {
            if(theme.equals("RED")) {
                setTheme(R.style.MainThenmeRed);
            }
            else {
                setTheme(R.style.MainThenmeBlue);
            }
        }
        else {
            setTheme(R.style.MainThenmeRed);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve);
        ButterKnife.bind(this);
        approveService = new ApproveService(this);
        applyService = new ApplyService(this);

        initParams();
        initToolbar();
        initView();
    }

    private void initParams() {
        mUserId = SharedReferenceHelper.getInstance(this).getValue(Constant.LOGINID);
        Intent intent = getIntent();

        if (null != intent) {
            String param = intent.getStringExtra("VACATIONNO");
            if (null != param && !param.isEmpty()) {
                applyId = param;
                approveType = 0;
            }
            else {
                Bundle bundle = intent.getExtras();
                if(null != bundle) {
                    String applyJson = bundle.getString(JPushInterface.EXTRA_EXTRA);
                    JSONObject obj;
                    try {
                        obj = new JSONObject(applyJson);
                        applyId = obj.getString("ApplyID");
                        approveType = 1;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void initToolbar() {
        approvetoolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        approvetoolbar.setTitle("休假批准");
        setSupportActionBar(approvetoolbar);
        approvetoolbar.setNavigationIcon(R.mipmap.icon_back);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        approvetoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView() {

        if(applyId == null || applyId.isEmpty()) {
            ToastUtil.showToast(this, "没有可批准的信息", Toast.LENGTH_SHORT);
            return;
        }
        httpDialog = ProgressUtil.showHttpLoading(this);
        startSearchApproveInfo();
    }

    private void startSearchApproveInfo() {
        approveService.searchApplyInfo(mUserId, applyId, this);
    }

    @OnClick(R.id.btn_approve)
    void approve() {
        if(null == httpDialog) {
            httpDialog = ProgressUtil.showHttpLoading(this);
        }
        httpDialog.show();
        approveService.saveApprove(mUserId, "01", editRemark.getText().toString(), applyId, this);
    }

    @OnClick(R.id.btn_refuse)
    void refuse() {

        if(editRemark.getText().toString().isEmpty()) {
            ToastUtil.showToast(this, "拒绝必须填写理由", Toast.LENGTH_LONG);
            return;
        }

        if(null == httpDialog) {
            httpDialog = ProgressUtil.showHttpLoading(this);
        }
        httpDialog.show();
        approveService.saveApprove(mUserId, "02", editRemark.getText().toString(), applyId, this);
    }

    @Override
    public void onSuccess(JSONObject obj) {

    }

    @Override
    public void onSuccess(JSONArray array) {
        if (null != array && array.length() > 0) {
            approves = new String[array.length()];
            try {

                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    approves[i] = obj.getString("ApproveUserName");
                    String approveState = obj.getString("ApproveStateName");
                    if (approveState.equals("批准")) {
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

        if (httpDialog.isShowing()) {
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
        if (httpDialog.isShowing()) {
            httpDialog.dismiss();
        }
    }

    @Override
    public void onApproveSucess(String content, String reason) {

        if(content.equals("OK")) {
            if (httpDialog.isShowing()) {
                httpDialog.dismiss();
            }
            //当前审批人已经审批，返回list页面
            if(approveType == 0) {
                Intent intent = new Intent();
                intent.putExtra("result", "OK");
                ApproveActivity.this.setResult(RESULT_OK, intent);
                ApproveActivity.this.finish();
            }
            else {
                Intent intent = new Intent(ApproveActivity.this, MainActivity.class);
                startActivity(intent);
                ApproveActivity.this.finish();
            }
        }
        else {
            txtApplyId.setText("【" + applyId + "】");
            txtContent.setText(content);
            txtRemark.setText(reason);

            applyService.searchApprogressList(mUserId, applyId, this);
        }
    }

    @Override
    public void onApproveFailure(int code, String msg) {
        if (httpDialog.isShowing()) {
            httpDialog.dismiss();
        }

        ToastUtil.showToast(this, msg, Toast.LENGTH_SHORT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        approveService.cancel();
        applyService.cancel();
    }
}
