package com.cqnu.lengyt.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.cqnu.lengyt.R;
import com.cqnu.lengyt.bean.User;
import com.cqnu.lengyt.fragment.MainFragment;
import com.cqnu.lengyt.fragment.SettingFragment;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import org.litepal.crud.DataSupport;

public class MainActivity extends AppCompatActivity {


    private FragmentManager fragmentManager;
    private BottomBar mBottomBar;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getFragmentManager();
        userName = getIntent().getExtras().getString("userName");
        initView();
    }

    private void initView() {
        mBottomBar = (BottomBar) findViewById(R.id.bottom_bar);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                Object ob = null;
                switch (tabId) {
                    case R.id.tab1:
                        ob = new MainFragment(userName);
                        break;
                    case R.id.tab2:
                        ob = new SettingFragment(userName);
                        break;

                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, (Fragment) ob)
                        .commit();
            }
        });
    }

}
