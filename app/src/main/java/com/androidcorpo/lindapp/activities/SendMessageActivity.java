package com.androidcorpo.lindapp.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.androidcorpo.lindapp.LindAppUtils;
import com.androidcorpo.lindapp.R;

import java.io.IOException;

/**
 * Created by severin MBEKOU on 17-04-2020.
 */
public class SendMessageActivity extends AppCompatActivity {

    EditText number;
    ImageButton contacts;
    EditText message;
    Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        number = findViewById(R.id.number);
        contacts = findViewById(R.id.contacts);
        message = findViewById(R.id.message);
        send = findViewById(R.id.sim1);


        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, 1);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message.getText().toString().length() > 0) {
                    String plainText = message.getText().toString();
                    String phoneNumber = number.getText().toString();

                    try {
                        LindAppUtils.sendCypherMessage(getApplicationContext(), plainText, phoneNumber);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(getApplicationContext(), MessagesActivity.class);
                    finish();
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri contactData = data.getData();
        String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor cursor = getContentResolver().query(contactData, projection, null, null, null);
        if (cursor.moveToFirst()) {
            String numberValue = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
            number.setText(numberValue);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MessagesActivity.class);
        finish();
        startActivity(intent);
    }

}
