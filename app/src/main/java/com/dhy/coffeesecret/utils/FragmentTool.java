package com.dhy.coffeesecret.utils;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.HashMap;
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

    public static FragmentTool  getFragmentToolInstance(Context context) {
        if (!managerMap.containsKey(context)) {
            managerMap.put(context, new FragmentTool(((FragmentActivity) context).getSupportFragmentManager()));
        }
        return managerMap.get(context);
    }

    public void showDialogFragmen(String tag, DialogFragment dialogFragment) {
        FragmentTransaction mFragTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            //为了不重复显示dialog，在显示对话框之前移除正在显示的对话框
            mFragTransaction.remove(fragment);
        }
        //显示一个Fragment并且给该Fragment添加一个Tag，可通过findFragmentByTag找到该Fragment
        dialogFragment.show(mFragTransaction, tag);
    }
}
