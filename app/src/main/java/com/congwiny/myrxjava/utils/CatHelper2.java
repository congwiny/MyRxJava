package com.congwiny.myrxjava.utils;

import android.net.Uri;

import com.congwiny.catsdk.bean.Cat;
import com.congwiny.myrxjava.wrapper.Callback;
import com.congwiny.myrxjava.wrapper.CatApiWrapper;

import java.util.Collections;
import java.util.List;

/**
 * Created by congwiny on 2016/8/30.
 */
public class CatHelper2 {
    //使用包装类取代CatApi
    CatApiWrapper catApiWrapper;

    /**
     * 使用此工具类 保存只需要传入两个参数
     *
     * @param query
     * @param cutestCatCallback 保存结果的回调
     */
    public void saveTheCutestCat(String query, final Callback<Uri> cutestCatCallback) {
        catApiWrapper.queryCats2(query, new Callback<List<Cat>>() {
            @Override
            public void onResult(List<Cat> cats) {
                Cat cat = findCutestCat(cats);
                /**
                 * 由于使用了泛型回调接口，这里的cutestCatCallback 可以直接设置为函数 apiWrapper.store的参数，
                 * 所以 上面的代码比前面的代码
                 * @see CatHelper#saveTheCutestCat2(String, CatHelper.CutestCatCallback2)
                 * 要少一层匿名类。看起来简单一点。
                 */
                catApiWrapper.store2(cat, cutestCatCallback);
            }

            @Override
            public void onError(Exception e) {
                cutestCatCallback.onError(e);
            }
        });
    }

    private Cat findCutestCat(List<Cat> cats) {
        return Collections.max(cats);
    }

}
