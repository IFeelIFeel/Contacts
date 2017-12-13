package com.cqnu.lengyt.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cqnu.lengyt.R;
import com.cqnu.lengyt.bean.Contact;
import com.cqnu.lengyt.bean.User;
import com.cqnu.lengyt.utils.ToastUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

public class ContactDetailActivity extends AppCompatActivity {
    private ImageView back;
    private TextView contactName;
    private TextView contactPhone;
    private Button editContact;
    private Button deleteContact;
    private Button callPhone;
    private Button sendMessage;
    private String name;
    private String phone;
    private int position;
    private List<Contact> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
        name = getIntent().getExtras().getString("name");
        phone = getIntent().getExtras().getString("phone");
        position = getIntent().getExtras().getInt("position");
        contactList = DataSupport.findAll(Contact.class);
        initView();

    }

    private void initView() {
        back = (ImageView) findViewById(R.id.header_back);
        contactName = (TextView) findViewById(R.id.detail_name);
        contactPhone = (TextView) findViewById(R.id.detail_phone);
        editContact = (Button) findViewById(R.id.detail_edit);
        deleteContact = (Button) findViewById(R.id.detail_delete);
        callPhone = (Button) findViewById(R.id.detail_call);
        sendMessage = (Button) findViewById(R.id.detail_message);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        contactName.setText(name);
        contactPhone.setText(phone);
        callPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 检查是否获得了权限（Android6.0运行时权限）
                if (ContextCompat.checkSelfPermission(ContactDetailActivity.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // 没有获得授权，申请授权
                    if (ActivityCompat.shouldShowRequestPermissionRationale(ContactDetailActivity.this,
                            Manifest.permission.CALL_PHONE)) {
                        // 返回值：
//                          如果app之前请求过该权限,被用户拒绝, 这个方法就会返回true.
//                          如果用户之前拒绝权限的时候勾选了对话框中”Don’t ask again”的选项,那么这个方法会返回false.
//                          如果设备策略禁止应用拥有这条权限, 这个方法也返回false.
                        // 弹窗需要解释为何需要该权限，再次请求授权
                        Toast.makeText(ContactDetailActivity.this, "请授权！", Toast.LENGTH_LONG).show();

                        // 帮跳转到该应用的设置界面，让用户手动授权
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    } else {
                        // 不需要解释为何需要该权限，直接请求授权
                        ActivityCompat.requestPermissions(ContactDetailActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE}, 1);
                    }
                } else {
                    // 已经获得授权，可以打电话
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                    startActivity(intent);
                }
            }
        });
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri smsToUri = Uri.parse("smsto:" + phone);
                Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
                startActivity(intent);
            }
        });
        editContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Contact> t = DataSupport.findAll(Contact.class);
                contactList.remove(contactList.get(position));
                DataSupport.deleteAll(Contact.class);
                DataSupport.saveAll(contactList);
                t = DataSupport.findAll(Contact.class);
                finish();
                ToastUtil.showToast(getApplicationContext(), 0, "删除成功！");
            }
        });
    }


}
