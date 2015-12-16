package com.eland.android.eoas.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eland.android.eoas.R;
import com.eland.android.eoas.Util.ConsoleUtil;
import com.eland.android.eoas.Util.ToastUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liu.wenbin on 15/11/27.
 */
public class MainFragment extends Fragment {

    @Bind(R.id.img_registSchedule)
    LinearLayout imgRegistSchedule;
    private Context context;
    private View rootView;
    private FragmentManager fragmentManager;
    private RegistScheduleFragment registScheduleFragment;
    private ContactFragment contactFragment;
    private static final int SCHEDULE_REGISTER = 10;    //打卡
    private String TAG = "EOAS";

    public MainFragment() {
    }

    @SuppressLint("ValidFragment")
    public MainFragment(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.main_fragment, null);

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        ConsoleUtil.i(TAG, "-------Fragment onStart---------------");
    }

    @Override
    public void onResume() {
        super.onResume();
        ConsoleUtil.i(TAG, "-------Fragment onResume---------------");
    }

    @Override
    public void onPause() {
        super.onPause();
        ConsoleUtil.i(TAG, "-------Fragment onPause---------------");
    }

    @Override
    public void onStop() {
        super.onStop();
        ConsoleUtil.i(TAG, "-------Fragment onStop---------------");
    }

    @OnClick(R.id.img_registSchedule) void goRegistSchedule() {
        //ToastUtil.showToast(context, "点击了", Toast.LENGTH_LONG);
        registScheduleFragment = new RegistScheduleFragment(context);
        Fragment mainFragment = fragmentManager.getFragments().get(1);
        goView(mainFragment, registScheduleFragment);
    }

    @OnClick(R.id.img_contact) void goContact() {
        contactFragment = new ContactFragment(context);
        Fragment mainFragment = fragmentManager.getFragments().get(1);
        goView(mainFragment, contactFragment);
    }

    private void goView(Fragment from, Fragment to) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.setCustomAnimations(R.anim.push_up_in, R.anim.push_up_out);
        //hideFragments(transaction);
        if (from.isAdded()) {
            transaction.hide(from).add(R.id.main_content, to).addToBackStack(null).commit();
            //transaction.add(R.id.main_content, registScheduleFragment);
        } else {
            transaction.hide(from).show(to).commit();
            //transaction.show(registScheduleFragment);
        }
//        transaction.addToBackStack(null);
//        transaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {
        //if (registScheduleFragment != null) {
        transaction.hide(this);
        //}
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}