package com.dhy.coffeesecret.ui.bake.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.url.UrlBake;
import com.facebook.rebound.ui.Util;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by CoDeleven on 17-3-15.
 */

public class SharedFragment extends DialogFragment {
    public static final String BEAN_NAME = "bean_name";
    public static final String BAKE_DEGREE = "bake_degree";
    public static final String BAKE_TIME = "bake_time";
    public static final String SPECIES = "species";
    public static final String REPORT_ID = "report_id";
    public static final String TOKEN = "token";


    @Bind(R.id.id_shared_beanName)
    TextView beanNameView;
    @Bind(R.id.id_shared_bakeDegree)
    TextView bakeDegreeView;
    @Bind(R.id.id_shared_developRate)
    TextView developRateView;
    @Bind(R.id.id_shared_species)
    TextView speciesView;
    @Bind(R.id.id_iv_qrcode)
    ImageView imageView;
    private Context mContext;
    private String beanName, bakeDegree, bakeTime, species, token;
    private long id;
    public SharedFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beanName = getArguments().getString(BEAN_NAME);
        bakeDegree = getArguments().getString(BAKE_DEGREE);
        bakeTime = getArguments().getString(BAKE_TIME);
        species = getArguments().getString(SPECIES);
        id = getArguments().getLong(REPORT_ID);
        token = getArguments().getString(TOKEN);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_shared, container, false);
        ButterKnife.bind(this, view);
        beanNameView.setText("豆名：" + beanName);
        bakeDegreeView.setText("烘焙度：" + bakeDegree);
        developRateView.setText("烘焙时间：" + bakeTime);
        speciesView.setText("豆种：" + species);

        loadingData();


        return view;
}

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(Util.dpToPx(300, getResources()), WRAP_CONTENT);
    }

    private void loadingData(){
        ImageLoader.getInstance().displayImage(UrlBake.share(token, id), imageView);
    }

}
