package com.dhy.coffeesecret.ui.bake.fragments;

import com.dhy.coffeesecret.pojo.BeanInfo;
import com.dhy.coffeesecret.ui.counters.fragments.BeanListFragment;

/**
 * 继承BeanListFragment， 更换了里面的hook方法，根据不同的hook进行不同的调用
 * Created by CoDeleven on 17-3-23.
 */

public class BakeBeanListFragment extends BeanListFragment {
    private OnBeanSelected onBeanSelected;

    public void setOnBeanSelected(OnBeanSelected onBeanSelected) {
        this.onBeanSelected = onBeanSelected;
    }

    @Override
    public void hook(BeanInfo beanInfo) {
        this.onBeanSelected.onBeanSelected(beanInfo);
    }

    public interface OnBeanSelected {
        void onBeanSelected(BeanInfo beanInfo);
    }
}
