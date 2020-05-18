package com.androidcorpo.lindapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidcorpo.lindapp.Constant;
import com.androidcorpo.lindapp.R;
import com.androidcorpo.lindapp.fragments.MessagesFragment.OnListFragmentInteractionListener;
import com.androidcorpo.lindapp.model.MessageDetailContent.MessageDetailItem;

import java.text.SimpleDateFormat;
import java.util.List;

public class MyMessagesRecyclerViewAdapter extends RecyclerView.Adapter<MyMessagesRecyclerViewAdapter.ViewHolder> {

    private final List<MessageDetailItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyMessagesRecyclerViewAdapter(List<MessageDetailItem> items, OnListFragmentInteractionListener listener,Context context) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        SimpleDateFormat dtFormat = new SimpleDateFormat(Constant.DATE_PATTERN);
        holder.mItem = mValues.get(position);
        String displayName = mValues.get(position).getContactName()==null ? mValues.get(position).getNumberName() : mValues.get(position).getContactName();
        holder.name.setText(displayName);
        String msg = mValues.get(position).getLatestMessage();
        String from = mValues.get(position).getNumberName();
/*
        if (BinaryConversions.isHexNumber(msg))
            msg = LindAppUtils.decryptCypherText(context,msg,from);
*/
        holder.latest_message.setText(msg);
        holder.list_image.setText(displayName.charAt(0) + "");
        holder.time.setText(dtFormat.format(mValues.get(position).getTime()));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final TextView name;
        private final TextView list_image;
        private final TextView latest_message;
        private final TextView time;
        private MessageDetailItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            name = view.findViewById(R.id.name);
            list_image = view.findViewById(R.id.list_image);
            latest_message = view.findViewById(R.id.latest_message);
            time = view.findViewById(R.id.time);
        }

    }
}
