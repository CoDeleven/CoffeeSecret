package com.dhy.coffeesecret.model.line_search;

import com.dhy.coffeesecret.model.base.BasePresenter;
import com.dhy.coffeesecret.model.base.IBaseView;

/**
 * Created by CoDeleven on 17-8-25.
 */

public class Presenter4SearchLine extends BasePresenter<IBaseView, Model4SearchLine> {
    private static Presenter4SearchLine mSelf;
    public Presenter4SearchLine() {
        super(Model4SearchLine.newInstance());
    }

    public static Presenter4SearchLine newInstance(){
        if(mSelf == null){
            mSelf = new Presenter4SearchLine();
        }
        return mSelf;
    }
}
