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
import com.cqnu.lengyt.fragment.MainFragment;
import com.cqnu.lengyt.fragment.SettingFragment;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class MainActivity extends AppCompatActivity {


    private FragmentManager fragmentManager;
    private FrameLayout container;
    private BottomBar mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getFragmentManager();
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
                        ob = new MainFragment();
                        break;
                    case R.id.tab2:
                        ob = new SettingFragment();
                        break;

                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, (Fragment) ob)
                        .commit();
            }
        });
    }

    //获取系统联系人
    private String[] getContacts() {
        //联系人的Uri，也就是content://com.android.contacts/contacts
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        //指定获取_id和display_name两列数据，display_name即为姓名
        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
        };
        //根据Uri查询相应的ContentProvider，cursor为获取到的数据集
        Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);
        String[] arr = new String[cursor.getCount()];
        int i = 0;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Long id = cursor.getLong(0);
                //获取姓名
                String name = cursor.getString(1);
                //指定获取NUMBER这一列数据
                String[] phoneProjection = new String[]{
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                };
                arr[i] = id + " , 姓名：" + name;

                //根据联系人的ID获取此人的电话号码
                Cursor phonesCusor = this.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        phoneProjection,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id,
                        null,
                        null);

                //因为每个联系人可能有多个电话号码，所以需要遍历
                if (phonesCusor != null && phonesCusor.moveToFirst()) {
                    do {
                        String num = phonesCusor.getString(0);
                        arr[i] += " , 电话号码：" + num;
                    } while (phonesCusor.moveToNext());
                }
                i++;
            } while (cursor.moveToNext());
        }
        return arr;
    }
}
