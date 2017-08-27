package com.dhy.coffeesecret.model.base;

/**
 * Created by CoDeleven on 17-8-1.
 */

public interface IBaseView {
    void updateText(int index, Object updateContent);
    void showToast(int index, String toastContent);
    void showWarnDialog(int index);
}
