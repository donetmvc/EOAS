package com.eland.android.eoas.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eland.android.eoas.Model.Constant;
import com.eland.android.eoas.Model.NameValueInfo;
import com.eland.android.eoas.R;
import com.eland.android.eoas.Service.ApplyService;
import com.eland.android.eoas.Util.ProgressUtil;
import com.eland.android.eoas.Util.SharedReferenceHelper;
import com.eland.android.eoas.Util.ToastUtil;
import com.eland.android.eoas.Views.SegmentView;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.widget.Button;
import com.rey.material.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by liu.wenbin on 15/12/29.
 */
public class ApplyActivity extends AppCompatActivity implements ApplyService.IOnApplyListener {

    @Bind(R.id.applytoolbar)
    Toolbar applytoolbar;
    @Bind(R.id.spinner_vacationtype)
    Spinner spinnerVacationtype;
    @Bind(R.id.txt_startdate)
    TextView txtStartdate;
    @Bind(R.id.seg_starttime)
    SegmentView segStarttime;
    @Bind(R.id.txt_enddate)
    TextView txtEnddate;
    @Bind(R.id.seg_endtime)
    SegmentView segEndtime;
    @Bind(R.id.btn_apply)
    Button btnApply;
    @Bind(R.id.txt_diffDays)
    TextView txtDiffDays;
    @Bind(R.id.edit_remark)
    EditText editRemark;
    @Bind(R.id.txt_all_vacation)
    TextView txtAllVacation;
    @Bind(R.id.txt_year_vacation)
    TextView txtYearVacation;
    @Bind(R.id.txt_company_vacation)
    TextView txtCompanyVacation;
    @Bind(R.id.txt_adjust_vacation)
    TextView txtAdjustVacation;

    private String mUserId;
    private String startTime, endTime;
    private String startDate, endDate;
    private String diffDays;
    private String vacationTypeCode = "01";
    private String yearVacation,adjustVacation,companyVacation = "0.0";
    private List<NameValueInfo> mList;
    private ApplyService applyService;

    private android.app.Dialog httpDialog;

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

        setContentView(R.layout.activity_apply);
        ButterKnife.bind(this);

        applyService = new ApplyService(this);
        //初始化页面loading
        httpDialog = ProgressUtil.showHttpLoading(this);

