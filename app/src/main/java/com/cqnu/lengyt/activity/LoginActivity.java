package com.cqnu.lengyt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cqnu.lengyt.R;
import com.cqnu.lengyt.bean.User;
import com.cqnu.lengyt.utils.ToastUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private EditText userName;
    private EditText pwd;
    private Button login;
    private TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        userName = (EditText) findViewById(R.id.user_name);
        pwd = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        register = (TextView) findViewById(R.id.register);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<User> resultList = DataSupport.where("user=?", userName.getText().toString()).find(User.class);
                if (resultList != null && resultList.size() > 0) {
                    User result = resultList.get(0);
                    if (TextUtils.equals(result.getPassword(), pwd.getText().toString())) {
                        result.setLogin(true);
                        result.updateAll("user=?", userName.getText().toString());
                        ToastUtil.showToast(getApplicationContext(), 0, "登陆成功！");
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        i.putExtra("userName", userName.getText().toString());
                        startActivity(i);
                        finish();
                    } else {
                        ToastUtil.showToast(getApplicationContext(), 0, "密码错误！");
                    }
                } else {
                    ToastUtil.showToast(getApplicationContext(), 0, "用户名错误！");
                }

            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });
    }


}
