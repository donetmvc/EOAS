package com.eland.android.eoas.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.eland.android.eoas.Adapt.ScheduleAdapt;
import com.eland.android.eoas.Model.Constant;
import com.eland.android.eoas.Model.ScheduleInfo;
import com.eland.android.eoas.R;
import com.eland.android.eoas.Service.SearchScheduleService;
import com.eland.android.eoas.Util.ProgressUtil;
import com.eland.android.eoas.Util.SharedReferenceHelper;
import com.eland.android.eoas.Util.ToastUtil;
import com.eland.android.eoas.Views.SwipeRefreshView.MaterialRefreshLayout;
import com.eland.android.eoas.Views.SwipeRefreshView.MaterialRefreshListener;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liu.wenbin on 15/12/24.
 */
public class SearchScheduleFragment extends Fragment implements SearchScheduleService.IOnSearchScheduleListener{

    @Bind(R.id.edit_startdate)
    EditText editStartdate;
    @Bind(R.id.edit_enddate)
    EditText editEnddate;
    @Bind(R.id.img_search)
    ImageView imgSearch;
    @Bind(R.id.listView)
    ListView listView;
    @Bind(R.id.refresh)
    MaterialRefreshLayout refresh;
    private Context context;
    private View rootView;
    private REFRESHTYEP refreshType = REFRESHTYEP.NONE;
    private String startDate, endDate;
    private ScheduleAdapt mAdapt;
    private List<ScheduleInfo> mList;
    private String mUserId;
    public enum REFRESHTYEP {
        REFRESH,LOAD_MORE,NONE
    }
    private android.app.Dialog httpDialog;

    public SearchScheduleFragment() {
    }

    @SuppressLint("ValidFragment")
    public SearchScheduleFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.searchschedule_fragment, null);
        ButterKnife.bind(this, rootView);

        initView();
        initListener();
        initAdapter();

        return rootView;
    }

    private void initView() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        String nowDate = simpleDateFormat.format(date);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 30);

        startDate = simpleDateFormat.format(cal.getTime());
        endDate = nowDate;
        editStartdate.setText(startDate);
        editEnddate.setText(endDate);

        mUserId = SharedReferenceHelper.getInstance(context).getValue(Constant.LOGINID);
    }

    private void initAdapter() {
        mList = new ArrayList<>();

        mAdapt = new ScheduleAdapt(context, mList) ;
        AnimationAdapter animAdapter = new ScaleInAnimationAdapter(mAdapt);
        animAdapter.setAbsListView(listView);
        animAdapter.setInitialDelayMillis(300);
        listView.setAdapter(animAdapter);
    }

    private void initListener() {
        refresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onfinish() {
                super.onfinish();
            }

            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                refresh.finishRefreshLoadMore();
                refreshType = REFRESHTYEP.REFRESH;

                mAdapt.notifyDataSetChanged();
                getData();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout);

                refresh.finishRefresh();
                refreshType = REFRESHTYEP.REFRESH;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh.finishRefreshLoadMore();
                    }
                }, 3000);

                getData();
            }
        });
    }

    @OnClick(R.id.edit_startdate) void setStartDate() {
        Dialog.Builder builder = new DatePickerDialog.Builder(R.style.Material_App_Dialog_DatePicker){
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                DatePickerDialog dialog = (DatePickerDialog)fragment.getDialog();

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                startDate = dialog.getFormattedDate(format);
                editStartdate.setText(startDate);
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
        fragment.show(getFragmentManager(), null);
    }

    @OnClick(R.id.edit_enddate) void setEndDate() {
        Dialog.Builder builder = new DatePickerDialog.Builder(R.style.Material_App_Dialog_DatePicker){
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                DatePickerDialog dialog = (DatePickerDialog)fragment.getDialog();

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                endDate = dialog.getFormattedDate(format);
                editEnddate.setText(endDate);
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
        fragment.show(getFragmentManager(), null);
    }

    @OnClick(R.id.img_search) void searchSchedule() {
        httpDialog = ProgressUtil.showHttpLoading(getContext());
        getData();
    }

    private void getData() {
        SearchScheduleService.searchSchedule(mUserId, startDate, endDate, this);
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
    public void onSuccess(List<ScheduleInfo> list) {
        clearLoading();
        if(null != list && list.size() > 0) {
            mList = list;
            mAdapt.setList(mList);
            mAdapt.notifyDataSetChanged();
        }
        else {
            ToastUtil.showToast(context, "没有可查询的数据", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onFailure(int code, String msg) {
        clearLoading();
        ToastUtil.showToast(context, msg, Toast.LENGTH_SHORT);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
