package com.dhy.coffeesecret.ui.mine;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.UniversalConfiguration;
import com.dhy.coffeesecret.ui.mine.adapter.QuickEventAdapter;
import com.dhy.coffeesecret.utils.SettingTool;
import com.dhy.coffeesecret.utils.T;
import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuickEventActivity extends AppCompatActivity {

    @Bind(R.id.btn_back)
    ImageView btnBack;
    @Bind(R.id.title_text)
    TextView titleText;
    @Bind(R.id.quick_event_list)
    RecyclerView quickEventList;
    @Bind(R.id.activity_quick_event)
    RelativeLayout activityQuickEvent;

    private Context mContext;
    private QuickEventAdapter quickEventAdapter;
    private UniversalConfiguration mConfig;
    private ArrayList<String> quickEvents;

    private static final String TAG = "QuickEventActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_event);
        ButterKnife.bind(this);

        mContext = QuickEventActivity.this;
        mConfig = SettingTool.getConfig();

        initView();
    }

    private void initView() {

        titleText.setText("快速事件");

        quickEvents = new ArrayList<>();
        quickEvents.addAll(SettingTool.parse2List(mConfig.getQuickEvents()));

        quickEventAdapter = new QuickEventAdapter(mContext, quickEvents, getFootView());
        quickEventAdapter.setOnItemClickListener(new QuickEventAdapter.onItemClickListener() {
            @Override
            public void onItemClicked(String itemString) {
                showEditDialog(itemString);
            }
        });
        quickEventAdapter.setOnDeleteClickListener(new QuickEventAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClicked(int position) {
                quickEvents.remove(position);
                quickEventAdapter.notifyDataSetChanged();
                mConfig.setQuickEvents(new Gson().toJson(quickEvents));
                SettingTool.saveConfig(mConfig);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        quickEventList.setLayoutManager(layoutManager);
        quickEventList.setAdapter(quickEventAdapter);
    }

    private View getFootView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View footView = inflater.inflate(R.layout.item_quick_event_foot, null);
        RelativeLayout addQuickEvent = (RelativeLayout) footView.findViewById(R.id.add_quick_event);
        addQuickEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog(null);
            }
        });
        return footView;
    }

    private void editQuickEvent(String newQuickEvent, String oldQuickEvent) {

        if (quickEvents.contains(newQuickEvent)) {
            T.showShort(mContext, "该事件已存在");
//            showEditDialog(oldQuickEvent);
            return;
        }

        if (oldQuickEvent == null) {
            quickEvents.add(newQuickEvent);
        } else {
            for (int i = 0; i < quickEvents.size(); i++) {
                if (quickEvents.get(i).equals(oldQuickEvent)) {
                    quickEvents.set(i, newQuickEvent);
                }
            }
        }
        saveConfig();
        quickEventAdapter.notifyDataSetChanged();
    }

    private void showEditDialog(final String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_edit_dialog, null);
        final EditText editText = (EditText) view.findViewById(R.id.edit_text);

        if (text == null) {
            builder.setTitle("添加快速事件");
            editText.setHint("在这里输入事件");
        } else {
            builder.setTitle("编辑快速事件");
            editText.setText(text);
        }
        final AlertDialog dialog = builder.setView(view)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newQuickEvent = editText.getText().toString().trim();
                        if (newQuickEvent.equals("")) {
                            T.showShort(mContext, "请输入事件的内容");
                            showEditDialog(text);
                            return;
                        }

                        editQuickEvent(editText.getText().toString().trim(), text);
                    }
                }).create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialog) {
                if (text == null) {
                    editText.setHint("在这里输入事件");
                } else {
                    editText.setText(text);
                    editText.setSelection(text.length());
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        dialog.show();
    }

    private void saveConfig() {
        Gson gson = new Gson();
        mConfig.setQuickEvents(gson.toJson(quickEvents));
        SettingTool.saveConfig(mConfig);
    }

    @OnClick(R.id.btn_back)
    public void onClick() {
        this.finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }
}
