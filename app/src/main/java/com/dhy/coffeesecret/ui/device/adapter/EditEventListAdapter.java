package com.dhy.coffeesecret.ui.device.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.dhy.coffeesecret.R;
import com.github.mikephil.charting.data.Entry;

import java.util.List;

/**
 * Created by CoDeleven on 17-3-9.
 */

public class EditEventListAdapter extends RecyclerView.Adapter<EditEventListAdapter.EventViewHolder> {
    private List<Entry> entries;
    private LayoutInflater inflater;
    private Context context;
    private AlertDialog dialog;
    public EditEventListAdapter(Context context, List<Entry> entries) {
        this.entries = entries;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EventViewHolder(inflater.inflate(R.layout.editor_event_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final EventViewHolder holder, final int position) {
        float time = entries.get(position).getX();

        int minutes = (int) (time / 60);
        int seconds = (int) (time % 60);
        holder.timeNode.setText(String.format("%1$02d", minutes) + ":" + String.format("%1$02d", seconds));
        holder.eventEditor.setText(entries.get(position).getEvent().getDescription() + "");
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("事件补充");
                final EditText editText = new EditText(context);
                editText.setHint(entries.get(position).getEvent().getDescription());
                builder.setView(editText);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        holder.eventEditor.setText(editText.getText().toString());
                        entries.get(position).getEvent().setDescription(editText.getText().toString());
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog = builder.show();
            }
        });
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        TextView timeNode;
        TextView eventEditor;
        View linearLayout;

        public EventViewHolder(View itemView) {
            super(itemView);
            linearLayout = itemView;
            timeNode = (TextView) itemView.findViewById(R.id.id_edit_event_item_timeNode);
            eventEditor = (TextView) itemView.findViewById(R.id.id_edit_event_item_event);
        }
    }
}
