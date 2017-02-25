package com.dhy.coffeesecret.ui.device;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BeanInfo;

import java.util.ArrayList;
import java.util.List;

public class DialogBeanSelectedActivity extends AppCompatActivity {
    private ListView mListView;
    private List<BeanInfo> mBeanInfos;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_bean_selected);
        mListView = (ListView) findViewById(R.id.dialog_bean_selected);
        initBeanInfos();
        mListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mBeanInfos.size();
            }

            @Override
            public Object getItem(int position) {
                return mBeanInfos.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(DialogBeanSelectedActivity.this).inflate(R.layout.item_bean_list, parent, false);
                    holder = new ViewHolder();
                    holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                    holder.textView = (TextView) convertView.findViewById(R.id.beanWeight);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.imageView.setImageResource(R.mipmap.ic_launcher);
                holder.textView.setText("55");
                return convertView;
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("beanInfo", mBeanInfos.get(position));
                setResult(7, intent);
                finish();
            }
        });
    }

    private void initBeanInfos() {
        if (mBeanInfos == null) {
            mBeanInfos = new ArrayList<>();
        }
        // 此处调用模型，获取正儿八经的数据


        // 此处假数据
        BeanInfo beanInfo = new BeanInfo();
        beanInfo.setName("巴西黄波旁");
        beanInfo.setSpecies("Bourbon");
        mBeanInfos.add(beanInfo);
    }

    class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}
