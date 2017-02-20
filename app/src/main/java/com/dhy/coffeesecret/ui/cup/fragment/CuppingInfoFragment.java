package com.dhy.coffeesecret.ui.cup.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dhy.coffeesecret.R;

public class CuppingInfoFragment extends Fragment {

    private static final String[] FEEL_TITLES = {"a", "v", "d", "da", "da", "ad", "weq", "eq"};
    private static final int[] FEEL_ICONS = {};
    private static final int[] FEEL_SCORES = {};

    private static final String[] FLAW_TITLES = {"eqqe", "eqqeq", "eq", "eq", "eqw", };
    private static final int[] FLAW_ICONS = {};
    private static final int[] FLAW_SCORES = {};

    private OnFragmentInteractionListener mListener;
    private GridView mGridViewFeel;
    private GridView mGridViewFlaw;

    private View cuppingInfoView;

    public CuppingInfoFragment() {
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onCuppingFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        mGridViewFeel = (GridView) cuppingInfoView.findViewById(R.id.gv_feel);
        mGridViewFlaw = (GridView) cuppingInfoView.findViewById(R.id.gv_flaw);

        mGridViewFeel.setAdapter(new InfoGridViewAdapter(FEEL_ICONS,FEEL_SCORES,FEEL_TITLES));
        mGridViewFlaw.setAdapter(new InfoGridViewAdapter(FLAW_ICONS,FLAW_SCORES,FLAW_TITLES));

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        cuppingInfoView = inflater.inflate(R.layout.fragment_cupping_info, container, false);
        return cuppingInfoView;
    }


    public interface OnFragmentInteractionListener {
        void onCuppingFragmentInteraction(Uri uri);
    }

    class InfoGridViewAdapter extends BaseAdapter {

        private String[] titles;
        private int[] icons;
        private int[] scores;

        InfoGridViewAdapter(int[] icons, int[] scores, String[] titles) {
            this.icons = icons;
            this.scores = scores;
            this.titles = titles;
        }

        @Override
        public int getCount() {
            return titles.length;
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
            View inflate = getActivity().getLayoutInflater().inflate(R.layout.item_cupping_info, null);
            TextView tv = (TextView) inflate.findViewById(R.id.item_name);
            ImageView iv = (ImageView) inflate.findViewById(R.id.iv);
            TextView score = (TextView) inflate.findViewById(R.id.score);
            tv.setText(titles[i]);
            iv.setImageDrawable(getResources().getDrawable(R.drawable.ic_container_search));
            score.setText("1");
            return inflate;
        }
    }
}
