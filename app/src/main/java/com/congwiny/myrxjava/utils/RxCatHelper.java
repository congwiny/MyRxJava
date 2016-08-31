package com.congwiny.myrxjava.utils;

import android.net.Uri;

import com.congwiny.catsdk.bean.Cat;
import com.congwiny.myrxjava.wrapper.RxCatApiWrapper;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by congwiny on 2016/8/31.
 */
public class RxCatHelper {

    /**
     * AsyncJob 等同于 Observable， 不仅仅可以返回一个结果，还可以返回一系列的结果，当然也可能没有结果返回。
     * <p/>
     * Callback 等同于 Observer， 除了onNext(T t), onError(Throwable t)以外，还有一个onCompleted()函数，
     * 该函数在结束继续返回结果的时候通知Observable 。
     * <p/>
     * abstract void start(Callback callback) 和 Subscription subscribe(final Observer observer) 类似，
     * 返回一个Subscription ，如果你不再需要后面的结果了，可以取消该任务。
     * 除了 map 和 flatMap 以外， Observable 还有很多其他常见的转换操作。
     * <p/>
     * 通过简单的转换操作，我们可以把异步操作抽象出来。这种抽象的结果可以像操作简单的阻塞函数一样来操作异步操作并组合异步操作。
     * 这样我们就可以摆脱层层嵌套的回调接口了，并且不用手工的去处理每次异步操作的异常。
     */

    RxCatApiWrapper rxCatApiWrapper;

    public Observable<Uri> saveTheCutestCat(String query) {

        return rxCatApiWrapper.queryCats(query)
                .map(new Func1<List<Cat>, Cat>() {
                    @Override
                    public Cat call(List<Cat> cats) {
                        return findCutestCat(cats);
                    }
                })
                .flatMap(new Func1<Cat, Observable<Uri>>() {
                    @Override
                    public Observable<Uri> call(Cat cat) {
                        return rxCatApiWrapper.store(cat);
                    }
                });
    }


    private Cat findCutestCat(List<Cat> cats) {
        return Collections.max(cats);
    }
}
