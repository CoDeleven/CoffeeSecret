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
import com.dhy.coffeesecret.ui.cup.listener.GridViewItemClickListener;
import com.dhy.coffeesecret.utils.ArrayUtil;
import com.dinuscxj.progressbar.CircleProgressBar;

import static com.dhy.coffeesecret.R.drawable.*;
import static com.dhy.coffeesecret.ui.cup.listener.GridViewItemClickListener.FEEL_GRID;
import static com.dhy.coffeesecret.ui.cup.listener.GridViewItemClickListener.FLAW_GRID;

public class CuppingInfoFragment extends Fragment implements InputDialogFragment.OnValueChangeListener {

    private static final String[] FEEL_TITLES = {"干湿香", "风味", "余韵", "酸质", "口感", "甜感", "均衡度", "整体感受"};
    private static final int[] FEEL_ICONS = {
            ic_dry_fragrant, ic_flavor, ic_after_taste, ic_acidity,
            ic_feel, ic_sweet, ic_balance, ic_overall
    };


    private static final String[] FLAW_TITLES = {"发展不充分", "过度发展", "烤焙味", "自焙烫伤", "胚芽烫伤", "豆表烫伤"};
    private static final int[] FLAW_ICONS = {ic_underdev, ic_overdev, ic_baked, ic_scorched, ic_tipped, ic_faced};

    private static final int FEEL_SCORE_MAX = 100;
    private static final int FLAW_SCORE_MAX = 100;

    private static final String FLAW_SCORES_ARRAY = "flawScores";
    private static final String FEEL_SCORES_ARRAY = "feelScores";

    private OnFragmentInteractionListener mListener;
    private GridView mGridViewFeel;
    private GridView mGridViewFlaw;
    private CircleProgressBar feelProgressBar;
    private CircleProgressBar flawProgressBar;
    private CircleProgressBar finalProgressBar;

    private View cuppingInfoView;

    private float[] flawScores;
    private float[] feelScores;
    private boolean isNewCupping = false;

    private InfoGridViewAdapter mFeelAdapter;
    private InfoGridViewAdapter mFlawAdapter;
    private GridViewItemClickListener mFeelGridListener;
    private GridViewItemClickListener mFlawGridListener;
    private boolean mEditable;

    public CuppingInfoFragment() {
    }

    public static CuppingInfoFragment newInstance(float[] flawScores, float[] feelScores) {
        Bundle args = new Bundle();
        args.putFloatArray(FEEL_SCORES_ARRAY, feelScores);
        args.putFloatArray(FLAW_SCORES_ARRAY, flawScores);
        CuppingInfoFragment fragment = new CuppingInfoFragment();
        fragment.setArguments(args);
        return fragment;
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
        feelProgressBar = (CircleProgressBar) cuppingInfoView.findViewById(R.id.cpb_feel);
        flawProgressBar = (CircleProgressBar) cuppingInfoView.findViewById(R.id.cpb_flaw);
        finalProgressBar = (CircleProgressBar) cuppingInfoView.findViewById(R.id.cpb_final);

        mFeelAdapter = new InfoGridViewAdapter(FEEL_ICONS, feelScores, FEEL_TITLES);
        mFlawAdapter = new InfoGridViewAdapter(FLAW_ICONS, flawScores, FLAW_TITLES);

        mGridViewFeel.setAdapter(mFeelAdapter);
        mGridViewFlaw.setAdapter(mFlawAdapter);
        feelProgressBar.setMax(FEEL_SCORE_MAX);
        flawProgressBar.setMax(FLAW_SCORE_MAX);
        finalProgressBar.setMax(FEEL_SCORE_MAX);

        InputDialogFragment fragment = InputDialogFragment.newInstance(isNewCupping, ArrayUtil.merge(feelScores, flawScores));

        mFeelGridListener = new GridViewItemClickListener(getChildFragmentManager(), fragment, FEEL_GRID);
        mFlawGridListener = new GridViewItemClickListener(getChildFragmentManager(), fragment, FLAW_GRID);

        mGridViewFeel.setOnItemClickListener(mFeelGridListener);
        mGridViewFlaw.setOnItemClickListener(mFlawGridListener);

        setEditable(mEditable);
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 设置GridView 是否可以更改数值
     *
     * @param editable true则可以更改
     */
    public void setEditable(boolean editable) {
        mFeelGridListener.setEditable(editable);
        mFlawGridListener.setEditable(editable);
    }

    public void initEditable(boolean editable) {
        this.mEditable = editable;
    }

    /**
     * 更新底部三个圆圈的数值
     *
     * @param feelScore 口感评分
     * @param flawScore 瑕疵分
     */
    public void updateProgressBar(final int feelScore, final int flawScore) {

        feelProgressBar.setProgress(feelScore);
        flawProgressBar.setProgress(flawScore);
        finalProgressBar.setProgress(feelScore - flawScore);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            feelScores = getArguments().getFloatArray(FEEL_SCORES_ARRAY);
            flawScores = getArguments().getFloatArray(FLAW_SCORES_ARRAY);
        }

        if (feelScores == null) {
            feelScores = new float[]{0, 0, 0, 0, 0, 0, 0, 0};
            flawScores = new float[]{0, 0, 0, 0, 0, 0};
            isNewCupping = true;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnValueChangeListener");
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

    @Override
    public void onValueChange(float value) {
        // TODO: 2017/2/20
    }


    public interface OnFragmentInteractionListener {
        void onCuppingFragmentInteraction(Uri uri);
    }

    class InfoGridViewAdapter extends BaseAdapter {

        private String[] titles;
        private int[] icons;
        private float[] scores;

        InfoGridViewAdapter(int[] icons, float[] scores, String[] titles) {
            this.icons = icons;
            this.scores = scores;
            this.titles = titles;
        }

        public void setScores(int index, float value) {
            scores[index] = value;
            notifyDataSetChanged();
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
            iv.setImageDrawable(getResources().getDrawable(icons[i]));
            score.setText(scores[i] + "");
            return inflate;
        }
    }
}
