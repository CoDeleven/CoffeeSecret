package com.dhy.coffeesecret.model.line_color;

import android.content.res.Resources;

import com.dhy.coffeesecret.model.base.BasePresenter;
import com.dhy.coffeesecret.pojo.LinesColor;
import com.dhy.coffeesecret.utils.ConvertUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by CoDeleven on 17-8-24.
 */

public class Presenter4ColorLine extends BasePresenter<IColorLineView, Model4ColorLine> {
    private static Presenter4ColorLine mSelf;
    private LinesColor mSavedLinesColor;
    public static Presenter4ColorLine newInstance(){
        if(mSelf == null){
            mSelf = new Presenter4ColorLine();
        }
        return mSelf;
    }

    public Presenter4ColorLine() {
        super(Model4ColorLine.newInstance());
    }

    public void changePreviewColor(String key, int position){
        mModelOperator.updateColor(key, position);
    }

    public void save(Resources resources, String colorLinesName){
        Map<String, Integer> colorDrawableIds = mModelOperator.getColors();
        Map<String, Integer> result = new HashMap<>();

        for (String key : colorDrawableIds.keySet()) {
            int colorVal = ConvertUtils.toDecimalColor(resources, colorDrawableIds.get(key));
            result.put(key, colorVal);
        }
        mSavedLinesColor = mModelOperator.saveAllColors(result);
        mSavedLinesColor.setPackageName(colorLinesName);

        mModelOperator.saveName(colorLinesName);
    }

    public ArrayList<Integer> getColors(){
        return new ArrayList<>(mModelOperator.getAllWaitingColors());
    }

    public LinesColor getSavedLinesColor(){
        return mSavedLinesColor;
    }
}
