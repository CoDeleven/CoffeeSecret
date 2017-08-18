package com.dhy.coffeesecret.model.bake.developbar;

import com.dhy.coffeesecret.model.IBaseView;

/**
 * Created by CoDeleven on 17-8-18.
 */

public interface IDevelopBarView extends IBaseView{
    void updateDevelopBar(float rawLen, float after160Len, float firstLen);
}
