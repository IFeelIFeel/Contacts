package com.cqnu.lengyt.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/12/7.
 */

public class ToastUtil {

    private static Toast toast;

    public static void showToast(Context context, int code, String content) {
        //code=1时Toast显示的时间长，code=0时显示的时间短。
        if (toast == null) {
            if (code == 0)
                toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
            if (code == 1)
                toast = Toast.makeText(context, content, Toast.LENGTH_LONG);
        } else {
            toast.setText(content);
        }
        toast.show();
    }
}
