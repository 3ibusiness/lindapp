package com.androidcorpo.lindapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidcorpo.lindapp.Constant;
import com.androidcorpo.lindapp.LindAppUtils;
import com.androidcorpo.lindapp.R;
import com.androidcorpo.lindapp.elipticurve.BinaryConversions;
import com.androidcorpo.lindapp.fragments.ContactMesagesFragment.OnListFragmentInteractionListener;
import com.androidcorpo.lindapp.model.MessageContent.MessageItem;

import java.text.SimpleDateFormat;
import java.util.List;

public class MyContactMessagesAdapter extends RecyclerView.Adapter<MyContactMessagesAdapter.ViewHolder> {

    private final List<MessageItem> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Context context;
    private String from;

    public MyContactMessagesAdapter(List<MessageItem> items, OnListFragmentInteractionListener listener, Context context, String from) {
        mValues = items;
        mListener = listener;
        this.context = context;
        this.from = from;
    }

    private static final int MY_MESSAGE = 0, OTHER_MESSAGE = 1;

    @Override
    public int getItemViewType(int position) {
        MessageItem item = mValues.get(position);

        if (item.getIsRorS() != 1) return MY_MESSAGE;
        else return OTHER_MESSAGE;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View convertView;
        if (viewType == MY_MESSAGE) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_detail_item_r,
                    null);
        } else {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_detail_item,
                    null);
        }
        return new ViewHolder(convertView);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        SimpleDateFormat dtFormat = new SimpleDateFormat(Constant.DATE_PATTERN);
        holder.mItem = mValues.get(position);
        //MessageItem messageItem = mValues.get(position);
        String msg = mValues.get(position).getMessage();

        if (BinaryConversions.isHexNumber(msg))
            msg = LindAppUtils.decryptCypherText(context, msg, from);
        holder.message.setText(msg);
        holder.time.setText(dtFormat.format(mValues.get(position).getTime()));

        if (mValues.get(position).getIsRorS() != 1) {
            holder.linearLayout.setBackgroundResource(R.color.colorPrimary);
            holder.mView.setPadding(20, 0, 0, 0);
            //  holder.linearLayout.setBackground(R.drawable.chat);
        } else {
            holder.mView.setBackgroundColor(Color.WHITE);
            holder.mView.setPadding(0, 0, 20, 0);
            //holder.linearLayout.setBackground(R.drawable.chat_r);
        }
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
        public final View mView;
        public final TextView message;
        public final TextView time;
        public MessageItem mItem;

        private LinearLayout linearLayout;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            linearLayout = view.findViewById(R.id.thumbnail);
            message = view.findViewById(R.id.message);
            time = view.findViewById(R.id.time);
        }
    }

}
