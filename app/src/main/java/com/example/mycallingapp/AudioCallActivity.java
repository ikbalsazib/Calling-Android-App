package com.example.mycallingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

public class AudioCallActivity extends AppCompatActivity {
    SinchClient sinchClient;
    CallClient callClient;
    private TextView name, email;
    private Button testCall;
    private Bundle bundle;
    private Call call;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_call);

        name = findViewById(R.id.name_view);
        email = findViewById(R.id.email_view);
        testCall = findViewById(R.id.test_call_btn);

        bundle = getIntent().getExtras();

        String callerId = bundle.getString("callerId");

        String reciverId = "vKyNh8ZNnTV9IngKEBULWIdRWcg2";

        name.setText(bundle.getString("name"));
        email.setText(bundle.getString("email"));



        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .applicationKey("4f974d01-cdcc-4876-afb6-d1e9c48384b3")
                .applicationSecret("FHGWoPECd0qyiO45SXFSzA==")
                .environmentHost("clientapi.sinch.com")
                .userId(callerId)
                .build();

        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.start();
        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());

        testCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (call == null) {
                    call = sinchClient.getCallClient().callUser(reciverId);
                    call.addCallListener(new SinchCallListener());
                    testCall.setText("Hang Up");
                } else {
                    call.hangup();
                }
            }
        });


//        testCall.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                callClient = sinchClient.getCallClient();
//                callClient.callPhoneNumber("+8801648879969");
//                Toast.makeText(AudioCallActivity.this, "Calling..." + callerId, Toast.LENGTH_LONG).show();
//            }
//        });

    }


    private class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            call = null;
            SinchError a = endedCall.getDetails().getError();
            testCall.setText("Hang Up");
            Toast.makeText(AudioCallActivity.this, "Calling...", Toast.LENGTH_LONG).show();

            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        }

        @Override
        public void onCallEstablished(Call establishedCall) {
            Toast.makeText(AudioCallActivity.this, "Connected..", Toast.LENGTH_LONG).show();
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }

        @Override
        public void onCallProgressing(Call progressingCall) {
            Toast.makeText(AudioCallActivity.this, "Ringing.....", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
        }
    }

    private class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {
            call = incomingCall;
            Toast.makeText(getApplicationContext(), "incoming call", Toast.LENGTH_SHORT).show();
            call.answer();
            call.addCallListener(new SinchCallListener());
            testCall.setText("Hang Up");
        }


    }
}
