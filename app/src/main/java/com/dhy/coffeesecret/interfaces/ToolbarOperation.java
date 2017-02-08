package com.dhy.coffeesecret.interfaces;

import android.support.v7.widget.Toolbar;

/**
 * Created by CoDeleven on 17-2-5.
 */

public interface ToolbarOperation {
    Toolbar getToolbar();

    void modifyToolbarTitle(String title);

    float getToolbarHeight();
}
