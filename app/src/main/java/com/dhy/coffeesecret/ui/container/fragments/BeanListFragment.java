package com.dhy.coffeesecret.ui.container.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.bigkoo.quicksidebar.QuickSideBarTipsView;
import com.bigkoo.quicksidebar.QuickSideBarView;
import com.bigkoo.quicksidebar.listener.OnQuickSideBarTouchListener;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BeanInfo;
import com.dhy.coffeesecret.ui.container.BeanInfoActivity;
import com.dhy.coffeesecret.ui.container.adapters.BeanListAdapter;
import com.dhy.coffeesecret.views.DividerDecoration;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.HashMap;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * CoffeeSecret
 * Created by Simo on 2017/2/12.
 */

public class BeanListFragment extends Fragment implements OnQuickSideBarTouchListener {

    private static final String TAG = "BeanListFragment";
    private final String[] beanLst = {"All", "Asia", "Asia", "Africa", "Baby", "Central American", "Death", "Destroy"
            , "E", "Fate", "Great", "Grand", "Handsome", "I", "Joker", "Joker", "Joker", "Joker", "Joker", "Joker"
            , "Joker", "Joker", "Joker", "Joker", "Joker", "Joker", "Joker", "Joker", "Joker", "Joker", "Joker", "Joker", "Joker", "King", "Luna", "Morning", "North American"
            , "Oceania", "Other", "Person", "Queen", "Read", "Real", "Strange", "Trouble"};
    private HashMap<String, Integer> letters = new HashMap<>();

    private View beanListView;
    private LinearLayout btnCountryChoose = null;
    private LinearLayout btnScreen = null;
    private RecyclerView beanListRecycler;
    private QuickSideBarView quickSideBarView;
    private QuickSideBarTipsView quickSideBarTipsView;
    private PopupWindow mSortPopupWindow;
    private Context context;
    private String title;
    private boolean isSortWindowShowing = false;

    public BeanListFragment() {
        super();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        initPopupWindow();
    }

    private void init() {

        btnCountryChoose = (LinearLayout) beanListView.findViewById(R.id.btn_country_choose);
        btnScreen = (LinearLayout) beanListView.findViewById(R.id.btn_screen);
        beanListRecycler = (RecyclerView) beanListView.findViewById(R.id.bean_list);
        quickSideBarView = (QuickSideBarView) beanListView.findViewById(R.id.quickSideBarView);
        quickSideBarTipsView = (QuickSideBarTipsView) beanListView.findViewById(R.id.quickSideBarTipsView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        beanListRecycler.setLayoutManager(layoutManager);

        ArrayList<BeanInfo> coffeeBeanInfos = new ArrayList<>();
        for (int i = 0; i < beanLst.length; i++) {
            BeanInfo beanInfo = new BeanInfo();
            beanInfo.setName(beanLst[i]);

            coffeeBeanInfos.add(beanInfo);

            if (!letters.containsKey(beanLst[i].substring(0, 1))) {

                letters.put(beanLst[i].substring(0, 1), i);
            }
        }
        BeanListAdapter adapter = new BeanListAdapter(context, coffeeBeanInfos, new BeanListAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                Intent intent = new Intent(context, BeanInfoActivity.class);
                intent.putExtra("beanInfo", "beanInfo");
                Log.i(TAG, "onItemClicked: position = " + position);
                startActivity(intent);
            }
        });

        beanListRecycler.setAdapter(adapter);

        StickyRecyclerHeadersDecoration headersDecoration = new StickyRecyclerHeadersDecoration(adapter);
        beanListRecycler.addItemDecoration(headersDecoration);
        beanListRecycler.addItemDecoration(new DividerDecoration(context));

        quickSideBarView.setOnQuickSideBarTouchListener(this);
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public void initPopupWindow() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        final View contentView = inflater.inflate(R.layout.ppw_conta_screen, null);

        mSortPopupWindow = new PopupWindow(contentView, MATCH_PARENT, WRAP_CONTENT);
        mSortPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mSortPopupWindow.setOutsideTouchable(true);
        mSortPopupWindow.setFocusable(true);
        btnScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSortWindowShowing) {
                    mSortPopupWindow.dismiss();
                } else {
                    mSortPopupWindow.showAsDropDown(btnScreen);
                    isSortWindowShowing = true;
                }
            }
        });

        mSortPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isSortWindowShowing = false;
            }
        });
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        beanListView = inflater.inflate(R.layout.fragment_bean_list, container, false);
        context = getActivity();
        return beanListView;
    }

    @Override
    public void onLetterChanged(String letter, int position, float y) {
        quickSideBarTipsView.setText(letter, position, y);
        //有此key则获取位置并滚动到该位置
        if (letters.containsKey(letter)) {
            beanListRecycler.scrollToPosition(letters.get(letter));
        }
    }

    @Override
    public void onLetterTouching(boolean touching) {
        //可以自己加入动画效果渐显渐隐
        quickSideBarTipsView.setVisibility(touching ? View.VISIBLE : View.INVISIBLE);
    }
}
