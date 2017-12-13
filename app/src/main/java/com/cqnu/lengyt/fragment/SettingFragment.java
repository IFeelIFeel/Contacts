package com.cqnu.lengyt.fragment;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cqnu.lengyt.R;
import com.cqnu.lengyt.activity.AddEditContactActivity;
import com.cqnu.lengyt.activity.LoginActivity;
import com.cqnu.lengyt.bean.Contact;
import com.cqnu.lengyt.utils.CharacterParser;
import com.cqnu.lengyt.utils.ToastUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
                startActivity(intent);
            }
        });
        importContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactList = getSystemContactInfos();
                for (int i = 0; i < contactList.size(); i++) {
                    String name = contactList.get(i).getName();
                    if (!TextUtils.isEmpty(name) && name.length() > 0) {
                        String pinyin = CharacterParser.getInstance().getPinYinSpelling(name.substring(0, 1));
                        contactList.get(i).setFirstLetter((char) (pinyin.charAt(0) - 32));
                    }

                }
                Collections.sort(contactList, comparator);
                DataSupport.saveAll(contactList);
                if (DataSupport.findAll(Contact.class).size() >= 0) {
                    ToastUtil.showToast(getContext(), 0, "导入成功！");
                } else {
                    ToastUtil.showToast(getContext(), 0, "导入失败！");
                }

            }
        });
        loginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    private Comparator<Contact> comparator = new Comparator<Contact>() {

        @Override
        public int compare(Contact arg0, Contact arg1) {
            //获取联系人姓名对应的拼音，通过比较拼音来确定联系人的先后次序
            String pinyin0 = CharacterParser.getInstance().getPinYinSpelling(arg0.getName());
            String pinyin1 = CharacterParser.getInstance().getPinYinSpelling(arg1.getName());
            return pinyin0.compareTo(pinyin1);
        }
    };

}
