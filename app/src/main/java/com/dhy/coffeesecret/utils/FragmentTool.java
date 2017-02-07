package com.dhy.coffeesecret.utils;

import android.content.Context;
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

    public void add(int containerId, Fragment fragment, boolean toStack) {
        FragmentTransaction tx = fragmentManager.beginTransaction();
        tx.add(containerId, fragment);
        if (toStack) {
            tx.addToBackStack(null);
        }
        tx.commit();
    }

    public void replace(int containerId, Fragment fragment, boolean toStack) {
        FragmentTransaction tx = fragmentManager.beginTransaction();
        tx.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        tx.replace(containerId, fragment);
        if (toStack) {
            tx.addToBackStack(null);
        }
        tx.commit();
    }

    public void popStack() {
        fragmentManager.popBackStackImmediate();
    }
}
