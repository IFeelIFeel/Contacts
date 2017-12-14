package com.cqnu.lengyt.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cqnu.lengyt.R;
import com.cqnu.lengyt.bean.Contact;
import com.cqnu.lengyt.utils.ToastUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

public class AddEditContactActivity extends AppCompatActivity {
    private int index;
    private String oldName;
    private String oldPhone;
    private String mUserName;

    private ImageView back;
    private TextView title;
    private ImageView complete;
    private EditText addEditName;
    private EditText addEditPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_contact);
        index = getIntent().getExtras().getInt("index");
        initView();
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.header_back);
        title = (TextView) findViewById(R.id.header_title);
        complete = (ImageView) findViewById(R.id.header_complete);
        addEditName = (EditText) findViewById(R.id.add_edit_name);
        addEditPhone = (EditText) findViewById(R.id.add_edit_phone);
        back.setVisibility(View.VISIBLE);
        complete.setVisibility(View.VISIBLE);
        if (index == 0) {
            oldName = getIntent().getExtras().getString("name");
            oldPhone = getIntent().getExtras().getString("phone");
            addEditName.setHint(oldName);
            addEditPhone.setHint(oldPhone);
            title.setText("编辑");
            complete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    List<Contact> result = DataSupport.where("name=?", addEditName.getText().toString()).find(Contact.class);
                    if (result != null && result.size() > 0) {
                        ToastUtil.showToast(getApplicationContext(), 0, "该姓名已存在！");
                    } else {
                        Contact mContact = new Contact();
                        mContact.setName(addEditName.getText().toString());
                        mContact.setPhone(addEditPhone.getText().toString());
                        mContact.updateAll("name=?", oldName);
                        ToastUtil.showToast(getApplicationContext(), 0, "修改成功！");
                        finish();
                    }
                }
            });
        } else {
            mUserName = getIntent().getExtras().getString("userName");
            addEditName.setHint("请输入姓名");
            addEditPhone.setHint("请输入电话");
            title.setText("添加");
            complete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<Contact> result = DataSupport.where("name=?", addEditName.getText().toString()).find(Contact.class);
                    if (result != null && result.size() > 0) {
                        ToastUtil.showToast(getApplicationContext(), 0, "该姓名已存在！");
                    } else {
                        Contact mContact = new Contact();
                        mContact.setName(addEditName.getText().toString());
                        mContact.setPhone(addEditPhone.getText().toString());
                        mContact.setUser(mUserName);
                        mContact.save();
                        ToastUtil.showToast(getApplicationContext(), 0, "添加成功！");
                        finish();
                    }
                }
            });

        }
    }
}
