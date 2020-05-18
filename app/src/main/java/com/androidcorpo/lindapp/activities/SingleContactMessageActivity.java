package com.androidcorpo.lindapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidcorpo.lindapp.Constant;
import com.androidcorpo.lindapp.LindAppUtils;
import com.androidcorpo.lindapp.R;
import com.androidcorpo.lindapp.fragments.ContactMessagesFragment;
import com.androidcorpo.lindapp.model.Message;
import com.androidcorpo.lindapp.model.MessageContent;
import com.androidcorpo.lindapp.model.MessageContent.MessageItem;
import com.androidcorpo.lindapp.model.MessageDetailContent;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by severin MBEKOU on 17-04-2020.
 */
public class SingleContactMessageActivity extends AppCompatActivity implements ContactMessagesFragment.OnListFragmentInteractionListener, View.OnClickListener {

    EditText message;
    Button sim1;
    MessageDetailContent.MessageDetailItem dc = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_contact_messages);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String from = intent.getStringExtra(Constant.ADDRESS);
        String contactName = LindAppUtils.getContactName(getApplicationContext(), from);

        toolbar.setTitle(R.string.title_activity_single_contact_messages);
        toolbar.setSubtitle(String.format("With : %s", contactName != null ? contactName : from));

        dc = MessageDetailContent.ITEM_MAP.get(from);
        MessageContent.fillData(dc);
        message = findViewById(R.id.message);
        sim1 = findViewById(R.id.sim1);
        sim1.setOnClickListener(this);
        message.setFocusable(true);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ContactMessagesFragment fragment = new ContactMessagesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constant.FROM, from);
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.commit();

        message.setFocusable(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MessagesActivity.class);
        finish();
        startActivity(intent);
    }

    @Override
    public void onListFragmentInteraction(MessageItem item) {

    }

    @Override
    public void onClick(View v) {
        if (message.getText().toString().length() > 0) {
            SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            String plainText = message.getText().toString();
            String phoneNumber = dc.getNumberName();

            try {
                LindAppUtils.sendCypherMessage(getApplicationContext(), plainText, phoneNumber);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Message message = null;
            try {
                message = new Message(plainText, dtFormat.parse(dtFormat.format(new Date())), 0);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ArrayList<Message> arrayList = dc.getMessages();
            arrayList.add(arrayList.size(), message);

            MessageItem messageItem = new MessageItem(message.getMessage(), message.getTime(), message.getIsRorS());
            dc.setMessages(arrayList);
            MessageContent.ITEMS.add(MessageContent.ITEMS.size(), messageItem);
            MessageContent.ITEM_MAP.put(message.getTime(), messageItem);

            Intent intent = new Intent(this, MessagesActivity.class);
            finish();
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Please,type some text...!", Toast.LENGTH_SHORT).show();
        }
    }
}
