package com.dhy.coffeesecret.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.Global;
import com.dhy.coffeesecret.pojo.LinesColor;
import com.dhy.coffeesecret.ui.mine.adapter.LinesColorAdapter;
import com.dhy.coffeesecret.utils.SPPrivateUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LinesColorActivity extends AppCompatActivity {

    @Bind(R.id.btn_back)
    ImageView btnBack;
    @Bind(R.id.title_text)
    TextView titleText;
    @Bind(R.id.color_packages)
    RecyclerView colorPackages;

    private Context context;
    private List<LinesColor> linesColorList;
    private LinesColorAdapter linesColorAdapter;

    private static final String TAG = "LinesColorActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lines_color);
        ButterKnife.bind(this);

        context = LinesColorActivity.this;

        init();
    }

    private void init() {

        titleText.setText("颜色选择");

        getColorsPackages();

        linesColorAdapter = new LinesColorAdapter(context, linesColorList, getFootView());

        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        colorPackages.setLayoutManager(manager);
        colorPackages.setAdapter(linesColorAdapter);

    }

    @OnClick(R.id.btn_back)
    public void onClick() {
        this.onBackPressed();
    }

    private View getFootView() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_lines_color_foot, null);
        RelativeLayout customPackage = (RelativeLayout) view.findViewById(R.id.custom_package);
        TextView textView = (TextView) view.findViewById(R.id.add_text);
        textView.setText("自定义曲线");
        customPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CustomPackageActivity.class);
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });
        return view;
    }

    private void getColorsPackages() {

        linesColorList = new ArrayList<>();

        LinesColor linesColor = new LinesColor();
        linesColor.setPackageName("海洋");
        linesColor.setBeanColor("#FFFFFF");
        linesColor.setInwindColor("#F0F0F0");
        linesColor.setOutwindColor("#F0F0F0");
        linesColor.setAccBeanColor("#FFFFF0");
        linesColor.setAccInwindColor("#00FFFF");
        linesColor.setAccOutwindColor("#0000FF");
        linesColor.setEnvColor("#000000");

        LinesColor linesColor2 = new LinesColor();
        linesColor2.setPackageName("夕阳");
        linesColor2.setBeanColor("#000000");
        linesColor2.setInwindColor("#FFFF00");
        linesColor2.setOutwindColor("#FFF0F0");
        linesColor2.setAccBeanColor("#FFFFF0");
        linesColor2.setAccInwindColor("#00FFFF");
        linesColor2.setAccOutwindColor("#0000FF");
        linesColor2.setEnvColor("#FFFFFF");

        LinesColor linesColor3 = new LinesColor();
        linesColor3.setPackageName("大地");
        linesColor3.setBeanColor("#FFFFFF");
        linesColor3.setInwindColor("#FF00FF");
        linesColor3.setOutwindColor("#FFF0F0");
        linesColor3.setAccBeanColor("#00FF00");
        linesColor3.setAccInwindColor("#00FFFF");
        linesColor3.setAccOutwindColor("#0000FF");
        linesColor3.setEnvColor("#000000");

        linesColorList.add(linesColor);
        linesColorList.add(linesColor2);
        linesColorList.add(linesColor3);

        Gson gson = new Gson();
        String listJson = SPPrivateUtils.getString(context, Global.LINES_COLOR_PACKAGE, gson.toJson(linesColorList));

        linesColorList = gson.fromJson(listJson, new TypeToken<ArrayList<LinesColor>>() {
        }.getType());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                Log.i(TAG, "onActivityResult: resultOk");
                LinesColor linesColor = (LinesColor) data.getSerializableExtra("linesColor");
                Log.i(TAG, "onActivityResult: " + linesColor);
                linesColorList.add(linesColor);
                linesColorAdapter.notifyDataSetChanged();
                SPPrivateUtils.put(context, Global.LINES_COLOR_PACKAGE, new Gson().toJson(linesColorList));
                break;
            case RESULT_CANCELED:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }


}
