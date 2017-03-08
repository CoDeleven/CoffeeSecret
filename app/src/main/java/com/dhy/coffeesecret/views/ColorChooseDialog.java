package com.dhy.coffeesecret.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dhy.coffeesecret.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

/**
 * CoffeeSecret
 * Created by Simo on 2017/3/4.
 */

public class ColorChooseDialog {

    private static AlertDialog dialog;

    private ColorChooseDialog() {

    }

    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public static class Builder extends AlertDialog.Builder {
        private Context context = null;

        public Builder(Context context) {
            super(context);
            this.context = context;
        }

        public Builder setView(List<Integer> items, OnItemClickListener onItemClickListener) {

            View view = LayoutInflater.from(context).inflate(R.layout.dialog_choose_color, null);

            RecyclerView colorsView = (RecyclerView) view.findViewById(R.id.colors_view);

            LinearLayoutManager manager = new LinearLayoutManager(context);
            manager.setOrientation(LinearLayoutManager.VERTICAL);

            ColorsAdapter adapter = new ColorsAdapter(context, items, onItemClickListener);
            colorsView.setLayoutManager(manager);
            colorsView.setAdapter(adapter);

            return (Builder) super.setView(view);
        }

        public ColorChooseDialog createDialog() {
            dialog = super.create();

            return new ColorChooseDialog();
        }
    }

    static class ColorsAdapter extends RecyclerView.Adapter {

        private Context context;
        private List<Integer> colors;
        private LayoutInflater mLayoutInflater;
        private OnItemClickListener onItemClickListener;

        public ColorsAdapter(Context context, List<Integer> colors, OnItemClickListener onItemClickListener) {
            this.context = context;
            this.colors = colors;
            this.onItemClickListener = onItemClickListener;

            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(mLayoutInflater.inflate(R.layout.item_color_choose, null));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            final ViewHolder vh = (ViewHolder) holder;
            vh.layoutColor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(vh.getAdapterPosition());
                }
            });

            vh.colorPreviewChoose.setImageResource(colors.get(position));

            vh.icSelected.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        }

        @Override
        public int getItemCount() {
            return colors.size();
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout layoutColor;
        private RoundedImageView colorPreviewChoose;
        private ImageView icSelected;

        public ViewHolder(View itemView) {
            super(itemView);

            layoutColor = (RelativeLayout) itemView.findViewById(R.id.layout_color);
            colorPreviewChoose = (RoundedImageView) itemView.findViewById(R.id.color_preview_choose);
            icSelected = (ImageView) itemView.findViewById(R.id.ic_selected);
        }
    }

}
