package com.androidcorpo.lindapp.activities;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.androidcorpo.lindapp.Constant;
import com.androidcorpo.lindapp.R;
import com.androidcorpo.lindapp.fragments.MessagesFragment;
import com.androidcorpo.lindapp.model.MessageDetailContent;

public class MessagesActivity extends AppCompatActivity implements MessagesFragment.OnListFragmentInteractionListener {

    private static MessagesActivity inst;

    public static MessagesActivity getInst() {
        return inst;
    }

    @Override
    protected void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_messages);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SendMessageActivity.class);
                startActivity(intent);
            }
        });

        Cursor messageInboxCursor = getContentResolver().query(Uri.parse("content://sms/"), null, null, null, null);
        MessageDetailContent.fillData(getApplicationContext(), messageInboxCursor);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MessagesFragment fragment = new MessagesFragment();
        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /*switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }*/
        return super.onOptionsItemSelected(item);
    }

    public void refreshActivity(String notifiacationTitle, String notificationMessage) {
        Notify(notifiacationTitle, notificationMessage);
        Intent intent = new Intent(this, MessagesActivity.class);
        finish();
        startActivity(intent);

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void Notify(String notifiacationTitle, String notificationMessage) {

        Intent intent = new Intent(this, com.androidcorpo.lindapp.activities.MessagesActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Notification notification = new Notification.Builder(this)
                .setContentTitle("New Message From " + notifiacationTitle)
                .setContentText(notificationMessage)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, notification);

    }

    @Override
    public void onListFragmentInteraction(MessageDetailContent.MessageDetailItem item) {

        Intent intent = new Intent(getApplicationContext(), SingleContactMessageActivity.class);
        intent.putExtra(Constant.ADDRESS, item.getNumberName());
        startActivity(intent);
    }
}
