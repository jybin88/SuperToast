package com.lfh.custom.widget.toast;

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.Gravity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.ReplaySubject;


/**
 * Toast 工具类
 * <p>
 * Created by Administrator on 2017/1/18 0018.
 * <p>
 * 在 {@link #TIME_SPAN} 的时间内连续调用时，会去除相同的Toast<br/>
 * 如：在 {@link #TIME_SPAN} 的时间内连续用“网络错误”调用，只会弹出一个Toast显示“网络错误”<br/>
 * 注：如果是不同的 <code>string</code> 调用则不会去重，会按顺序弹出
 */
public class ToastUtil {
    private static final int TIME_SPAN = 500;
    private static ToastUtil sInstance;
    private ReplaySubject<ToastParam> mToastSubject = ReplaySubject.create();

    private ToastUtil() {
        /* no-op */
    }

    private static ToastUtil instance() {
        if (null == sInstance) {
            synchronized (ToastUtil.class) {
                if (null == sInstance) {
                    sInstance = new ToastUtil();
                }
            }
        }

        return sInstance;
    }

    @SuppressWarnings("unused")
    private Subscription mToastSubscription = mToastSubject
            .filter(new Func1<ToastParam, Boolean>() {
                @Override
                public Boolean call(ToastParam pToastParam) {
                    return null != pToastParam.mContext && !TextUtils.isEmpty(pToastParam.mString);
                }
            })
            .buffer(TIME_SPAN, TimeUnit.MILLISECONDS)
            .filter(new Func1<List<ToastParam>, Boolean>() {
                @Override
                public Boolean call(List<ToastParam> pToastParams) {
                    return null != pToastParams && !pToastParams.isEmpty();
                }
            })
            .map(new Func1<List<ToastParam>, List<ToastParam>>() {
                @Override
                public List<ToastParam> call(List<ToastParam> pToastParams) {
                    ArrayList<ToastParam> newParams = new ArrayList<>();

                    for (ToastParam param : pToastParams) {
                        if (!newParams.contains(param)) {
                            newParams.add(param);
                        }
                    }

                    return newParams;
                }
            })
            .flatMap(new Func1<List<ToastParam>, Observable<ToastParam>>() {
                @Override
                public Observable<ToastParam> call(List<ToastParam> pToastParams) {
                    return Observable.from(pToastParams);
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<ToastParam>() {
                @Override
                public void call(ToastParam pToastParam) {
                    toast(pToastParam.mContext, pToastParam.mString, pToastParam.mGravity);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable pThrowable) {
                    pThrowable.printStackTrace();
                }
            });

    private void toast(Context pContext, String pMessage, int pGravity) {
        SuperToast superToast = SuperToast.create(pContext.getApplicationContext(), pMessage, SuperToast.Duration.VERY_SHORT);
        superToast.setTextSize(SuperToast.TextSize.MEDIUM);
        superToast.setGravity(pGravity, 0, 0);
        superToast.show();
    }

    /**
     * 显示Toast
     *
     * @param pContext   {@link Context}
     * @param pStringRes 消息资源
     */
    @SuppressWarnings("unused")
    public static void show(final Context pContext, @StringRes int pStringRes) {
        show(pContext, pContext.getApplicationContext().getString(pStringRes));
    }

    /**
     * 显示Toast
     *
     * @param pContext {@link Context}
     * @param pString  消息
     */
    public static void show(final Context pContext, final String pString) {
        instance().mToastSubject.onNext(new ToastParam(pContext, pString));
    }

    /**
     * 显示Toast
     *
     * @param pContext   {@link Context}
     * @param pStringRes 消息资源
     * @param pGravity   toast 位置 {@link Gravity}
     */
    @SuppressWarnings("unused")
    public static void show(final Context pContext, @StringRes int pStringRes, final int pGravity) {
        show(pContext, pContext.getApplicationContext().getString(pStringRes), pGravity);
    }

    /**
     * 显示Toast
     *
     * @param pContext {@link Context}
     * @param pString  消息
     * @param pGravity toast 位置 {@link Gravity}
     */
    @SuppressWarnings("WeakerAccess")
    public static void show(final Context pContext, final String pString, final int pGravity) {
        instance().mToastSubject.onNext(new ToastParam(pContext, pString, pGravity));
    }

    private static class ToastParam {
        private final Context mContext;
        private final String mString;
        private int mGravity = Gravity.BOTTOM;

        private ToastParam(Context pContext, String pString) {
            mContext = pContext;
            mString = pString;
        }

        private ToastParam(Context pContext, String pString, int pGravity) {
            this(pContext, pString);
            mGravity = pGravity;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ToastParam that = (ToastParam) o;
            return mString != null ? mString.equals(that.mString) : that.mString == null;
        }

        @Override
        public int hashCode() {
            return mString != null ? mString.hashCode() : 0;
        }
    }
}
