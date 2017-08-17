package com.dhy.coffeesecret.ui.device.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dhy.coffeesecret.MyApplication;
import com.dhy.coffeesecret.R;
import com.dhy.coffeesecret.pojo.BeanInfoSimple;

import java.util.List;

/**
 * Created by CoDeleven on 17-3-23.
 */

public class BeanSelectAdapter extends RecyclerView.Adapter<BeanSelectAdapter.BeanSelectHandler> {
    private List<BeanInfoSimple> infoSimple;
    private Context mContext;
    private MyButtonOnClick myButtonOnClick;

    public BeanSelectAdapter(Context context, List<BeanInfoSimple> infoSimple) {
        this.infoSimple = infoSimple;
        this.mContext = context;
    }

    @Override
    public BeanSelectHandler onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.bake_dialog_item, null, false);
        return new BeanSelectHandler(view);
    }


    @Override
    public void onBindViewHolder(final BeanSelectHandler holder, final int position) {
        if(holder.beanWeight.getTag() == null){
            holder.beanWeight.setTag(position);
        }
        final BeanInfoSimple simple = infoSimple.get(position);
        holder.beanName.setText(simple.getBeanName());
        holder.beanName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myButtonOnClick.onNameClick(position);
            }
        });
        holder.beanWeight.setText(simple.getUsage());
        holder.beanIndex.setText(position + "");
        holder.weightUnit.setText(MyApplication.weightUnit);

        // 因为每次修改一种豆种信息时会引起其他豆种信息的刷新
        // 并且都调用了onAfterTextChanged()方法。导致数据设置问题
        // 故在每次获取焦点的时候设置TextWatcher,失去焦点再删除TextWatcher
        holder.beanWeight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    TextWatcherListener listener = new TextWatcherListener(simple);
                    holder.beanWeight.addTextChangedListener(listener);
                    holder.beanWeight.setTag(listener);
                }else{
                    holder.beanWeight.removeTextChangedListener((TextWatcherListener)holder.beanWeight.getTag());
                }
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myButtonOnClick.onDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return infoSimple.size();
    }

    public void setMyButtonOnClick(MyButtonOnClick myButtonOnClick) {
        this.myButtonOnClick = myButtonOnClick;
    }

    public interface MyButtonOnClick {
        void onNameClick(int position);

        void onDeleteClick(int position);
    }

    class BeanSelectHandler extends RecyclerView.ViewHolder {
        private TextView beanIndex;
        private TextView beanName;
        private EditText beanWeight;
        private ImageView delete;
        private TextView weightUnit;
        public BeanSelectHandler(View itemView) {
            super(itemView);
            beanName = (TextView) itemView.findViewById(R.id.id_bake_dialog_beanName);
            beanWeight = (EditText) itemView.findViewById(R.id.id_bake_dialog_beanWeight);
            delete = (ImageView) itemView.findViewById(R.id.id_bake_dialog_delete);
            beanIndex = (TextView)itemView.findViewById(R.id.id_dialog_beanIndex);
            weightUnit = (TextView)itemView.findViewById(R.id.id_bake_dialog_unit);
        }
    }
    class TextWatcherListener implements TextWatcher{
        private final BeanInfoSimple simple;

        public TextWatcherListener(BeanInfoSimple simple) {
            this.simple = simple;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            simple.setUsage(s.toString());
        }
    }
}
