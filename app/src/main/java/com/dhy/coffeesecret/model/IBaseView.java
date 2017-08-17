package com.dhy.coffeesecret.model;

/**
 * Created by CoDeleven on 17-8-1.
 */

public interface IBaseView {
    void updateText(int index, Object updateContent);
    void showToast(int index, String toastContent);
    void showDialog(int index);
}
