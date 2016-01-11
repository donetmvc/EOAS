package com.eland.android.eoas.Activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eland.android.eoas.Adapt.ContactAdapt;
import com.eland.android.eoas.Application.EOASApplication;
import com.eland.android.eoas.Model.Constant;
import com.eland.android.eoas.Model.LoginInfo;
import com.eland.android.eoas.R;
import com.eland.android.eoas.Service.ContactService;
import com.eland.android.eoas.Util.CacheInfoUtil;
import com.eland.android.eoas.Util.ProgressUtil;
import com.eland.android.eoas.Util.SharedReferenceHelper;
import com.eland.android.eoas.Util.ToastUtil;
import com.eland.android.eoas.Views.SwipeRefreshView.MaterialRefreshLayout;
import com.eland.android.eoas.Views.SwipeRefreshView.MaterialRefreshListener;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.rey.material.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by liu.wenbin on 15/12/16.
 */
public class SearchActivity extends AppCompatActivity implements ContactService.IOnSearchContactListener {

    @Bind(R.id.searchtoolbar)
    Toolbar searchtoolbar;
    EditText editText;

    List<LoginInfo> mList;
    @Bind(R.id.txt_tips)
    TextView txtTips;
    @Bind(R.id.listView)
    ListView listView;
    @Bind(R.id.refresh)
    MaterialRefreshLayout refresh;
    private ImageLoader imageLoader;

    ContactAdapt mAdapt;
    ContactService contactService;

    private String searchKey;
    private Dialog httpDialog;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        EOASApplication.getInstance().addActivity(this);
        ButterKnife.bind(this);

        contactService = new ContactService();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        initToolbar();
        editText = (EditText) searchtoolbar.findViewById(R.id.edit_searchKey);
        userId = SharedReferenceHelper.getInstance(this).getValue(Constant.LOGINID);

        initRefreshListener();
    }

    private void initRefreshListener() {
        refresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                refresh.finishRefresh();
            }
        });
    }

    private void initToolbar() {
        searchtoolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        searchtoolbar.setTitle("");
        setSupportActionBar(searchtoolbar);
        searchtoolbar.setNavigationIcon(R.mipmap.icon_back);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.ab_search) {
            startSearch();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startSearch() {
        searchKey = editText.getText().toString();

        if (searchKey.isEmpty()) {
            ToastUtil.showToast(this, "请输入您想查询的人员信息", Toast.LENGTH_SHORT);
            txtTips.setVisibility(TextView.VISIBLE);
            return;
        }

        //hide input keyboard
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (im.isActive()) {
            im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        httpDialog = ProgressUtil.showHttpLoading(this);

        txtTips.setVisibility(TextView.GONE);

        mList = CacheInfoUtil.loadContact(this);

        if (null != mList && mList.size() > 0) {
            startFindLikeData(mList);
        } else {
            getData();
        }
    }

    private void getData() {
        contactService.searchContact(userId, this);
    }

    private void clearLoading() {
        if(null != refresh) {
            refresh.finishRefresh();
            refresh.finishRefreshLoadMore();
        }
        if(httpDialog != null && httpDialog.isShowing()) {
            httpDialog.dismiss();
        }
    }

    @Override
    public void onSearchSuccess(ArrayList<LoginInfo> list) {
        if(null != list && list.size() > 0) {
            startFindLikeData(list);
        }
        else {
            clearLoading();
            ToastUtil.showToast(this, "没有搜索到匹配的人员信息", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onSearchFailure(int code, String msg) {
        if (httpDialog.isShowing()) {
            httpDialog.dismiss();
        }
        ToastUtil.showToast(this, "没有搜索到匹配的人员信息", Toast.LENGTH_SHORT);
    }

    private void startFindLikeData(List<LoginInfo> list) {
        List<LoginInfo> returnList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).userName.contains(searchKey)
                    || list.get(i).userId.contains(searchKey)
                    || list.get(i).cellNo.contains(searchKey)) {
                returnList.add(list.get(i));
                continue;
            }
        }

        initAdapt(returnList);
    }

    private void initAdapt(List<LoginInfo> list) {
        if (null != list && list.size() > 0) {
            mAdapt = new ContactAdapt(this, list, imageLoader);
            AnimationAdapter animAdapter = new ScaleInAnimationAdapter(mAdapt);
            animAdapter.setAbsListView(listView);
            animAdapter.setInitialDelayMillis(300);
            listView.setAdapter(animAdapter);
        } else {
            ToastUtil.showToast(this, "没有搜索到匹配的人员信息", Toast.LENGTH_SHORT);
        }
        clearLoading();
    }
}
