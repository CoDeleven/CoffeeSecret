package com.dhy.coffeesecret.ui.common;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.dhy.coffeesecret.ui.common.interfaces.OnItemClickListener;

import butterknife.Bind;
import butterknife.ButterKnife;


public abstract class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";
    @Bind(R.id.id_search_edit)
    EditText editText;
    @Bind(R.id.id_btn_cancel)
    Button cancel;
    @Bind(R.id.id_search_clear)
    ImageButton clear;
    @Bind(R.id.search_list)
    RecyclerView searchList;
    private View searchView;
    private InputMethodManager imm;
    private Context mContext;
    // private SearchHandler mHandler = new SearchHandler(this);
    private OnSearchCallBack onSearchCallBack;
    private OnItemClickListener resultClickListenr;
    private boolean isRemoved = false;
    private Handler mThreadHandler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        searchView = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, searchView);
        mContext = getContext();
        Bundle bundle = getArguments();
        initData(bundle);
        return searchView;
    }

    public OnItemClickListener getResultClickListenr() {
        return resultClickListenr;
    }

    protected abstract void initData(Bundle bundle);/* {
        if (entrance.equals("search_bean")) {
            beanInfos = new ArrayList<>();
            beanInfoTemp = (ArrayList<BeanInfo>) bundle.getSerializable("beanList");

            beanListAdapter = new BeanListAdapter(mContext, beanInfos, new BeanListAdapter.OnItemClickListener() {
                @Override
                public void onItemClicked(int position) {
                    Intent intent = new Intent(mContext, BeanInfoActivity.class);
                    intent.putExtra("beanInfo", beanInfos.get(position));
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
                    ((MyApplication) (getContext().getApplicationContext())).setBakeReport(report);
                    remove();
                    Intent intent = new Intent(getContext(), ReportActivity.class);
                    startActivity(intent);
                }
            });
        } else if (entrance.equals("search_info")) {
            infos = new ArrayList<>();
            infoTemp = bundle.getStringArrayList("infoList");

            if (infoTemp != null) {
                for (String info : infoTemp) {
                    Log.i(TAG, "initData: " + info);
                }
            }

            infoListAdapter = new InfoListAdapter(mContext, infos, new InfoListAdapter.OnInfoListClickListener() {
                @Override
                public void onInfoClicked(String item) {
                    onSearchCallBack.onSearchCallBack(item);
                }
            });
        } else if (entrance.equals("search_cupping")) {
            cuppingInfos = new ArrayList<>();
            cuppingInfosTemp = (ArrayList<CuppingInfo>) bundle.getSerializable("cuppingInfos");
            cuppingListAdapter = new CuppingListAdapter(mContext, cuppingInfos);
            cuppingListAdapter.setOnItemClickListener(new CuppingListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    remove();
                    if(resultClickListenr != null){
                        resultClickListenr.onItemClick(cuppingInfos.get(position));
                    }
                }
            });
        }else if (entrance.equals("select_line")) {
            bakeReports = new ArrayList<>();
            bakeReportTemp = (ArrayList<BakeReport>) bundle.getSerializable("reportList");
            lineListAdapter = new LineListAdapter(mContext, bakeReports, new LineListAdapter.OnItemClickListener() {
                @Override
                public void onItemClicked(int position, BakeReport report) {
                    remove();
                    resultClickListenr.onItemClick(report);
                }
            });
        }

    }*/



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        initCancel();
        initClear();
        initEditText();

/*        if (entrance.equals("search_bean")) {
            initSearchList(beanListAdapter);
        } else if (entrance.equals("search_line")) {
            initSearchList(lineListAdapter);
        } else if (entrance.equals("search_info")) {
            initSearchList(infoListAdapter);
        } else if (entrance.equals("search_cupping")) {
            initSearchList(cuppingListAdapter);
        }*/
    }

    protected void initSearchList(RecyclerView.Adapter adapter) {

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
/*
                Message msg = new Message();
                switch (entrance) {
                    case "search_bean":
                        msg.what = GET_LIKE_BEAN_LIST;
                        break;
                    case "search_line":
                        msg.what = GET_LIKE_LINE_LIST;
                        break;
                    case "search_info":
                        msg.what = GET_LIKE_INFO_LIST;
                        break;
                    case "search_cupping":
                        msg.what = GET_LIKE_CUPPING_LIST;
                        break;
                }*/
                /*msg.obj = searchText;
                mHandler.sendMessage(msg);*/
                handleDataBySearchKey(searchText);

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
        tx.setCustomAnimations(R.anim.in_from_left, R.anim.out_to_right);
        tx.remove(SearchFragment.this);

        tx.commit();
        isRemoved = true;
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

    }

    public boolean isRemoved() {
        return isRemoved;
    }

    @Override
    public void onStart() {
        super.onStart();
        editText.requestFocus();
        // toolbar.setVisibility(View.GONE);
        if (imm == null) {
            imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
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

    public void addOnSearchCallBack(OnSearchCallBack onSearchCallBack) {
        this.onSearchCallBack = onSearchCallBack;
    }

    public void setOnResultClickListenr(OnItemClickListener listenr) {
        this.resultClickListenr = listenr;
    }


    protected abstract void handleDataBySearchKey(String searchKey);

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /*public interface OnResultClickListenr {
        void onItemClick(Serializable serializable);
    }*/


    /*private class SearchHandler extends Handler {

        private final WeakReference<SearchFragment> mActivity;

        public SearchHandler(SearchFragment activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final SearchFragment activity = mActivity.get();

            String msgObj = ((String) msg.obj).toLowerCase();
            switch (msg.what) {
                case GET_LIKE_BEAN_LIST:
                    if (activity.beanInfoTemp != null) {
                        beanInfos.clear();

                        for (BeanInfo beanInfo : activity.beanInfoTemp) {
                            if (beanInfo.getArea().toLowerCase().contains(msgObj)) {
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
                            if (bakeReport.getDate().toLowerCase().contains(msgObj)) {
                                activity.bakeReports.add(bakeReport);
                            }
                        }

                        activity.lineListAdapter.notifyDataSetChanged();
                    }
                    break;
                case GET_LIKE_INFO_LIST:
                    if (activity.infoTemp != null) {
                        infos.clear();

                        for (String string : activity.infoTemp) {
                            if (string.toLowerCase().contains(msgObj)) {
                                activity.infos.add(string);
                            }
                        }
                        activity.infoListAdapter.notifyDataSetChanged();
                    }
                    break;
                case GET_LIKE_CUPPING_LIST:
                    if (cuppingInfosTemp != null) {
                        cuppingInfos.clear();
                        for (CuppingInfo cuppingInfo : cuppingInfosTemp) {
                            if (cuppingInfo.getName().toLowerCase().contains(msgObj)) {
                                cuppingInfos.add(cuppingInfo);
                            }
                        }
                        cuppingListAdapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }
        }
    }*/

    protected void runOnMainThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            mThreadHandler.post(runnable);
        }
    }

    public interface OnSearchCallBack {
        void onSearchCallBack(String info);
    }
}
