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

public class BakeInfoFragment extends Fragment {


    private View mView;
    private ExpandableLayoutListView mListView;

    public BakeInfoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                return 2;
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
