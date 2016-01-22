package com.eland.android.eoas.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.eland.android.eoas.Activity.SearchActivity;
import com.eland.android.eoas.Adapt.ContactAdapt;
import com.eland.android.eoas.Model.Constant;
import com.eland.android.eoas.Model.LoginInfo;
import com.eland.android.eoas.R;
import com.eland.android.eoas.Service.ContactService;
import com.eland.android.eoas.Util.CacheInfoUtil;
import com.eland.android.eoas.Util.ConsoleUtil;
import com.eland.android.eoas.Util.SharedReferenceHelper;
import com.eland.android.eoas.Util.ToastUtil;
import com.eland.android.eoas.Views.SwipeRefreshView.MaterialRefreshLayout;
import com.eland.android.eoas.Views.SwipeRefreshView.MaterialRefreshListener;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by liu.wenbin on 15/12/14.
 */
public class ContactFragment extends Fragment implements ContactService.IOnSearchContactListener{

    @Bind(R.id.listView)
    ListView listView;
    @Bind(R.id.refresh)
    MaterialRefreshLayout refresh;
    private Context context;
    private View rootView;
    List<LoginInfo> mLsit;
    private ContactAdapt mAdapt;
    private ImageLoader imageLoader;
    String userId;
    Toolbar toolbar;
    private String TAG = "EOAS";

    private ContactService contactService;
    private REFRESH_TYPE refreshType = REFRESH_TYPE.RERESH;

    public enum REFRESH_TYPE {
        RERESH, LOAD
    }

    public ContactFragment() {
        super();
    }

    public static ContactFragment newInstance(Bundle args) {
        ContactFragment f = new ContactFragment();
        f.setArguments(args);
        return f;
    }

    @SuppressLint("ValidFragment")
    public ContactFragment(Context context) {
        //this.context = context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        ConsoleUtil.i(TAG, "=====I am back=========");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ConsoleUtil.i(TAG, "=====I am saved=========");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.contact_fragment, null);
        ButterKnife.bind(this, rootView);

        this.context = getContext();

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
        contactService = new ContactService(context);
        userId = SharedReferenceHelper.getInstance(context).getValue(Constant.LOGINID);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        initListener();

        initAdapter();

        return rootView;
    }

    private void initListener() {
        refresh.setMaterialRefreshListener(new MaterialRefreshListener() {

            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                refreshType = REFRESH_TYPE.RERESH;
                refresh.finishRefreshLoadMore();

                getData();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout);
                refreshType = REFRESH_TYPE.LOAD;
                refresh.finishRefresh();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if(null != refresh) {
                            refresh.finishRefreshLoadMore();
                        }
                    }
                }, 3000);
            }

            @Override
            public void onfinish() {
                super.onfinish();
            }

        });
    }

    private void initAdapter() {
        mLsit = new ArrayList<>();

        if(CacheInfoUtil.loadContact(context).size() > 0) {
            mLsit = CacheInfoUtil.loadContact(context);
        }
        else {
            getData();
        }

        mAdapt = new ContactAdapt(context, mLsit, imageLoader) ;
        AnimationAdapter animAdapter = new ScaleInAnimationAdapter(mAdapt);
        animAdapter.setAbsListView(listView);
        animAdapter.setInitialDelayMillis(300);
        listView.setAdapter(animAdapter);
    }

    private void getData() {
        contactService.searchContact(userId, this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        contactService.cancel();
        ButterKnife.unbind(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.ab_search) {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);

            return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearLoading() {
        if(null != refresh) {
            refresh.finishRefresh();
            refresh.finishRefreshLoadMore();
        }
    }

    @Override
    public void onSearchSuccess(ArrayList<LoginInfo> list) {
        clearLoading();
        if(null != listView) {
            if(list != null && list.get(list.size() - 1) != null) {
                //把数据加入缓存
                CacheInfoUtil.saveContact(context, list);

                mLsit.clear();
                mLsit.addAll(list);
                mAdapt.setList(mLsit);
                mAdapt.notifyDataSetChanged();
            }
            else {
                ToastUtil.showToast(context, "没有可查询的数据", Toast.LENGTH_LONG);
            }
        }
    }

    @Override
    public void onSearchFailure(int code, String msg) {
        clearLoading();
        ToastUtil.showToast(context, msg, Toast.LENGTH_LONG);
    }
}
