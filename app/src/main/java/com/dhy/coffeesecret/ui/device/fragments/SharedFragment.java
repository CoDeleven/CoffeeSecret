package com.dhy.coffeesecret.ui.device.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.dhy.coffeesecret.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by CoDeleven on 17-3-15.
 */

public class SharedFragment extends DialogFragment {
    public static final String BEAN_NAME = "bean_name";
    public static final String BAKE_DEGREE = "bake_degree";
    public static final String DEVELOP_RATE = "develop_rate";
    public static final String SPECIES = "species";

    @Bind(R.id.id_shared_beanName)
    TextView beanNameView;
    @Bind(R.id.id_shared_bakeDegree)
    TextView bakeDegreeView;
    @Bind(R.id.id_shared_developRate)
    TextView developRateView;
    @Bind(R.id.id_shared_species)
    TextView speciesView;
    private Context mContext;
    private String beanName, bakeDegree, developRate, species;

    public SharedFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beanName = getArguments().getString(BEAN_NAME);
        bakeDegree = getArguments().getString(BAKE_DEGREE);
        developRate = getArguments().getString(DEVELOP_RATE);
        species = getArguments().getString(SPECIES);
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
        developRateView.setText("发展率：" + developRate);
        speciesView.setText("豆种：" + species);

        return view;
    }
}
