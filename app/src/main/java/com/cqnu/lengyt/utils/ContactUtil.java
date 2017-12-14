package com.cqnu.lengyt.utils;

import android.text.TextUtils;

import com.cqnu.lengyt.bean.Contact;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2017/12/14.
 */

public class ContactUtil {

    //设置首字母，并按字母排序
    public static void sortContactList(List<Contact> contactList) {
        for (int i = 0; i < contactList.size(); i++) {
            String name = contactList.get(i).getName();
            if (!TextUtils.isEmpty(name) && name.length() > 0) {
                String pinyin = CharacterParser.getInstance().getPinYinSpelling(name.substring(0, 1));
                contactList.get(i).setFirstLetter((char) (pinyin.charAt(0) - 32));
            }

        }
        Collections.sort(contactList, comparator);
    }

    private static Comparator<Contact> comparator = new Comparator<Contact>() {

        @Override
        public int compare(Contact arg0, Contact arg1) {
            //获取联系人姓名对应的拼音，通过比较拼音来确定联系人的先后次序
            String pinyin0 = CharacterParser.getInstance().getPinYinSpelling(arg0.getName());
            String pinyin1 = CharacterParser.getInstance().getPinYinSpelling(arg1.getName());
            return pinyin0.compareTo(pinyin1);
        }
    };

}
