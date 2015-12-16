package com.eland.android.eoas.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.eland.android.eoas.Adapt.ContactAdapt;
import com.eland.android.eoas.Model.LoginInfo;
import com.eland.android.eoas.R;
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
 * Created by elandmac on 15/12/14.
 */
public class ContactFragment extends Fragment {

    @Bind(R.id.listView)
    ListView listView;
    @Bind(R.id.refresh)
    MaterialRefreshLayout refresh;
    private Context context;
    private View rootView;
    List<LoginInfo> mLsit;
    private ContactAdapt mAdapt;
    private ImageLoader imageLoader;

    public ContactFragment() {
    }

    @SuppressLint("ValidFragment")
    public ContactFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.contact_fragment, null);
        ButterKnife.bind(this, rootView);

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));

        initListener();

        initAdapter();

        return rootView;
    }

    private void initListener() {
        refresh.setMaterialRefreshListener(new MaterialRefreshListener() {

            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {

            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout);
            }

            @Override
            public void onfinish() {
                super.onfinish();
            }

        });
    }

    private void initAdapter() {
        mLsit = new ArrayList<>();
        LoginInfo dto = null;

        for(int i = 0; i < 100; i++) {
            dto = new LoginInfo();
            dto.userName = "系统管理员" + i;
            dto.email = "sysadmin@eland.co.kr";
            dto.cellNo = "15110188722";
            dto.url = "http://182.92.65.253:30001/Eland.EOAS/images/liuwenbin.jpg";
            mLsit.add(dto);
        }

        mAdapt = new ContactAdapt(context, mLsit, imageLoader) ;
        AnimationAdapter animAdapter = new ScaleInAnimationAdapter(mAdapt);
        animAdapter.setAbsListView(listView);
        animAdapter.setInitialDelayMillis(300);
        listView.setAdapter(animAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
