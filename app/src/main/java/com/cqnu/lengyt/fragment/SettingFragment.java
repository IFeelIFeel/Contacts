package com.cqnu.lengyt.fragment;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cqnu.lengyt.R;
import com.cqnu.lengyt.activity.AddEditContactActivity;
import com.cqnu.lengyt.activity.LoginActivity;
import com.cqnu.lengyt.bean.Contact;
import com.cqnu.lengyt.bean.User;
import com.cqnu.lengyt.utils.ToastUtil;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {
    private TextView userName;
    private Button addContact;
    private Button importContact;
    private Button exportContact;
    private Button loginOut;


    private static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Photo.PHOTO_ID, ContactsContract.CommonDataKinds.Phone.CONTACT_ID};
    private List<Contact> contactList;
    private String mUserName;

    public SettingFragment(String userName) {
        // Required empty public constructor
        mUserName = userName;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        contactList = DataSupport.findAll(Contact.class);
        userName = (TextView) view.findViewById(R.id.setting_user_name);
        addContact = (Button) view.findViewById(R.id.setting_add_contact);
        importContact = (Button) view.findViewById(R.id.setting_import_contact);
        exportContact = (Button) view.findViewById(R.id.setting_export_contact);
        loginOut = (Button) view.findViewById(R.id.setting_login_out);
        userName.setText(mUserName);
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddEditContactActivity.class);
                intent.putExtra("index", 1);
                intent.putExtra("userName", mUserName);
                startActivity(intent);
            }
        });
        importContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactList = getSystemContactInfos();
                DataSupport.saveAll(contactList);
                if (DataSupport.findAll(Contact.class).size() >= 0) {
                    ToastUtil.showToast(getContext(), 0, "导入成功！");
                } else {
                    ToastUtil.showToast(getContext(), 0, "导入失败！");
                }

            }
        });
        exportContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    exportAsVcf(contactList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        loginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                User mUser = new User();
                mUser.setToDefault("isLogin");
                mUser.updateAll("user=?", mUserName);
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });
    }


    /**
     * 获取系统联系人信息
     *
     * @return
     */

    public List<Contact> getSystemContactInfos() {

        List<Contact> infos = new ArrayList<Contact>();
        Cursor cursor = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
        if (cursor != null) {

            while (cursor.moveToNext()) {

                Contact info = new Contact();
                String contactName = cursor.getString(0);
                String phoneNumber = cursor.getString(1);
                info.setName(contactName);
                info.setPhone(phoneNumber);
                info.setUser(mUserName);
                infos.add(info);
                info = null;
            }
            cursor.close();

        }
        return infos;
    }


    /**
     * 导出联系人到VCF文件中
     */
    public void exportAsVcf(List<Contact> contacts) throws IOException {
        //导出到指定文件夹。
        if (contacts.size() == 0) {
            ToastUtil.showToast(getContext(), 0, "当前没有联系人！");
        } else {
            try {
                //String path = Environment.getExternalStorageDirectory() + "/contacts/VCFContacts.vcf";
                File file = new File("/mnt/sdcard/contact.vcf");
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                        String path = "/mnt/sdcard/contact.vcf";
                        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path), "UTF-8");
                        //将数据写入到文件中。
                        for (Contact contact : contacts) {
                            writer.write("BEGIN:VCARD\r\n");
                            writer.write("VERSION:3.0\r\n");
                            if (contact.getName() != null) {
                                writer.write("N:;" + contact.getName() + ";;;\r\n");
                            }
                            if (contact.getPhone() != null) {
                                writer.write("TEL;CELL:" + contact.getPhone() + "\r\n");
                            }
                            writer.write("END:VCARD");
                            if (contacts.indexOf(contact) != (contacts.size() - 1)) {
                                writer.write("\r\n");
                            }
                        }
                        writer.flush();
                        writer.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    String path = "/mnt/sdcard/contact.vcf";
                    OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path), "UTF-8");
                    //将数据写入到文件中。
                    for (Contact contact : contacts) {
                        writer.write("BEGIN:VCARD\r\n");
                        writer.write("VERSION:3.0\r\n");
                        if (contact.getName() != null) {
                            writer.write("N:;" + contact.getName() + ";;;\r\n");
                        }
                        if (contact.getPhone() != null) {
                            writer.write("TEL;CELL:" + contact.getPhone() + "\r\n");
                        }
                        writer.write("END:VCARD");
                        if (contacts.indexOf(contact) != (contacts.size() - 1)) {
                            writer.write("\r\n");
                        }
                    }
                    writer.flush();
                    writer.close();
                    ToastUtil.showToast(getContext(), 0, "导出成功！");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        }
    }


}
