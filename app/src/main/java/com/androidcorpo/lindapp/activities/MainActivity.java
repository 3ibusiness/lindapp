package com.androidcorpo.lindapp.activities;

import android.content.Context;
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
import android.widget.Toast;

import com.androidcorpo.lindapp.Constant;
import com.androidcorpo.lindapp.LindAppUtils;
import com.androidcorpo.lindapp.R;
import com.androidcorpo.lindapp.elipticurve.EEC;
import com.androidcorpo.lindapp.model.MyKey;
import com.androidcorpo.lindapp.network.ApiClient;
import com.androidcorpo.lindapp.network.ApiInterface;
import com.androidcorpo.lindapp.network.response.PublicKeyResponse;
import com.androidcorpo.lindapp.resources.LindAppDbHelper;

import java.io.IOException;
import java.security.KeyPair;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    int progressbarstatus = 0;
    private Handler progressBarbHandler = new Handler();
    private String myNumber;
    private SharedPreferences pref;
    private LindAppDbHelper lindAppDbHelper;
    private ProgressBar pb;
    private Context context;
    private LinearLayout loginForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        pref = getApplicationContext().getSharedPreferences(Constant.PREFERENCE, 0); // 0 - for private mode
        context = this;
        setContentView(R.layout.activity_main);
        loginForm = findViewById(R.id.linear);
        pb = findViewById(R.id.progressBar);
        lindAppDbHelper = LindAppDbHelper.getInstance(this);
        Button button = findViewById(R.id.save_contact);
        final EditText editText = findViewById(R.id.my_contact);

        if (pref.contains(Constant.MY_CONTACT)) {
            loginForm.setVisibility(View.GONE);
            animateProgressBar(pb);
        } else {
            pb.setVisibility(View.GONE);
            loginForm.setVisibility(View.VISIBLE);
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
                            postPublicKey(myKey);
                        }

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

                    Intent i = new Intent(context, MessagesActivity.class);
                    startActivity(i);
                }
            }
        }).start();
    }

    private void postPublicKey(final MyKey key) {

        pb.setVisibility(View.VISIBLE);
        pb.setIndeterminate(true);

        byte[] bytes = LindAppUtils.publicKeyToStream(key.getPublicKey());

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<PublicKeyResponse> call = apiService.create(key.getContact(), EEC.bytesToHex(bytes));

        call.enqueue(new Callback<PublicKeyResponse>() {
            @Override
            public void onResponse(Call<PublicKeyResponse> call, Response<PublicKeyResponse> response) {
                pb.setIndeterminate(false);
                pb.setVisibility(View.INVISIBLE);
                PublicKeyResponse keyResponse = response.body();

                if (response.isSuccessful() && keyResponse.getCode() == 200) {
                    savePreference(key.getContact());
                    Toast.makeText(context, " public key shared ok!!", Toast.LENGTH_LONG).show();
                    finish();
                    Intent i = new Intent(context, MessagesActivity.class);
                    context.startActivity(i);
                } else
                    Toast.makeText(context, " Request post fail -- Public Key -- ", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<PublicKeyResponse> call, Throwable t) {
                Toast.makeText(context, " Failed to post public key make sure you are connected", Toast.LENGTH_LONG).show();
                loginForm.setVisibility(View.VISIBLE);
                pb.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void savePreference(String myContact) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Constant.MY_CONTACT, myContact);
        editor.apply();
    }

}
