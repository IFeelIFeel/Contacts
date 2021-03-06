package com.cqnu.lengyt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.cqnu.lengyt.R;
import com.cqnu.lengyt.bean.Contact;
import com.cqnu.lengyt.bean.User;
import com.cqnu.lengyt.utils.TimeUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

public class SplashActivity extends AppCompatActivity implements Runnable {
    final Handler mHandler = new Handler();
    String startTime;
    long diff;
    List<User> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        result = DataSupport.where("isLogin=?", "1").find(User.class);
        mHandler.postDelayed(this, 2000);


    }

    @Override
    public void run() {
        if (result.size() == 0) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            startTime = result.get(0).getStartTime();
            diff = TimeUtil.getTimeDifference(startTime, TimeUtil.getNowTime());
            //判断时间差
            if (diff > 10) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("userName", result.get(0).getUser());
                startActivity(intent);
                finish();
            }
        }


    }


}
