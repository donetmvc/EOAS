package com.eland.android.eoas.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eland.android.eoas.Adapt.ContactAdapt;
import com.eland.android.eoas.Adapt.ProxyAdapt;
import com.eland.android.eoas.Model.Constant;
import com.eland.android.eoas.Model.ProxyInfo;
import com.eland.android.eoas.Model.SaveProxyInfo;
import com.eland.android.eoas.R;
import com.eland.android.eoas.Service.ProxyService;
import com.eland.android.eoas.Util.ProgressUtil;
import com.eland.android.eoas.Util.SharedReferenceHelper;
import com.eland.android.eoas.Util.ToastUtil;
import com.eland.android.eoas.Views.SwipeRefreshView.MaterialRefreshLayout;
import com.eland.android.eoas.Views.SwipeRefreshView.MaterialRefreshListener;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;
import com.rey.material.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by liu.wenbin on 16/1/6.
 * 设置代理负责人
 */
public class SetProxyUserActivity extends AppCompatActivity implements ProxyService.IOnSearchProxyInfoListener {

    @Bind(R.id.setProxytoolbar)
    Toolbar setProxytoolbar;
    @Bind(R.id.listView)
    ListView listView;
    @Bind(R.id.refresh)
    MaterialRefreshLayout refresh;
    private String startDate, endDate;
    private String mUserId;
    private Dialog httpDialog;

    private List<ProxyInfo> mList;
    private ProxyAdapt mAdapt;
    private ProxyService proxyService;

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

        setContentView(R.layout.activity_setproxy);
        ButterKnife.bind(this);

        proxyService = new ProxyService(this);
        mUserId = SharedReferenceHelper.getInstance(this).getValue(Constant.LOGINID);
        initToolBar();
        initParams();
        initProxyList();
    }

    private void initToolBar() {
        setProxytoolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        setProxytoolbar.setTitle("设置审批人");
        setSupportActionBar(setProxytoolbar);
        setProxytoolbar.setNavigationIcon(R.mipmap.icon_back);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setProxytoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        refresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                refresh.finishRefresh();
                refresh.finishRefreshLoadMore();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout);
                refresh.finishRefresh();
                refresh.finishRefreshLoadMore();
            }
        });
    }

    private void initParams() {
        Intent intent = getIntent();

        if (null != intent) {
            startDate = intent.getStringExtra("StartDate");
            endDate = intent.getStringExtra("EndDate");
        }
    }

    private void initProxyList() {
        httpDialog = ProgressUtil.showHttpLoading(this);
        proxyService.searchProxyInfo(mUserId, startDate, endDate, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_proxy, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.ab_saveproxy) {
            saveProxy();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveProxy() {
        if(null != httpDialog && !httpDialog.isShowing()) {
            httpDialog.show();
        }
        else {
            httpDialog = ProgressUtil.showHttpLoading(this);
        }

        List<SaveProxyInfo> list = new ArrayList<>();
        SaveProxyInfo saveProxyInfo;

        for(int i = 0; i < listView.getCount(); i++) {
            saveProxyInfo = new SaveProxyInfo();
            View view = listView.getChildAt(i);
            TextView orgView = (TextView) view.findViewById(R.id.txt_orgCode);
            Spinner empSpinner = (Spinner) view.findViewById(R.id.spinner_proxy);

            saveProxyInfo.orgId = orgView.getText().toString();
            saveProxyInfo.empId = findEmpId(empSpinner.getSelectedItem().toString(), i);
            saveProxyInfo.startDate = startDate;
            saveProxyInfo.endDate = endDate;
            saveProxyInfo.userId = mUserId;

            list.add(saveProxyInfo);
        }

        proxyService.saveProxy(list, this);
    }

    private String findEmpId(String userName, int position) {
        String empId = "";

        if(null != mList && mList.size() > 0) {
            if(mList.get(position) != null && mList.get(position).proxyInfoList != null) {
                for(int j = 0; j < mList.get(position).proxyInfoList.size(); j++) {
                    if(mList.get(position).proxyInfoList.get(j).UserName.equals(userName)) {
                        empId = mList.get(position).proxyInfoList.get(j).EmpId;
                        break;
                    }
                }
            }
        }
        return empId;
    }

    @Override
    public void onSuccess(List<ProxyInfo> list) {
        if (httpDialog.isShowing()) {
            httpDialog.dismiss();
        }

        if(list != null && list.size() > 0) {
            mList = list;
        }
        else {
            mList = new ArrayList<>();
        }

        mAdapt = new ProxyAdapt(this, mList, startDate, endDate);
        AnimationAdapter animAdapter = new ScaleInAnimationAdapter(mAdapt);
        animAdapter.setAbsListView(listView);
        animAdapter.setInitialDelayMillis(300);
        listView.setAdapter(animAdapter);
    }

    @Override
    public void onFailure(int code, String msg) {
        if (httpDialog.isShowing()) {
            httpDialog.dismiss();
        }
        if(code == 1000) {
            //go back to apply activity
            Intent intent = new Intent();
            intent.putExtra("result", "OK");
            SetProxyUserActivity.this.setResult(RESULT_OK, intent);
            SetProxyUserActivity.this.finish();
        }
        else {
            ToastUtil.showToast(this, msg, Toast.LENGTH_SHORT);
        }
    }
}
