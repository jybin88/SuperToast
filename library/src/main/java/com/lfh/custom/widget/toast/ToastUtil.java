package com.lfh.custom.widget.toast;

import android.content.Context;
import android.support.annotation.StringRes;
import android.view.Gravity;


/**
 * Created by Administrator on 2017/1/18 0018.
 */
public class ToastUtil {
    private static ToastUtil sInstance;

    private ToastUtil() {
    }

    public static ToastUtil getInstance() {
        if (null == sInstance) {
            synchronized (ToastUtil.class) {
                if (null == sInstance) {
                    sInstance = new ToastUtil();
                }
            }
        }

        return sInstance;
    }

    public void toast(Context pContext, @StringRes int res) {
        toast(pContext, pContext.getApplicationContext().getString(res));
    }

    public void toast(Context pContext, String message) {
        SuperToast superToast = SuperToast.create(pContext.getApplicationContext(), message, SuperToast.Duration.VERY_SHORT);
        superToast.setTextSize(SuperToast.TextSize.MEDIUM);
        superToast.setGravity(Gravity.BOTTOM, 0, 0);
        superToast.show();
    }
}
