package com.eland.android.eoas.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eland.android.eoas.Activity.ApproveActivity;
import com.eland.android.eoas.Adapt.ApproveListAdapt;
import com.eland.android.eoas.Model.ApproveListInfo;
import com.eland.android.eoas.Model.Constant;
import com.eland.android.eoas.R;
import com.eland.android.eoas.Receiver.DeskCountChangeReceiver;
import com.eland.android.eoas.Service.ApproveListService;
import com.eland.android.eoas.Util.ProgressUtil;
import com.eland.android.eoas.Util.SharedReferenceHelper;
import com.eland.android.eoas.Util.ToastUtil;
import com.eland.android.eoas.Views.SwipeRefreshView.MaterialRefreshLayout;
import com.eland.android.eoas.Views.SwipeRefreshView.MaterialRefreshListener;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by liu.wenbin on 16/1/7.
 */
@SuppressLint("ValidFragment")
public class ApproveListFragment extends Fragment implements ApproveListService.IOnApproveListListener{

    @Bind(R.id.listView)
    ListView listView;
    @Bind(R.id.refresh)
    MaterialRefreshLayout refresh;
    private Context context;
    private View rootView;
    private String mUserId;

    private ApproveListAdapt mAdapter;
    private List<ApproveListInfo> mList;
    private Dialog httpDialog;

    public ApproveListFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.contact_fragment, null);
        ButterKnife.bind(this, rootView);

        initView();
        initAdapt();
        initListener();

        return rootView;
    }

    private void initView() {
        mUserId = SharedReferenceHelper.getInstance(getContext()).getValue(Constant.LOGINID);
    }

    private void initAdapt() {
        initData();
    }

    private void initListener() {
        refresh.setMaterialRefreshListener(new MaterialRefreshListener() {

            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                refresh.finishRefreshLoadMore();
                getData();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout);
                refresh.finishRefresh();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        refresh.finishRefreshLoadMore();
                    }
                }, 3000);
            }

            @Override
            public void onfinish() {
                super.onfinish();
            }

        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView txtNo = (TextView) view.findViewById(R.id.txt_applyId);
                String vacationNo = txtNo.getText().toString();

                if(!vacationNo.isEmpty()) {
                    Intent intent = new Intent(getActivity(), ApproveActivity.class);
                    intent.putExtra("VACATIONNO", vacationNo);
                    startActivityForResult(intent, 1);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(null != intent) {
            String result = intent.getStringExtra("result");
            if(result.equals("OK")) {
                if(null != httpDialog && !httpDialog.isShowing()) {
                    httpDialog.show();
                }
                getData();
            }
        }
    }

    private void initData() {
        httpDialog = ProgressUtil.showHttpLoading(getContext());
        getData();
    }

    private void getData() {
        ApproveListService.searchApproveList(mUserId, this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onSuccess(List<ApproveListInfo> list) {
        if(httpDialog.isShowing()) {
            httpDialog.dismiss();
        }
        refresh.finishRefresh();

        if(null != list && list.size() > 0) {
            mList = list;

        }
        else {
            mList = new ArrayList<>();
        }

        mAdapter = new ApproveListAdapt(context, mList) ;
        AnimationAdapter animAdapter = new ScaleInAnimationAdapter(mAdapter);
        animAdapter.setAbsListView(listView);
        animAdapter.setInitialDelayMillis(300);
        listView.setAdapter(animAdapter);


        Intent intent = new Intent(getActivity(), DeskCountChangeReceiver.class);
        intent.setAction("EOAS_COUNT_CHANGED");
        intent.putExtra("COUNT", String.valueOf(list.size() == 0 ? 0 : list.size()));
        context.sendBroadcast(intent);
    }

    @Override
    public void onFailure(int code, String msg) {
        if(httpDialog.isShowing()) {
            httpDialog.dismiss();
        }
        refresh.finishRefresh();
        ToastUtil.showToast(context, msg, Toast.LENGTH_LONG);
    }
}
