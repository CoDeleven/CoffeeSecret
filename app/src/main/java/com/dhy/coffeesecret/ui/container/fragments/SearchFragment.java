package com.dhy.coffeesecret.ui.container.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BakeReport;
import com.dhy.coffeesecret.pojo.BeanInfo;
import com.dhy.coffeesecret.ui.container.BeanInfoActivity;
import com.dhy.coffeesecret.ui.container.adapters.BeanListAdapter;
import com.dhy.coffeesecret.ui.container.adapters.LineListAdapter;
import com.dhy.coffeesecret.ui.device.fragments.ReportActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class SearchFragment extends Fragment {

    private View searchView;

    private EditText editText;
    private Button cancel;
    private ImageButton clear;
    private RecyclerView searchList;
    private InputMethodManager imm;

    private Context mContext;
    private BeanListAdapter beanListAdapter;
    private LineListAdapter lineListAdapter;
    private ArrayList<BeanInfo> beanInfos;
    private ArrayList<BeanInfo> beanInfoTemp;
    private ArrayList<BakeReport> bakeReports;
    private ArrayList<BakeReport> bakeReportTemp;
    private String entrance;

    private static final String TAG = "SearchFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        searchView = inflater.inflate(R.layout.fragment_search, container, false);

        mContext = getContext();
        entrance = getTag();
        Bundle bundle = getArguments();

        initData(bundle);

        return searchView;
    }

    private void initData(Bundle bundle) {
        if (entrance.equals("search_bean")) {
            beanInfos = new ArrayList<>();
            beanInfoTemp = (ArrayList<BeanInfo>) bundle.getSerializable("beanList");

            beanListAdapter = new BeanListAdapter(mContext, beanInfos, new BeanListAdapter.OnItemClickListener() {
                @Override
                public void onItemClicked(int position) {
                    Intent intent = new Intent(mContext, BeanInfoActivity.class);
                    intent.putExtra("beanInfo", "beanInfo");
                    remove();
                    startActivity(intent);
                }
            });

        } else if (entrance.equals("search_line")) {
            bakeReports = new ArrayList<>();
            bakeReportTemp = (ArrayList<BakeReport>) bundle.getSerializable("reportList");

            lineListAdapter = new LineListAdapter(mContext, bakeReports, new LineListAdapter.OnItemClickListener() {
                @Override
                public void onItemClicked(int position, BakeReport report) {
                    Intent intent = new Intent(mContext, ReportActivity.class);
                    intent.putExtra("bakeReport", bakeReports);
                    remove();
                    startActivity(intent);
                }
            });
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        editText = (EditText) searchView.findViewById(R.id.id_search_edit);
        cancel = (Button) searchView.findViewById(R.id.id_btn_cancel);
        clear = (ImageButton) searchView.findViewById(R.id.id_search_clear);
        searchList = (RecyclerView) searchView.findViewById(R.id.search_list);

        initCancel();
        initClear();
        initEditText();

        if (entrance.equals("search_bean")) {
            initSearchList(beanListAdapter);
        } else if (entrance.equals("search_line")) {
            initSearchList(lineListAdapter);
        }
    }

    private void initSearchList(RecyclerView.Adapter adapter) {

        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        searchList.setLayoutManager(manager);
        searchList.setAdapter(adapter);
    }

    private void initCancel() {
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
                remove();
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

    private void initEditText() {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    clear.setVisibility(View.VISIBLE);
                } else {
                    clear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.i(TAG, "afterTextChanged: " + System.currentTimeMillis());
                String searchText = editable.toString();

                Message msg = new Message();
                msg.what = entrance.equals("search_bean")?GET_LIKE_BEAN_LIST: GET_LIKE_LINE_LIST;
                msg.obj = searchText;
                mHandler.sendMessage(msg);

                Log.i(TAG, "afterTextChanged: " + System.currentTimeMillis());
            }
        });
    }

    private void initClear() {
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
            }
        });
    }

    public void remove() {
        FragmentTransaction tx = getFragmentManager().beginTransaction();
        tx.remove(SearchFragment.this);
        tx.commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        editText.requestFocus();
        // toolbar.setVisibility(View.GONE);
        if (imm == null) {
            imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            editText.requestFocus();
            imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
        }
    }

    private static final int GET_LIKE_BEAN_LIST = 111;
    private static final int GET_LIKE_LINE_LIST = 222;
    private SearchHandler mHandler = new SearchHandler(this);

    private class SearchHandler extends Handler {

        private final WeakReference<SearchFragment> mActivity;

        public SearchHandler(SearchFragment activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final SearchFragment activity = mActivity.get();
            switch (msg.what) {
                case GET_LIKE_BEAN_LIST:
                    if (activity.beanInfoTemp != null) {
                        beanInfos.clear();

                        for (BeanInfo beanInfo : activity.beanInfoTemp) {
                            if (beanInfo.getName().contains((String) msg.obj)) {
                                activity.beanInfos.add(beanInfo);
                            }
                        }

                        activity.beanListAdapter.notifyDataSetChanged();
                    }
                    break;
                case GET_LIKE_LINE_LIST:
                    if (activity.bakeReportTemp != null) {
                        bakeReports.clear();

                        String bakeDate = null;
                        for (BakeReport bakeReport : activity.bakeReportTemp) {
                            bakeDate = String.format("%1$tY-%1$tm-%1$te", bakeReport.getBakeDate());
                            if (bakeDate.contains((String) msg.obj)) {
                                activity.bakeReports.add(bakeReport);
                            }
                        }

                        activity.lineListAdapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: ");
    }
}
