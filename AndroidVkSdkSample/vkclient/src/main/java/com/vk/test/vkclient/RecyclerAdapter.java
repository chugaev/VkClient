package com.vk.test.vkclient;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.perm.kate.api.Message;

import java.util.ArrayList;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<Message> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.test_msg);
        }
    }

    public RecyclerAdapter(ArrayList<Message> dataset) {
        mDataset = dataset;
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.mTextView.getLayoutParams();
        Message message = mDataset.get(position);
        if (message.is_out) {
            params.gravity = Gravity.RIGHT;
            holder.mTextView.setLayoutParams(params);
            holder.mTextView.setBackgroundResource(R.drawable.bg_msg_from);
        } else {
            params.gravity = Gravity.LEFT;
            holder.mTextView.setBackgroundResource(R.drawable.bg_msg_to);
            holder.mTextView.setLayoutParams(params);
        }
        holder.mTextView.setText(mDataset.get(position).body);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}