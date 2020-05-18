package com.androidcorpo.lindapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.androidcorpo.lindapp.Constant;
import com.androidcorpo.lindapp.LindAppUtils;
import com.androidcorpo.lindapp.R;
import com.androidcorpo.lindapp.elipticurve.EEC;
import com.androidcorpo.lindapp.model.MyKey;
import com.androidcorpo.lindapp.network.PostOnlinePublicKey;
import com.androidcorpo.lindapp.resources.LindAppDbHelper;

import java.io.IOException;
import java.security.KeyPair;

public class MainActivity extends AppCompatActivity {

    int progressbarstatus = 0;
    private Handler progressBarbHandler = new Handler();
    private String myNumber;
    private SharedPreferences pref;
    private LindAppDbHelper lindAppDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        pref = getApplicationContext().getSharedPreferences(Constant.PREFERENCE, 0); // 0 - for private mode
        setContentView(R.layout.activity_main);
        LinearLayout linearLayout = findViewById(R.id.linear);
        final ProgressBar pb = findViewById(R.id.progressBar);
        lindAppDbHelper = LindAppDbHelper.getInstance(this);

        if (pref.contains(Constant.MY_CONTACT)) {
            linearLayout.setVisibility(View.GONE);
            animateProgressBar(pb);
        } else {
            linearLayout.setVisibility(View.VISIBLE);
            pb.setVisibility(View.GONE);
            Button button = findViewById(R.id.save_contact);
            final EditText editText = findViewById(R.id.my_contact);
            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    myNumber = editText.getText().toString();

                    String cleanNumber = LindAppUtils.getCleanAdress(myNumber);

                    if (cleanNumber.isEmpty() || cleanNumber.length() < 8) {
                        editText.setError("Please enter you number");
                    } else {
                        MyKey savedKey = null;
                        try {
                            savedKey = lindAppDbHelper.findByContact(cleanNumber);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (savedKey == null) {
                            KeyPair keyPair = EEC.keyGeneration();
                            MyKey myKey = new MyKey(cleanNumber, keyPair.getPrivate(), keyPair.getPublic());
                            try {
                                lindAppDbHelper.saveKey(myKey);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            //DatatypeConverter.printBase64Binary(bytes);
                            new PostOnlinePublicKey().execute(myKey);

                        }
                        savePreference(cleanNumber);

                        // new GetOnlinePublicKey(lindAppDbHelper).execute("237677925286");
                        pb.setVisibility(View.VISIBLE);
                        animateProgressBar(pb);
                    }
                }
            });

        }

    }

    private void animateProgressBar(final ProgressBar pb) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressbarstatus < 100) {
                    progressbarstatus = progressbarstatus + 50;
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    progressBarbHandler.post(new Runnable() {

                        public void run() {
                            pb.setProgress(progressbarstatus);
                        }

                    });
                }
                if (progressbarstatus >= 100) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    finish();

                    Intent i = new Intent(MainActivity.this, MessagesActivity.class);
                    startActivity(i);
                }
            }
        }).start();
    }

    private void savePreference(String myContact) {

        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Constant.MY_CONTACT, myContact);
        editor.apply();
    }
}
