package com.dhy.coffeesecret.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by CoDeleven on 17-2-7.
 */

public class FragmentTool {
    private static Map<Object, FragmentTool> managerMap = new HashMap<>();
    private static Fragment curFragment = null;
    private FragmentManager fragmentManager;
    public FragmentTool(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public static FragmentTool getFragmentToolInstance(Context context) {
        if (!managerMap.containsKey(context)) {
            managerMap.put(context, new FragmentTool(((FragmentActivity) context).getSupportFragmentManager()));
        }
        return managerMap.get(context);
    }

    public static Fragment getCurFragment() {
        return curFragment;
    }

    public static void setCurFragment(Fragment fragment) {
        curFragment = fragment;
    }

    public void add(int containerId, Fragment fragment, boolean toStack, @Nullable String tag) {
        FragmentTransaction tx = fragmentManager.beginTransaction();
        List<Fragment> list = fragmentManager.getFragments();
        if (list != null && list.contains(fragment)) {
            tx.show(fragment);
        } else {
            tx.add(containerId, fragment, tag);
            if (toStack) {
                tx.addToBackStack(tag);
            }
        }
        curFragment = fragment;
        tx.commit();
    }

    public void replace(int containerId, Fragment fragment, boolean toStack) {
        FragmentTransaction tx = fragmentManager.beginTransaction();

        tx.replace(containerId, fragment);
        if (toStack) {
            tx.addToBackStack(null);
        }
        curFragment = fragment;
        tx.commit();
    }

    public FragmentTool hideCur() {
        FragmentTransaction tx = fragmentManager.beginTransaction();
        tx.hide(curFragment);
        // curFragment = nextFragment;
        tx.commit();
        return this;
    }


    public void removeKey(Object key) {
        managerMap.remove(key);
    }

    public Fragment getFragment(String tag) {
        Fragment fragment = null;
        if ((fragment = fragmentManager.findFragmentByTag(tag)) != null) {
            return fragment;
        }
        return fragment;
    }
}