        initView();
        initToolbar();
        initSegment();
        initVacationDays();
        //initSpinner();
    }

    private void initView() {
        mUserId = SharedReferenceHelper.getInstance(this).getValue(Constant.LOGINID);

        segStarttime.setSegmentText("08:00", 0);
        segStarttime.setSegmentText("13:00", 1);

        segEndtime.setSegmentText("13:00", 0);
        segEndtime.setSegmentText("17:00", 1);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        String nowDate = simpleDateFormat.format(date);

        setTime("08", "13");
        setDate(nowDate, nowDate);
        setDiffDays("0.5");
    }

    private void initToolbar() {
        applytoolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        applytoolbar.setTitle("新申请");
        setSupportActionBar(applytoolbar);
        applytoolbar.setNavigationIcon(R.mipmap.icon_back);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        applytoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initSegment() {
        segStarttime.setOnSegmentViewClickListener(new SegmentView.onSegmentViewClickListener() {
            @Override
            public void onSegmentViewClick(View v, int position) {
                if (position == 0) {
                    //8点
                    //startTime = "08";
                    setTime("08", endTime);
                } else {
                    //13点
                    setTime("13", endTime);
                }
                getDiffDate();
            }
        });

        segEndtime.setOnSegmentViewClickListener(new SegmentView.onSegmentViewClickListener() {
            @Override
            public void onSegmentViewClick(View v, int position) {
                if (position == 0) {
                    //13点
                    setTime(startTime, "13");
                } else {
                    //15点
                    setTime(startTime, "17");
                }
                getDiffDate();
            }
        });

    }

    private void initVacationDays() {
        applyService.searchVacationDays(mUserId, this);
    }

    private void initSpinner() {
        applyService.searchVacationType(mUserId, this);
    }

    private void getDiffDate() {
        if(httpDialog != null && !httpDialog.isShowing()) {
            httpDialog.show();
        }
        setVacationType();
        applyService.searchDiffDays(mUserId, startDate, endDate, startTime, endTime, vacationTypeCode, this);
    }

    private void setVacationType() {
        String select = spinnerVacationtype.getSelectedItem().toString();
        //find vacation code
        if(null != mList && mList.size() > 0) {
            for(NameValueInfo result: mList) {
                if(result.name.equals(select)) {
                    vacationTypeCode = result.code;
                }
            }
        }
    }

    private void setDate(String start, String end) {
        startDate = start;
        endDate = end;
        txtStartdate.setText(startDate);
        txtEnddate.setText(endDate);
    }

    private void setTime(String start, String end) {
        startTime = start;
        endTime = end;
    }

    private void setDiffDays(String diff) {
        diffDays = diff;
        txtDiffDays.setText(diffDays + "天");
    }

    @OnClick(R.id.txt_startdate)
    void setStartDate() {
        Dialog.Builder builder = new DatePickerDialog.Builder(R.style.Material_App_Dialog_DatePicker) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                DatePickerDialog dialog = (DatePickerDialog) fragment.getDialog();

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//                startDate = dialog.getFormattedDate(format);
//                txtStartdate.setText(startDate);
                setDate(dialog.getFormattedDate(format), endDate);
                getDiffDate();
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                //Toast.makeText(fragment.getDialog().getContext(), "Cancelled" , Toast.LENGTH_SHORT).show();
                super.onNegativeActionClicked(fragment);
            }
        };

        builder.positiveAction("确定")
                .negativeAction("取消");
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getSupportFragmentManager(), null);
    }

    @OnClick(R.id.txt_enddate)
    void setEndDate() {
        Dialog.Builder builder = new DatePickerDialog.Builder(R.style.Material_App_Dialog_DatePicker) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                DatePickerDialog dialog = (DatePickerDialog) fragment.getDialog();

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//                endDate = dialog.getFormattedDate(format);
//                txtEnddate.setText(endDate);
                setDate(startDate, dialog.getFormattedDate(format));
                getDiffDate();
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                //Toast.makeText(fragment.getDialog().getContext(), "Cancelled" , Toast.LENGTH_SHORT).show();
                super.onNegativeActionClicked(fragment);
            }
        };

        builder.positiveAction("确定")
                .negativeAction("取消");

        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getSupportFragmentManager(), null);
    }

    @OnClick(R.id.btn_apply) void apply() {
        boolean isGo = validateInput();
        if(isGo) {
            startApply();
        }
    }

    private boolean validateInput() {
        setVacationType();

        double all = 0.0;

        if(editRemark.getText().toString().isEmpty()) {
            ToastUtil.showToast(this, "请填写休假理由", Toast.LENGTH_LONG);
            return false;
        }

        //06 - adjust 07 - year 11 - company
        if(vacationTypeCode.equals("06")) {
            double adjust = Double.valueOf(txtAdjustVacation.getText().toString());
            if(all >= adjust) {
                ToastUtil.showToast(this, "调休假不足，请选择其他休假类型", Toast.LENGTH_LONG);
                return false;
            }
        }
        else if(vacationTypeCode.equals("07")) {
            double years = Double.valueOf(txtYearVacation.getText().toString());
            if(all >= years) {
                ToastUtil.showToast(this, "年休假不足，请选择其他休假类型", Toast.LENGTH_LONG);
                return false;
            }
        }
        else if(vacationTypeCode.equals("11")) {
            double company = Double.valueOf(txtCompanyVacation.getText().toString());
            if(all >= company) {
                ToastUtil.showToast(this, "企业年休假不足，请选择其他休假类型", Toast.LENGTH_LONG);
                return false;
            }
        }

        if(diffDays.equals("0.0")) {
            ToastUtil.showToast(this, "选择正确的休假日期", Toast.LENGTH_LONG);
            return false;
        }

        return true;
    }

    private void startApply() {

        if(null == httpDialog) {
            httpDialog = ProgressUtil.showHttpLoading(this);
        }
        else {
            httpDialog.show();
        }

        applyService.saveApply(mUserId, startDate, endDate, startTime, endTime, vacationTypeCode, diffDays,
                editRemark.getText().toString(), this);
    }

    @Override
    public void onSuccess(JSONObject obj) {
        if (null != obj) {
            try {
                yearVacation = obj.getString("YearVacation");
                adjustVacation = obj.getString("AdjustVacation");
                companyVacation = obj.getString("CompanyYearVacation");

                float all = Float.valueOf(yearVacation) +
                        Float.valueOf(adjustVacation) +
                        Float.valueOf(companyVacation);

                txtYearVacation.setText(yearVacation);
                txtCompanyVacation.setText(companyVacation);
                txtAdjustVacation.setText(adjustVacation);
                txtAllVacation.setText(String.valueOf(all));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        initSpinner();
//        if (httpDialog.isShowing()) {
//            httpDialog.dismiss();
//        }
    }

    @Override
    public void onSuccess(JSONArray array) {
        mList = new ArrayList<>();
        NameValueInfo dto = null;

        String[] vacationTypes = new String[array.length()];
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = null;
                obj = array.getJSONObject(i);
                dto = new NameValueInfo();
                dto.name = obj.getString("name");
                dto.code = obj.getString("code");
                mList.add(dto);
                vacationTypes[i] = obj.getString("name");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<>(ApplyActivity.this, R.layout.vacation_spinner_layout, vacationTypes);
        adapter.setDropDownViewResource(R.layout.vacation_spinner_dropdown);
        spinnerVacationtype.setAdapter(adapter);
        if (httpDialog.isShowing()) {
            httpDialog.dismiss();
        }
    }

    @Override
    public void onSuccess(String diffDays) {
        if(!diffDays.isEmpty()) {
            setDiffDays(diffDays.replaceAll("\"", ""));
        }
        if (httpDialog.isShowing()) {
            httpDialog.dismiss();
        }
    }

    @Override
    public void onSuccess(int type, String msg) {
        String message = msg.replaceAll("\"", "");
        String alertMessage = "";
        if (httpDialog.isShowing()) {
            httpDialog.dismiss();
        }

        if(message.isEmpty()) {
            alertMessage = "申请失败";
            ToastUtil.showToast(this, alertMessage, Toast.LENGTH_LONG);
        }
        else if(message.equals("HUM008")) {
            alertMessage = "此期间内已申请过相同的假期";
            ToastUtil.showToast(this, alertMessage, Toast.LENGTH_LONG);
        }
        else if(message.equals("HUM009")) {
            //alertMessage = "请先设置代理负责人";
            Intent intent = new Intent(ApplyActivity.this, SetProxyUserActivity.class);
            intent.putExtra("StartDate", startDate);
            intent.putExtra("EndDate", endDate);

            startActivityForResult(intent, 1);
        }
        else {
            alertMessage = "申请成功";
            ToastUtil.showToast(this, alertMessage, Toast.LENGTH_LONG);
            Intent intent = new Intent();
            //把返回数据存入Intent
            intent.putExtra("result", "refresh");
            //设置返回数据
            ApplyActivity.this.setResult(RESULT_OK, intent);
            ApplyActivity.this.finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(null != intent) {
            String result = intent.getExtras().getString("result");//得到新Activity 关闭后返回的数据
            if(result.equals("OK")) {
                startApply();
            }
        }

    }

    @Override
    public void onFailure(int type, int code, String msg) {
        ToastUtil.showToast(this, msg, Toast.LENGTH_LONG);
        if (httpDialog.isShowing()) {
            httpDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        applyService.cancel();
    }

}
