package com.dhy.coffeesecret.model.line_color;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.model.base.BaseModel;
import com.dhy.coffeesecret.pojo.LinesColor;
import com.dhy.coffeesecret.utils.ConvertUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by CoDeleven on 17-8-24.
 */

public class Model4ColorLine extends BaseModel implements IColorLineModel {
    private static Model4ColorLine mSelf;
    private Map<String, Integer> mCurProcessColor;
    private List<Integer> colors = new LinkedList<>();
    private final static Integer[] LINES_COLOR = {
            R.color.default_0, R.color.default_1, R.color.default_2
            , R.color.default_3, R.color.default_4, R.color.default_5, R.color.default_6,
            R.color.simo_blue300, R.color.simo_green300, R.color.simo_red300, R.color.simo_yellow300,
            R.color.simo_purple700, R.color.simo_pink700, R.color.simo_limea400, R.color.simo_orange300,
            R.color.simo_brown300, R.color.simo_grey500, R.color.simo_lightgreena400, R.color.simo_reda700};

    public static Model4ColorLine newInstance(){
        if(mSelf == null){
            mSelf = new Model4ColorLine();
        }
        return mSelf;
    }

    private Model4ColorLine(){
        mCurProcessColor = new LinkedHashMap<>();
        mCurProcessColor.put("colorPreviewBean", R.color.default_0);
        mCurProcessColor.put("colorPreviewInWind", R.color.default_1);
        mCurProcessColor.put("colorPreviewOutWind", R.color.default_2);
        mCurProcessColor.put("colorPreviewAccBean", R.color.default_3);
        mCurProcessColor.put("colorPreviewAccInWind", R.color.default_4);
        mCurProcessColor.put("colorPreviewAccOutWind", R.color.default_5);
        mCurProcessColor.put("colorPreviewEnv", R.color.default_6);

        Collections.addAll(colors, LINES_COLOR);

        // 移除上面7个已用的颜色
        for(int i = 6; i >= 0; --i){
            // 因为ArrayList，删除一个元素后面的元素移前
            colors.remove(0);
        }
    }

    @Override
    public void updateColor(String key, int position) {
        // 获取该key原先的颜色
        int originalColor = mCurProcessColor.get(key);
        // 添加到备用color
        colors.add(originalColor);

        mCurProcessColor.put(key, colors.get(position));

        // 移除备用color中 已经使用的color
        colors.remove(position);
    }

    @Override
    public LinesColor saveAllColors(Map<String, Integer> colors) {
        LinesColor linesColor = new LinesColor();
        linesColor.setBeanColor(ConvertUtils.toHexColor(colors.get("colorPreviewBean")));
        mConfig.setBeanColor(colors.get("colorPreviewBean"));

        linesColor.setInwindColor(ConvertUtils.toHexColor(colors.get("colorPreviewInWind")));
        mConfig.setInwindColor(colors.get("colorPreviewInWind"));

        linesColor.setOutwindColor(ConvertUtils.toHexColor(colors.get("colorPreviewOutWind")));
        mConfig.setOutwindColor(colors.get("colorPreviewOutWind"));

        linesColor.setAccBeanColor(ConvertUtils.toHexColor(colors.get("colorPreviewAccBean")));
        mConfig.setAccBeanColor(colors.get("colorPreviewAccBean"));

        linesColor.setAccInwindColor(ConvertUtils.toHexColor(colors.get("colorPreviewAccInWind")));
        mConfig.setAccInwindColor(colors.get("colorPreviewAccInWind"));

        linesColor.setAccOutwindColor(ConvertUtils.toHexColor(colors.get("colorPreviewAccOutWind")));
        mConfig.setAccOutwindColor(colors.get("colorPreviewAccOutWind"));

        linesColor.setEnvColor(ConvertUtils.toHexColor(colors.get("colorPreviewEnv")));
        mConfig.setEnvColor(colors.get("colorPreviewEnv"));

        return linesColor;
    }

    public void saveName(String colorLinesName){
        mConfig.setColorPackageName(colorLinesName);
    }
    public Map<String, Integer> getColors(){
        return mCurProcessColor;
    }
    public List<Integer> getAllWaitingColors(){
        return colors;
    }
}
