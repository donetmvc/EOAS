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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eland.android.eoas.Activity.ApplyActivity;
import com.eland.android.eoas.Activity.ApplyStateActivity;
import com.eland.android.eoas.Adapt.ApplyListAdapt;
import com.eland.android.eoas.Model.ApplyListInfo;
import com.eland.android.eoas.Model.Constant;
import com.eland.android.eoas.R;
import com.eland.android.eoas.Service.ApplyListService;
import com.eland.android.eoas.Util.ProgressUtil;
import com.eland.android.eoas.Util.SharedReferenceHelper;
import com.eland.android.eoas.Util.ToastUtil;
import com.eland.android.eoas.Views.SwipeRefreshView.MaterialRefreshLayout;
import com.eland.android.eoas.Views.SwipeRefreshView.MaterialRefreshListener;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by liu.wenbin on 15/12/28.
 */
public class ApplyListFragment extends Fragment implements ApplyListService.IOnSearchApplyListListener{

    @Bind(R.id.listView)
    ListView listView;
    @Bind(R.id.refresh)
    MaterialRefreshLayout refresh;
    private Context context;
    private View rootView;

    private List<ApplyListInfo> mList;
    private ApplyListAdapt mAdapt;

    Dialog httpDialog;
    private String startDate,endDate;
    private String mUserId;

    private REFRESH_TYPE refreshType = REFRESH_TYPE.RERESH;

    public enum REFRESH_TYPE {
        RERESH, LOAD
    }

    public ApplyListFragment() {
        super();
    }

    @SuppressLint("ValidFragment")
    public ApplyListFragment(Context context) {
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 30 * 6);

        Calendar cal1 = Calendar.getInstance();
        cal1.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 30 * 6);

        startDate = simpleDateFormat.format(cal.getTime());
        endDate = simpleDateFormat.format(cal1.getTime());

        mUserId = SharedReferenceHelper.getInstance(context).getValue(Constant.LOGINID);
    }

    private void initAdapt() {
        mList = new ArrayList<>();
        initGetData();
        mAdapt = new ApplyListAdapt(context, mList) ;
        AnimationAdapter animAdapter = new ScaleInAnimationAdapter(mAdapt);
        animAdapter.setAbsListView(listView);
        animAdapter.setInitialDelayMillis(300);
        listView.setAdapter(animAdapter);
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
                TextView txtNo = (TextView) view.findViewById(R.id.txt_vacationno);
                String vacationNo = txtNo.getText().toString();

                if(!vacationNo.isEmpty()) {
                    Intent intent = new Intent(getActivity(), ApplyStateActivity.class);
                    intent.putExtra("VACATIONNO", vacationNo);
                    startActivity(intent);
                }
            }
        });
    }

    private void initGetData() {
        httpDialog = ProgressUtil.showHttpLoading(getContext());
        getData();
    }

    private void getData() {
        ApplyListService.searchApplyList(mUserId, startDate, endDate, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu_applylist,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.ab_apply) {
            Intent intent = new Intent(getActivity(), ApplyActivity.class);
            startActivityForResult(intent, 1);

            return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(null != intent) {
            String result = intent.getExtras().getString("result");//得到新Activity 关闭后返回的数据
            if(result.equals("refresh")) {
                initGetData();
            }
        }
    }

    @Override
    public void onSearchSuccess(List<ApplyListInfo> list) {
        if(null != list && list.size() > 0) {
            mList = list;
            mAdapt.setList(mList);
            mAdapt.notifyDataSetChanged();
        }
        clearLoading();
    }

    @Override
    public void onSearchFailure(int code, String msg) {
        ToastUtil.showToast(getContext(), msg, Toast.LENGTH_LONG);
        clearLoading();
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
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
