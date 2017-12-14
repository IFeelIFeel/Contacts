package com.cqnu.lengyt.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.cqnu.lengyt.activity.ContactDetailActivity;
import com.cqnu.lengyt.adapter.ContactsListAdapter;
import com.cqnu.lengyt.bean.Contact;
import com.cqnu.lengyt.bean.User;
import com.cqnu.lengyt.utils.ContactUtil;
import com.cqnu.lengyt.widget.SideBar;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
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
    private String mUserName;

    public MainFragment(String userName) {
        mUserName = userName;
    }

    @Override
    public void onResume() {
        super.onResume();
        showListView();
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
        showListView();
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
                if (!TextUtils.isEmpty(s)) {
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
            }

            @Override
            public void onMoveUp(String s) {
                hintTv.setVisibility(View.GONE);
                hintTv.setText(s);
            }
        });
    }

    //显示联系人列表
    private void showListView() {
        list = DataSupport.where("user=?", mUserName).find(Contact.class);
        ContactUtil.sortContactList(list);
        adapter = new ContactsListAdapter(getContext(), list, R.layout.list_item);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                //点击ListView的某个item后进入当前选择的联系人的详情页
                String name = adapter.getItem(arg2).getName();
                String phone = adapter.getItem(arg2).getPhone();
                //ToastUtil.showToast(getContext(), 0, name);
                Intent intent = new Intent(getContext(), ContactDetailActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("phone", phone);
                intent.putExtra("position", arg2);
                startActivity(intent);

            }
        });
    }


}
