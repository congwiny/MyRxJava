package com.congwiny.myrxjava.api;

import android.net.Uri;

import com.congwiny.myrxjava.bean.Cat;

import java.util.List;

/**
 * Created by congwiny on 2016/8/29.
 */
public interface CatApi {
    List<Cat> queryCats(String query);

    Uri Store(Cat cat);
}
