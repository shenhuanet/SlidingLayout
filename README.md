# SlidingLayout
[ ![jCenter](https://img.shields.io/badge/version-1.0.0-yellowgreen.svg) ](https://dl.bintray.com/shenhuanetos/maven/com/shenhua/libs/slidingLayout/1.0/)
[![Build Status](https://img.shields.io/travis/rust-lang/rust/master.svg)](https://bintray.com/shenhuanetos/maven/slidingLayout)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

SlidingLayout is a Android platform View control, it can help you achieve a similar WeChat web browsing drop-down function, it can also help you achieve a similar UITableView iOS drop-down bounce jelly effect.

SlidingLayout is perfectly compatible with all View components of the Android native libraries and compatible libraries,including RecyclerView,ListView,ScrollView,and WebView.

Project site： <https://github.com/shenhuanet/SlidingLayout>.

Demo: <https://github.com/shenhuanet/SlidingLayout/tree/master/apk/demo.apk>.

## ScreenShot：
### On ScrollView
![p1](https://raw.githubusercontent.com/shenhuanet/SlidingLayout/master/screenshot/scrollview.gif)

### On ListView
![p2](https://raw.githubusercontent.com/shenhuanet/SlidingLayout/master/screenshot/listview.gif)

### On RecyclerView
![p3](https://raw.githubusercontent.com/shenhuanet/SlidingLayout/master/screenshot/recyclerview.gif)

### On WebView
![p4](https://raw.githubusercontent.com/shenhuanet/SlidingLayout/master/screenshot/webview.gif)

## how to use:
```
dependencies {
    compile 'com.shenhua.libs:slidinglayout:1.0'
}
```

If you project need to support API V9,you should add this：

``` groovy
compile 'com.nineoldandroids:library:2.4.0'
```

## Usage

SlidingLayout is easy to use,let the SlidingLayout to be RootLayout,for example：

### 1.Create a background view use xml

``` xml
<?xml version="1.0" encoding="utf-8"?>
<TextView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#8c8c8e"
    android:gravity="top"
    android:textSize="12sp"
    android:textColor="#f5f3f3"
    android:padding="16dp"
    android:text="developed by Shenhua"/>
```

### 2.Add your views included SlidingLayout

Note that the layout needs to res-auto namespace, pay attention to its own control to set a background, otherwise it will Perspective Perspective out.

```xml
<?xml version="1.0" encoding="utf-8"?>
<com.shenhua.lib.slidinglayout.SlidingLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/slidingLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:sliding_mode="both"
    app:background_view="@layout/view_bg">

    <ListView
        android:id="@+id/listView"
        android:background="#ffffff"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
    </ListView>
</com.shenhua.lib.slidinglayout.SlidingLayout>
```

### 3.Run！


## XML param

 * `background_view` another layout or view
 * `sliding_mode` both,top,bottom,(default both)
 * `sliding_pointer_mode` gesture,one for one finger,more for more fingers,more is default.
 * `top_max` valid only in top mode.
Maximum distance for slides, such as "top_max: 200dp", defaults to -1 (no limit)

## Commonly used API

 * `public void setSlidingOffset(float slidingOffset)`
Set the sliding resistance of the control, the effective value of 0.1F ~ 1.0F, the smaller the value, the greater the resistance, the default is 0.5F.
 * `public void setTargetView(View view)`
Sets the foreground of the control.
 * `public void setBackgroundView(View view)`
Sets the background of the control
 * `public void setSlidingListener(SlidingListener slidingListener)` Set the monitor to monitor, you can monitor the sliding situation.
 * `public void setSlidingMode(int mode)` Set the slide mode
 * `public void setSlidingDistance(int max)` Set the maximum sliding distance, valid only in top mode.

## Developed By

 * shenhua - <shenhuanet@126.com>

## License
Copyright 2016 Shenhua  ShenhueNet OS.