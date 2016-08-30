package com.congwiny.myrxjava.wrapper;

import android.net.Uri;

import com.congwiny.catsdk.api.*;
import com.congwiny.myrxjava.utils.CatHelper;

import java.util.List;

/**
 * Created by congwiny on 2016/8/30.
 * <p>
 * 观察{@link CatApi,CatHelper}的回调接口
 *
 * @see CatApi.CatsQueryCallback2
 * @see CatApi.StoreCallback2
 * @see CatHelper.CutestCatCallback2
 * <p>
 * 会发现一个通用的模式：
 * 1、这些接口都有一个函数来返回结果
 * @see CatApi.CatsQueryCallback2#onCatListReceived(List)
 * @see CatApi.StoreCallback2#onCatStored(Uri)
 * @see CatHelper.CutestCatCallback2#onCutestCatSaved(Uri)
 * <p>
 * 2、这些接口还都有一个返回异常情况的函数
 * @see CatApi.CatsQueryCallback2#onQueryFailed(Exception)
 * @see CatApi.StoreCallback2#onStoreFailed(Exception)
 * @see CatHelper.CutestCatCallback2#onError(Exception)
 * <p>
 * <p>
 * 所以我们可以使用一个#泛型接口#来替代这三个接口。
 * 由于我们无法修改Api调用的参数类型，必须要创建一个包装类来调用泛型接口
 */
public interface Callback<T> {
    void onResult(T result);

    void onError(Exception e);
}
