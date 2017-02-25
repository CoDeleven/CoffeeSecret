package com.dhy.coffeesecret.ui.cup.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.andexert.expandablelayout.library.ExpandableLayout;
import com.andexert.expandablelayout.library.ExpandableLayoutItem;
import com.andexert.expandablelayout.library.ExpandableLayoutListView;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReport;

public class BakeInfoFragment extends Fragment {

    public final static String TARGET = "target";
    private View mView;
    private ExpandableLayoutListView mListView;
    private BakeReport mBakeReport;

    public BakeInfoFragment() {

    }

    public static BakeInfoFragment newInstance(BakeReport report){
        BakeInfoFragment fragment = new BakeInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable(TARGET,report);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBakeReport = (BakeReport) getArguments().getSerializable(TARGET);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_bake_info, container, false);
        return mView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView = (ExpandableLayoutListView) mView.findViewById(R.id.beanInfo);

        mListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mBakeReport.getBeanInfoSimples().size();
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.expande_item, viewGroup, false);
                // TODO: 2017/2/25 烘焙报告里的豆种信息填充到列表中
                if (i == 0){
                    ExpandableLayoutItem layout =
                            (ExpandableLayoutItem) inflate.findViewById(R.id.eli);
                    layout.show();
                }
                return inflate;
            }
        });

    }
}
