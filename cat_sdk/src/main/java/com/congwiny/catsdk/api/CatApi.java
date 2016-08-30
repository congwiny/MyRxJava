package com.congwiny.catsdk.api;

import android.net.Uri;


import com.congwiny.catsdk.bean.Cat;

import java.util.List;

/**
 * Created by congwiny on 2016/8/29.
 */
public interface CatApi {
    //网络查询猫咪
    interface CatsQueryCallback1 {
        void onCatListReceived(List<Cat> cats);

        void onError(Exception e);//拆分到两个接口的方法里：onQueryFailed，onStoreFailed
    }

    interface CatsQueryCallback2 {
        void onCatListReceived(List<Cat> cats);

        void onQueryFailed(Exception e);
    }

    interface StoreCallback2{
        void onCatStored(Uri uri);
        void onStoreFailed(Exception e);
    }

    List<Cat> queryCats2(String query, CatsQueryCallback2 catsQueryCallback);

    List<Cat> queryCats1(String query, CatsQueryCallback1 catsQueryCallback);

    List<Cat> queryCats0(String query);//1

    Uri store(Cat cat);
    Uri store2(Cat cat, StoreCallback2 storeCallback2);
}
