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
import android.widget.ListView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BeanInfo;
import com.dhy.coffeesecret.ui.container.BeanInfoActivity;
import com.dhy.coffeesecret.ui.container.adapters.BeanListAdapter;

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
    private ArrayList<BeanInfo> beanInfos;
    private ArrayList<BeanInfo> beanInfoTemp;

    private static final String TAG = "SearchFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        searchView = inflater.inflate(R.layout.fragment_search, container, false);
        beanInfos = new ArrayList<>();
        beanInfoTemp = (ArrayList<BeanInfo>) getArguments().getSerializable("beanList");
        mContext = getContext();

        return searchView;
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
        initSearchList();
    }

    private void initSearchList() {

        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        beanListAdapter = new BeanListAdapter(mContext, beanInfos, new BeanListAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                Intent intent = new Intent(mContext, BeanInfoActivity.class);
                intent.putExtra("beanInfo", "beanInfo");
                Log.i(TAG, "onItemClicked: position = " + position);
                startActivity(intent);
            }
        });

        searchList.setLayoutManager(manager);
        searchList.setAdapter(beanListAdapter);
    }

    private void initCancel() {
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
                FragmentTransaction tx = getFragmentManager().beginTransaction();
                tx.hide(SearchFragment.this);
                tx.commit();
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
                msg.what = GET_LIKE_BEAN_LIST;
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
                    default:
                        break;
                }
            }
    }

}
