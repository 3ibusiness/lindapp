package com.androidcorpo.lindapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidcorpo.lindapp.R;
import com.androidcorpo.lindapp.adapter.MyMessagesRecyclerViewAdapter;
import com.androidcorpo.lindapp.model.MessageDetailContent;
import com.androidcorpo.lindapp.model.MessageDetailContent.MessageDetailItem;

public class MessagesFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    public MessagesFragment() {
    }

    public static MessagesFragment newInstance(int columnCount) {
        MessagesFragment fragment = new MessagesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_messages_list, container, false);
        final Context context = view.getContext();

        RecyclerView recyclerView = view.findViewById(R.id.list_main);

        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        MyMessagesRecyclerViewAdapter myMessagesRecyclerViewAdapter = new MyMessagesRecyclerViewAdapter(MessageDetailContent.ITEMS, mListener, context);
        recyclerView.setAdapter(myMessagesRecyclerViewAdapter);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(MessageDetailItem item);
    }
}
