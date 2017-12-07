package com.cqnu.lengyt.fragment;


import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.arlib.floatingsearchview.FloatingSearchView;

import com.cqnu.lengyt.R;
import com.cqnu.lengyt.adapter.ContactsListAdapter;
import com.cqnu.lengyt.bean.Contact;
import com.cqnu.lengyt.utils.CharacterParser;
import com.cqnu.lengyt.utils.ToastUtil;
import com.cqnu.lengyt.widget.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    private FloatingSearchView searchView;
    private ListView listView;
    private ProgressDialog pd;
    private SideBar sideBar;
    private TextView hintTv;
    private ContactsListAdapter adapter;
    private List<Contact> list = new ArrayList<Contact>();
    private static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Photo.PHOTO_ID, ContactsContract.CommonDataKinds.Phone.CONTACT_ID};

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initView(view);
        return view;

    }


    private void initView(View view) {
        hintTv = (TextView) view.findViewById(R.id.centerHintTv);
        searchView = (FloatingSearchView) view.findViewById(R.id.floating_search_view);
        listView = (ListView) view.findViewById(R.id.listview);
        sideBar = (SideBar) view.findViewById(R.id.sidebar);
        pd = new ProgressDialog(getContext());
        pd.setTitle("提示");
        pd.setMessage("正在读取，请稍等...");
        pd.setCancelable(false);
        new LoadDataTask().execute();
        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {
                if (adapter != null) {
                    int len = newQuery.length();
                    if (len == 0) {
                        adapter.resetData();
                    } else if (len > 0) {
                        adapter.queryData(newQuery.toString());
                    }
                }
                //get suggestions based on newQuery

               /* //pass them on to the search view
                searchView.swapSuggestions(newSuggestions);*/
            }
        });


        //监听SideBar的手指按下和抬起事件
        sideBar.setOnSelectListener(new SideBar.OnSelectListener() {

            @Override
            public void onSelect(String s) {
                //手指按下时显示中央的字母
                hintTv.setVisibility(View.VISIBLE);
                hintTv.setText(s);
                //如果SideBar按下的是#号，则ListView定位到位置0
                if ("#".equals(s)) {
                    listView.setSelection(0);
                    return;
                }
                //获取手指按下的字母所在的块索引
                int section = s.toCharArray()[0];
                //根据块索引获取该字母首次在ListView中出现的位置
                int pos = adapter.getPositionForSection(section - 65);
                //定位ListView到按下字母首次出现的位置
                listView.setSelection(pos);
            }

            @Override
            public void onMoveUp(String s) {
                hintTv.setVisibility(View.GONE);
                hintTv.setText(s);
            }
        });
    }

    //显示联系人列表
    private void showListView(final List<Contact> list) {
        adapter = new ContactsListAdapter(getContext(), list, R.layout.list_item);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                //点击ListView的某个item后显示当前选择的联系人
                String name = adapter.getItem(arg2).getName();
                ToastUtil.showToast(getContext(), 0, name);
            }
        });
    }

    //
    private Comparator<Contact> comparator = new Comparator<Contact>() {

        @Override
        public int compare(Contact arg0, Contact arg1) {
            //获取联系人姓名对应的拼音，通过比较拼音来确定联系人的先后次序
            String pinyin0 = CharacterParser.getInstance().getPinYinSpelling(arg0.getName());
            String pinyin1 = CharacterParser.getInstance().getPinYinSpelling(arg1.getName());
            return pinyin0.compareTo(pinyin1);
        }
    };


    private class LoadDataTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            list = getSystemContactInfos();
            for (int i = 0; i < list.size(); i++) {
                String name = list.get(i).getName();
                if (!TextUtils.isEmpty(name) && name.length() > 0) {
                    String pinyin = CharacterParser.getInstance().getPinYinSpelling(name.substring(0, 1));
                    list.get(i).setFirstLetter((char) (pinyin.charAt(0) - 32));
                }

            }
            /*if(!TextUtils.isEmpty(name) && name.length() > 0){
                //获取城市名的首字母（这里获取的是小写）
                String pinyin = CharacterParser.getInstance().getPinYinSpelling(name.substring(0, 1));
                //-32获取小写首字母对应的大写字母
                city.setFirstLetter((char)(pinyin.charAt(0) - 32));
            }*/
            Collections.sort(list, comparator);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pd.dismiss();
            showListView(list);
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
                    infos.add(info);
                    info = null;
                }
                cursor.close();

            }
            return infos;
        }

    }


}
