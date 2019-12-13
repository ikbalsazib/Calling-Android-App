package com.example.mycallingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mycallingapp.adapter.AllUserAdapter;
import com.example.mycallingapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {
    SinchClient sinchClient;
    Call call;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    private RecyclerView recyclerView;
    private ArrayList<User> userArrayList;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        // Get All Users..
        fetchAllUser();

        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(firebaseUser.getUid())
                .applicationKey("FHGWoPECd0qyiO45SXFSzA==")
                .applicationSecret("4f974d01-cdcc-4876-afb6-d1e9c48384b3")
                .environmentHost("clientapi.sinch.com")
                .build();

        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.start();

        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());





//        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener() {
//
//        });
//        sinchClient.start();

    }

    private void fetchAllUser() {

//        userArrayList = new ArrayList<>();
//
//        for (int i = 0; i < 10; i++) {
//            User item = new User(
//                    "Item " + (i+1),
//                    "Demo@gmail.com",
//                    "Excellent",
//                    "kasjsa"
//            );
//            userArrayList.add(item);
//        }
//        adapter =new AllUserAdapter(this, userArrayList);
//        recyclerView.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userArrayList = new ArrayList<>();
                // userArrayList.clear();

                for (DataSnapshot dss:dataSnapshot.getChildren()) {
                    User user = dss.getValue(User.class);
                    userArrayList.add(user);
                }

               adapter = new AllUserAdapter(getApplicationContext(), userArrayList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DashboardActivity.this, "ERROR! " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private class SinchCallListener implements CallListener {

        @Override
        public void onCallProgressing(com.sinch.android.rtc.calling.Call call) {
            Toast.makeText(DashboardActivity.this, "Ringing..", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCallEstablished(com.sinch.android.rtc.calling.Call call) {
            Toast.makeText(DashboardActivity.this, "Call Established", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCallEnded(com.sinch.android.rtc.calling.Call endedCall) {
            Toast.makeText(DashboardActivity.this, "Call Ended", Toast.LENGTH_SHORT).show();
            call = null;
            endedCall.hangup();
        }

        @Override
        public void onShouldSendPushNotification(com.sinch.android.rtc.calling.Call call, List<PushPair> list) {
            // Toast.makeText(DashboardActivity.this, "Ringing..", Toast.LENGTH_SHORT).show();
        }
    }

    private class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient,final com.sinch.android.rtc.calling.Call incomingCall) {
            AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
            alertDialog.setTitle("Calling...");

            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Reject", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    incomingCall.hangup();
                }
            });

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Pick", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    incomingCall.answer();
                    incomingCall.addCallListener(new SinchCallListener());
                    Toast.makeText(DashboardActivity.this, "Call is started..", Toast.LENGTH_SHORT).show();

                }
            });

            alertDialog.show();
        }
    }



    public void callUser(User user) {
        if (call == null) {
            call = sinchClient.getCallClient().callUser(user.getUserId());
            call.addCallListener(new SinchCallListener());

            opencallerDialog(call);
        }

        if (call == null) {
            call = sinchClient.getCallClient().callUser(user.getUserId());
            call.addCallListener(new SinchCallListener());
            // button.setText("Hang Up");
        } else {
            call.hangup();
        }
        Toast.makeText(this, user.getUserId(), Toast.LENGTH_SHORT).show();
    }

    private void opencallerDialog(final Call call) {
        AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Calling..");

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Hang Up", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                call.hangup();
            }
        });

        alertDialog.show();
    }
}
