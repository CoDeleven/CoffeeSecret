package com.dhy.coffeesecret.ui.cup;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.CuppingInfo;
import com.dhy.coffeesecret.ui.cup.adapter.CuppingListAdapter;
import com.dhy.coffeesecret.views.DividerDecoration;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.dhy.coffeesecret.ui.cup.NewCuppingActivity.NEW_CUPPING;
import static com.dhy.coffeesecret.ui.cup.NewCuppingActivity.SHOW_INFO;
import static com.dhy.coffeesecret.ui.cup.NewCuppingActivity.TARGET;
import static com.dhy.coffeesecret.ui.cup.NewCuppingActivity.VIEW_TYPE;

public class CupFragment extends Fragment {


    private OnCupInteractionListener mListener;
    private View mCuppingView;
    private Context mContext;
    private ImageView mAddButton;

    private RecyclerView mRecyclerView;
    private List<CuppingInfo> cuppingInfos;
    private CuppingListAdapter mAdapter;

    public CupFragment() {
        cuppingInfos = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            CuppingInfo cuppingInfo = new CuppingInfo();
            cuppingInfo.setTitle("mxf---" + i);
            cuppingInfo.setScore((60 + 5 * i) % 100);
            cuppingInfo.setDate(new Date());
            cuppingInfos.add(cuppingInfo);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAddButton = (ImageView) mCuppingView.findViewById(R.id.iv_add);
        mRecyclerView = (RecyclerView) mCuppingView.findViewById(R.id.rv_cupping);
        mAdapter = new CuppingListAdapter(mContext, cuppingInfos);

        mAdapter.setOnItemClickListener(new CuppingListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(mContext, NewCuppingActivity.class);
                intent.putExtra(TARGET, cuppingInfos.get(position));
                intent.putExtra(VIEW_TYPE, SHOW_INFO);
                startActivity(intent);
            }
        });

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mRecyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration(mAdapter));
        mRecyclerView.addItemDecoration(new DividerDecoration(mContext));

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewCuppingActivity.class);
                intent.putExtra(VIEW_TYPE, NEW_CUPPING);
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mCuppingView = inflater.inflate(R.layout.fragment_cup, container, false);
        mContext = getContext();
        return mCuppingView;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onCupInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCupInteractionListener) {
            mListener = (OnCupInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnContainerInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnCupInteractionListener {
        // TODO: Update argument type and name
        void onCupInteraction(Uri uri);
    }
}
