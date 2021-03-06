package com.congwiny.myrxjava.async;

import com.congwiny.catsdk.bean.Cat;
import com.congwiny.myrxjava.map.Func;
import com.congwiny.myrxjava.wrapper.Callback;
import com.congwiny.catsdk.api.CatApi;
import com.congwiny.myrxjava.utils.*;

/**
 * Created by congwiny on 2016/8/31.
 * <p/>
 * 分离参数和回调接口
 *
 * @see CatApi#queryCats2(String, CatApi.CatsQueryCallback2)
 * @see CatApi#store2(Cat, CatApi.StoreCallback2)
 * @see CatHelper2#saveTheCutestCat(String, Callback)
 * <p/>
 * 这些函数都有同样的方式，传入一些参数（String,Cat）和回调接口(Callback)
 * <p/>
 * 甚至所有的异步操作都带有一些常规参数和一个回调接口参数。
 * <p/>
 * 能不能把他们分离？
 * 让每个异步操作只有一些常规参数，而函数返回的一个临时的对象来操作回调接口
 * <p/>
 * 如果我们返回一个临时的对象作为异步操作的回调接口处理方式，我们需要先定义这个对象。
 * 由于对象遵守通用的行为（有一个回调接口参数），我们定义一个能用于所有操作的对象。
 * 我们称之为 AsyncJob。
 */
public abstract class AsyncJob<T> {
    public abstract void start(Callback<T> callback);

    public <R> AsyncJob<R> map(final Func<T, R> func) {
        final AsyncJob<T> source = this;

        return new AsyncJob<R>() {
            @Override
            public void start(final Callback<R> callback) {
                source.start(new Callback<T>() {
                    @Override
                    public void onResult(T result) {
                        R mapped = func.call(result);
                        callback.onResult(mapped);
                    }

                    @Override
                    public void onError(Exception e) {
                        callback.onError(e);
                    }
                });
            }
        };
    }

    public <R> AsyncJob<R> flatMap(final Func<T, AsyncJob<R>> func) {
        final AsyncJob<T> source = this;
        return new AsyncJob<R>() {
            @Override
            public void start(final Callback<R> callback) {
                source.start(new Callback<T>() {
                    @Override
                    public void onResult(T result) {
                        AsyncJob<R> flapMapped = func.call(result);
                        //转换后，需要执行
                        flapMapped.start(new Callback<R>() {
                            @Override
                            public void onResult(R result) {
                                callback.onResult(result);
                            }

                            @Override
                            public void onError(Exception e) {
                                callback.onError(e);
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        callback.onError(e);
                    }
                });
            }
        };
    }
}
