package com.eland.android.eoas.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.eland.android.eoas.Application.EOASApplication;
import com.eland.android.eoas.Fragment.DrawerFragment;
import com.eland.android.eoas.Fragment.MainFragment;
import com.eland.android.eoas.R;
import com.eland.android.eoas.Util.ToastUtil;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by liu.wenbin on 15/11/10.
 */
public class MainActivity extends AppCompatActivity {

    private String TAG = "EOAS";
    private Boolean isExit = false;
    private static final int PROFILE_SETTING = 1;

//    private static final int SCHEDULE_SEARCH = 0; //出勤查询
//    private int VACATION_APPLY = 1; //休假申请
//    private int VACATION_APPROVE = 2;   //休假批准
//    private int MEETINGROOM_APPLY = 3;  //会议室申请
//    private int TRAVEL_APPLY = 4;   //出差申请
//    private int DASH_BOARD = 5; //Dash board
//    private int BOOK_BR = 6; //图书借阅
//    private int MONEY_MANAGER = 7;  //财务管理
//    private int SHCEDULE_MANAGER = 8;   //日程管理
//    private int CONTACT_PEPOLE = 9;
//    private static final int SCHEDULE_REGISTER = 10;    //打卡

    private static final int MAIN_FRAGMENT = 10;

    private FragmentManager fragmentManager;
    private MainFragment mainFragment;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private boolean drawerStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();

        EOASApplication.getInstance().addActivity(this);

        initDrawer();
        setMainPage(MAIN_FRAGMENT);
    }

    private void initDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.content_drawer);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.left_drawer,
                        new DrawerFragment(MainActivity.this)).commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.drawer_open,
                R.string.drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                drawerStatus = false ;
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                drawerStatus = true ;
            }
        };

        mActionBarDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
        setmDrawerLayout(mDrawerLayout);
    }

    /*
    *页面跳转
    * */
    public void setMainPage(int menuType) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.setCustomAnimations(R.anim.push_up_in, R.anim.push_up_out);
        switch (menuType) {
            case MAIN_FRAGMENT:
                hideFragments(transaction);
                if (null == mainFragment) {
                    mainFragment = new MainFragment(MainActivity.this, fragmentManager);
                    transaction.add(R.id.main_content, mainFragment);
                } else {
                    transaction.show(mainFragment);
                }
                break;
        }
        transaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (mainFragment != null) {
            transaction.hide(mainFragment);
        }
    }

    public void setmDrawerLayout(DrawerLayout mDrawerLayout) {
        this.mDrawerLayout = mDrawerLayout;
    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //if(mDrawerLayout.openDrawer(Gravity.NO_GRAVITY))
            List<Fragment> fragmentList = fragmentManager.getFragments();

            int i = fragmentManager.getBackStackEntryCount();

            if(i > 0) {
                fragmentManager.popBackStack();
            }
            else {
                existAppByDoubleClick();
            }
        }

        return false;
    }

    private void existAppByDoubleClick() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            ToastUtil.showToast(this, "再次点击将退出应用程序", Toast.LENGTH_LONG);
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            EOASApplication.getInstance().existApp();
            finish();
        }
    }
}
