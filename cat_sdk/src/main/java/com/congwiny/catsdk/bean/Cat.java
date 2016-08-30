package com.congwiny.catsdk.bean;

import android.graphics.Bitmap;

/**
 * Created by congwiny on 2016/8/29.
 */
public class Cat implements Comparable<Cat> {

    Bitmap image;
    int cuteness;

    @Override
    public int compareTo(Cat another) {
        return cuteness > another.cuteness ? 1 : (cuteness == another.cuteness ? 0 : -1);
    }
}
