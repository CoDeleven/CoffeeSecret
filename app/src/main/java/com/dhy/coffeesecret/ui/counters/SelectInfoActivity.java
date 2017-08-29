package com.dhy.coffeesecret.ui.counters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.quicksidebar.QuickSideBarTipsView;
import com.bigkoo.quicksidebar.QuickSideBarView;
import com.bigkoo.quicksidebar.listener.OnQuickSideBarTouchListener;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.Species;
import com.dhy.coffeesecret.ui.common.SearchFragment;
import com.dhy.coffeesecret.ui.common.views.DividerDecoration;
import com.dhy.coffeesecret.ui.common.views.SearchEditText;
import com.dhy.coffeesecret.ui.counters.adapters.InfoListAdapter;
import com.dhy.coffeesecret.ui.counters.fragments.SearchPlaceFragment;
import com.dhy.coffeesecret.utils.T;
import com.dhy.coffeesecret.utils.TestData;
import com.dhy.coffeesecret.utils.Utils;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectInfoActivity extends AppCompatActivity implements OnQuickSideBarTouchListener, SearchEditText.SearchBarListener {

    private static final int GET_COUNTRY_LIST = 111;
    private static final int GET_AREA_LIST = 222;
    private static final int GET_MANOR_LIST = 333;
    private static final int GET_SPECIES_LIST = 444;
    private static final int SHOW_WRONG_TOAST = 555;
    private static final String TAG = "SelectInfoActivity";
    @Bind(R.id.btn_cancel)
    TextView btnCancel;
    @Bind(R.id.title_text)
    TextView titleText;
    @Bind(R.id.btn_save)
    TextView btnSave;
    @Bind(R.id.search_edit)
    SearchEditText searchEdit;
    @Bind(R.id.info_list)
    RecyclerView infoList;
    @Bind(R.id.quickSideBarTipsView)
    QuickSideBarTipsView quickSideBarTipsView;
    @Bind(R.id.quickSideBarView)
    QuickSideBarView quickSideBarView;
    @Bind(R.id.activity_select_info)
    RelativeLayout activitySelectInfo;
    private Context context;
    private String infoType;
    private boolean isAddSearchFragment = false;
    private SearchFragment searchFragment;
    private ArrayList<String> dataList = null;
    private ArrayList<Species> speciesList = null;
    private InfoListAdapter infoAdapter = null;
    private HashMap<String, Integer> letters = new HashMap<>();
    private SelectHandler mHandler = new SelectHandler(SelectInfoActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_info);
        ButterKnife.bind(this);

        context = SelectInfoActivity.this;
        dataList = new ArrayList<>();
        speciesList = new ArrayList<>();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initView();
        initInfoList();
    }

    private void initInfoList() {
        infoType = getIntent().getStringExtra("info_type");

        switch (infoType) {
            case "country":
                titleText.setText("国家");
                mHandler.sendEmptyMessage(GET_COUNTRY_LIST);
                break;
            case "area":
                titleText.setText("产地");
                mHandler.sendEmptyMessage(GET_AREA_LIST);
                break;
            case "manor":
                titleText.setText("庄园");
                mHandler.sendEmptyMessage(GET_MANOR_LIST);
                break;
            case "species":
                titleText.setText("豆种");
                mHandler.sendEmptyMessage(GET_SPECIES_LIST);
                break;
            default:
                mHandler.sendEmptyMessage(SHOW_WRONG_TOAST);
                break;
        }
    }

    private void initView() {
        btnSave.setVisibility(View.GONE);

        searchEdit.setSearchBarListener(this);

        LinearLayoutManager manager = new LinearLayoutManager(SelectInfoActivity.this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        infoAdapter = new InfoListAdapter(context, speciesList, dataList, new InfoListAdapter.OnInfoListClickListener() {
            @Override
            public void onInfoClicked(String item) {
                exitToRight(item);
            }
        });

        infoList.setLayoutManager(manager);
        infoList.setAdapter(infoAdapter);

        StickyRecyclerHeadersDecoration headersDecoration = new StickyRecyclerHeadersDecoration(infoAdapter);
        infoList.addItemDecoration(headersDecoration);
        infoList.addItemDecoration(new DividerDecoration(context));

        quickSideBarView.setOnQuickSideBarTouchListener(this);
    }

    @Override
    public void onLetterChanged(String letter, int position, float y) {
        quickSideBarTipsView.setText(letter, position, y);
        //有此key则获取位置并滚动到该位置
        if (letters.containsKey(letter)) {
            infoList.scrollToPosition(letters.get(letter));
        }
    }

    @Override
    public void onLetterTouching(boolean touching) {
        //可以自己加入动画效果渐显渐隐
        quickSideBarTipsView.setVisibility(touching ? View.VISIBLE : View.INVISIBLE);
    }

    private void getDataList(int listType) {
        dataList.clear();
        switch (listType) {
            case GET_COUNTRY_LIST:
                Collections.addAll(dataList, getResources().getStringArray(R.array.all));
                break;
            case GET_AREA_LIST:
                // TODO 地区
                Collections.addAll(dataList, TestData.beanList1);
                break;
            case GET_MANOR_LIST:
                // TODO 庄园
                Collections.addAll(dataList, TestData.beanList2);
                break;
            case GET_SPECIES_LIST:
                for (String oneSpecies : TestData.beanList4) {
                    switch (oneSpecies) {
                        case "Arabica":
                            for (String spec : getResources().getStringArray(R.array.arabica)) {
                                Species species = new Species();
                                species.setOneSpecies(oneSpecies);
                                species.setSpecies(spec);
                                speciesList.add(species);
                            }
                            break;
                        case "Robusta":
                            for (String spec : getResources().getStringArray(R.array.robusta)) {
                                Species species = new Species();
                                species.setOneSpecies(oneSpecies);
                                species.setSpecies(spec);
                                speciesList.add(species);
                            }
                            break;
                        case "Liberica":
                            for (String spec : getResources().getStringArray(R.array.liberica)) {
                                Species species = new Species();
                                species.setOneSpecies(oneSpecies);
                                species.setSpecies(spec);
                                speciesList.add(species);
                            }
                            break;
                    }
                }
                break;
            default:
                break;
        }
        /* 原处理方式长达5s，对于双循环的算法hold不住
         * 考虑其不变形，固在排序处理后加入arrays.xml中
         */
        // dataList = sortByInfo(dataList);
        getLetters(dataList);
        // speciesList = sortBySpecies(speciesList);
        getLetters(getStrings(speciesList));
        infoAdapter.notifyDataSetChanged();
    }

    /**
     * 将传出的 list 中需要排序的数据传入到另一个 list 中
     *
     * @param list
     * @return
     */
    private ArrayList<Species> sortBySpecies(ArrayList<Species> list) {
        ArrayList<String> infos = getStrings(list);
        ArrayList<Species> specieses = new ArrayList<>();

        infos = sortByInfo(infos);

        for (int i = 0; i < infos.size(); i++) {
            list.get(i).setSpecies(infos.get(i));
        }

        return list;
    }

    @NonNull
    private ArrayList<String> getStrings(ArrayList<Species> list) {
        ArrayList<String> infos = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            Species species = list.get(i);
            // 放入大类的首字母标识符
            infos.add(species.getOneSpecies().substring(0, 1) + species.getSpecies());
        }
        return infos;
    }

    private ArrayList<String> sortByInfo(ArrayList<String> infos) {

        for (int i = infos.size() - 1; i > 0; --i) {
            long startTime = System.currentTimeMillis();

            for (int j = 0; j < i; ++j) {
                char a = Utils.getFirstPinYinLetter(infos.get(j + 1)).charAt(0);
                char b = Utils.getFirstPinYinLetter(infos.get(j)).charAt(0);

                if (a < b) {
                    String s = infos.get(j);
                    infos.set(j, infos.get(j + 1));
                    infos.set(j + 1, s);
                } else if (a == b) {
                    char c = Utils.getFirstPinYinLetter(infos.get(j + 1)).charAt(1);
                    char d = Utils.getFirstPinYinLetter(infos.get(j)).charAt(1);
                    if (c < d) {
                        String s = infos.get(j);
                        infos.set(j, infos.get(j + 1));
                        infos.set(j + 1, s);
                    }
                }
            }
            Log.e("getFirstPinYinLetter" + i + ":" , System.currentTimeMillis() - startTime + "");

        }

        return infos;
    }

    private void getLetters(ArrayList<String> infos) {

        int i = 0;

        for (String s : infos) {
            String letter = Utils.getFirstPinYinLetter(s).substring(0, 1);
            if (!letters.containsKey(letter)) {
                letters.put(letter, i);
            }
            i++;
        }

    }

    @Override
    public void startSearchPage() {
        //TODO  类似这个的代码需要重构一遍

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left);

        if (searchFragment != null) {
            isAddSearchFragment = !searchFragment.isRemoved();
        }
        if (!isAddSearchFragment) {
            searchFragment = new SearchPlaceFragment();
            Bundle bundle = new Bundle();
            if (dataList.size() > 0) {
                bundle.putStringArrayList("infoList", dataList);
            } else if (speciesList.size() > 0) {
                bundle.putStringArrayList("infoList", getStrings(speciesList));
            }
            searchFragment.setArguments(bundle);
            searchFragment.addOnSearchCallBack(new SearchFragment.OnSearchCallBack() {
                @Override
                public void onSearchCallBack(String info) {
                    exitToRight(info);
                }

            });
            tx.add(R.id.activity_select_info, searchFragment, "search_info");
            isAddSearchFragment = true;
        } else {
            tx.show(searchFragment);
        }
        tx.commit();
    }

    @OnClick(R.id.btn_cancel)
    public void onClick() {
        exitToRight();
    }

    private void exitToRight(String info) {
        Intent intent = new Intent();
        intent.putExtra("info", info);
        setResult(RESULT_OK, intent);
        exitToRight();
    }

    private void exitToRight() {
        this.finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    @Override
    public void onBackPressed() {
        if (searchFragment != null) {
            isAddSearchFragment = !searchFragment.isRemoved();
        }
        if (isAddSearchFragment) {
            searchFragment.remove();
            isAddSearchFragment = false;
        } else {
            exitToRight();
        }
    }

    private class SelectHandler extends Handler {
        private final WeakReference<SelectInfoActivity> mActivity;

        public SelectHandler(SelectInfoActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final SelectInfoActivity activity = mActivity.get();
            switch (msg.what) {
                case GET_COUNTRY_LIST:
                    activity.getDataList(GET_COUNTRY_LIST);
                    break;
                case GET_AREA_LIST:
                    activity.getDataList(GET_AREA_LIST);
                    break;
                case GET_MANOR_LIST:
                    activity.getDataList(GET_MANOR_LIST);
                    break;
                case GET_SPECIES_LIST:
                    activity.getDataList(GET_SPECIES_LIST);
                    break;
                case SHOW_WRONG_TOAST:
                    T.showShort(activity.context, "网络连接失败");
                default:
                    break;
            }
        }
    }
}
