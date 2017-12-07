package com.cqnu.lengyt.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cqnu.lengyt.R;
import com.cqnu.lengyt.bean.User;
import com.cqnu.lengyt.utils.TimeUtil;
import com.cqnu.lengyt.utils.ToastUtil;

import org.litepal.crud.DataSupport;

public class RegisterActivity extends AppCompatActivity {
    private EditText rUserName;
    private EditText rPassword;
    private Button register;

    private String userName;
    private String password;
    private String startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        rUserName = (EditText) findViewById(R.id.r_user_name);
        rPassword = (EditText) findViewById(R.id.r_password);
        register = (Button) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = rUserName.getText().toString();
                password = rPassword.getText().toString();
                startTime = TimeUtil.getNowTime();
                if (userName == null || password == null) {
                    ToastUtil.showToast(getApplicationContext(), 0, "输入为空！");
                } else {
                    if (DataSupport.where("user=?", userName).find(User.class).size() == 0) {
                        User mUser = new User();
                        mUser.setUser(userName);
                        mUser.setPassword(password);
                        mUser.setStartTime(startTime);
                        mUser.save();
                        ToastUtil.showToast(getApplicationContext(), 0, "注册成功！");
                        finish();
                    } else {
                        ToastUtil.showToast(getApplicationContext(), 0, "用户名已存在！");
                    }
                }
            }
        });
    }
}
