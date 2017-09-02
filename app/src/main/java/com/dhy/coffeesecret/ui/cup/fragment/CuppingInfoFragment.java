package com.dhy.coffeesecret.ui.cup.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.ui.common.views.BakeDegreeShowBar;
import com.dhy.coffeesecret.ui.cup.listener.GridViewItemClickListener;
import com.dhy.coffeesecret.utils.ArrayUtil;
import com.dinuscxj.progressbar.CircleProgressBar;

import java.util.HashMap;
import java.util.Map;

import static com.dhy.coffeesecret.R.drawable.ic_acidity;
import static com.dhy.coffeesecret.R.drawable.ic_after_taste;
import static com.dhy.coffeesecret.R.drawable.ic_baked;
import static com.dhy.coffeesecret.R.drawable.ic_balance;
import static com.dhy.coffeesecret.R.drawable.ic_dry_fragrant;
import static com.dhy.coffeesecret.R.drawable.ic_faced;
import static com.dhy.coffeesecret.R.drawable.ic_feel;
import static com.dhy.coffeesecret.R.drawable.ic_flavor;
import static com.dhy.coffeesecret.R.drawable.ic_overall;
import static com.dhy.coffeesecret.R.drawable.ic_overdev;
import static com.dhy.coffeesecret.R.drawable.ic_scorched;
import static com.dhy.coffeesecret.R.drawable.ic_sweet;
import static com.dhy.coffeesecret.R.drawable.ic_tipped;
import static com.dhy.coffeesecret.R.drawable.ic_underdev;
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

    private static final int FEEL_SCORE_MAX = 80;
    private static final int FLAW_SCORE_MAX = 30;

    private static final String FLAW_SCORES_ARRAY = "flawScores";
    private static final String FEEL_SCORES_ARRAY = "feelScores";
    private static final String ROAST_DEGREE_KEY = "roastDegree";


    private OnDeleteListener mListener;
    private GridView mGridViewFeel;
    private GridView mGridViewFlaw;
    private CircleProgressBar feelProgressBar;
    private CircleProgressBar flawProgressBar;
    private CircleProgressBar finalProgressBar;
    private Button mDeleteButton;
    private View mScoresCircles;

    private View cuppingInfoView;
    private BakeDegreeShowBar mShowBar;
    private float[] flawScores;
    private float[] feelScores;
    private int roastDegree;
    private boolean isNewCupping = false;

    private InfoGridViewAdapter mFeelAdapter;
    private InfoGridViewAdapter mFlawAdapter;
    private GridViewItemClickListener mFeelGridListener;
    private GridViewItemClickListener mFlawGridListener;
    private boolean mEditable;

    private Map<String, Float> mData;

    public CuppingInfoFragment() {
    }

    public static CuppingInfoFragment newInstance(float[] flawScores, float[] feelScores, int roastDegree) {
        Bundle args = new Bundle();
        args.putFloatArray(FEEL_SCORES_ARRAY, feelScores);
        args.putFloatArray(FLAW_SCORES_ARRAY, flawScores);
        args.putInt(ROAST_DEGREE_KEY, roastDegree);
        CuppingInfoFragment fragment = new CuppingInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void onDelete() {
        if (mListener != null) {
            mListener.onDelete();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        mGridViewFeel = (GridView) cuppingInfoView.findViewById(R.id.gv_feel);
        mGridViewFlaw = (GridView) cuppingInfoView.findViewById(R.id.gv_flaw);
        feelProgressBar = (CircleProgressBar) cuppingInfoView.findViewById(R.id.cpb_feel);
        flawProgressBar = (CircleProgressBar) cuppingInfoView.findViewById(R.id.cpb_flaw);
        finalProgressBar = (CircleProgressBar) cuppingInfoView.findViewById(R.id.cpb_final);
        mDeleteButton = (Button) cuppingInfoView.findViewById(R.id.btn_delete);
        mShowBar = (BakeDegreeShowBar) cuppingInfoView.findViewById(R.id.id_show_bake_degree);


        mShowBar.setCurProcess(roastDegree);

        mScoresCircles = cuppingInfoView.findViewById(R.id.line_result);


        mFeelAdapter = new InfoGridViewAdapter(FEEL_ICONS, mData, FEEL_TITLES);
        mFlawAdapter = new InfoGridViewAdapter(FLAW_ICONS, mData, FLAW_TITLES);

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
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDelete();
            }
        });
        setEditable(mEditable);
        updateProgressBar(feelScores, flawScores);
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
        if (editable) {
            mDeleteButton.setVisibility(View.VISIBLE);
            mScoresCircles.setVisibility(View.GONE);
        } else {
            mDeleteButton.setVisibility(View.GONE);
            mScoresCircles.setVisibility(View.VISIBLE);
        }
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

    public void updateProgressBar(final float[] feelScores, final float[] flawScores) {
        float feelScore = 0;
        float flawScore = 0;
        for (float score : feelScores) {
            feelScore += score;
        }

        for (float score : flawScores) {
            flawScore += score;
        }
        updateProgressBar((int)feelScore, (int)flawScore);
    }

    public Map<String, Float> getData() {
        return mData;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            feelScores = getArguments().getFloatArray(FEEL_SCORES_ARRAY);
            flawScores = getArguments().getFloatArray(FLAW_SCORES_ARRAY);
            roastDegree = getArguments().getInt(ROAST_DEGREE_KEY);
        }

        if (feelScores == null) {
            feelScores = new float[]{10f, 10f, 10f, 10f, 10f, 10f, 10f, 10f};
            flawScores = new float[]{0, 0, 0, 0, 0, 0};
            roastDegree = 0;
            isNewCupping = true;
        }

        mData = new HashMap<>();
        for (int i = 0; i < FEEL_TITLES.length; i++) {
            mData.put(FEEL_TITLES[i], feelScores[i]);
        }
        for (int i = 0; i < FLAW_TITLES.length; i++) {
            mData.put(FLAW_TITLES[i], flawScores[i]);
        }

        // TODO 显示烘焙都在界面上
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDeleteListener) {
            mListener = (OnDeleteListener) context;
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
    public void onValueChange(int position, float value) {
        if (position < 8) {
            mFeelAdapter.setScore(position, value);
        } else {
            mFlawAdapter.setScore(position - 8, value);
        }
    }

    public interface OnDeleteListener {
        void onDelete();
    }

    class InfoGridViewAdapter extends BaseAdapter {

        private String[] titles;
        private int[] icons;
        private Map<String, Float> date;

        InfoGridViewAdapter(int[] icons, Map<String, Float> date, String[] titles) {
            this.icons = icons;
            this.date = date;
            this.titles = titles;
        }

        public void setScore(int index, float value) {
            date.put(titles[index], value);
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
            score.setText(mData.get(titles[i]) + "");
            return inflate;
        }
    }
}
