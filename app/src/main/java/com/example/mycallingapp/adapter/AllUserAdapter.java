package com.example.mycallingapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycallingapp.AudioCallActivity;
import com.example.mycallingapp.DashboardActivity;
import com.example.mycallingapp.MainActivity;
import com.example.mycallingapp.R;
import com.example.mycallingapp.model.User;

import java.util.ArrayList;

public class AllUserAdapter extends RecyclerView.Adapter<AllUserAdapter.AllUserViewHolder> {
    private Context context;
    private ArrayList<User> userArrayList;

    public AllUserAdapter(Context context, ArrayList<User> userArrayList) {
        this.context = context;
        this.userArrayList = userArrayList;
    }

    @NonNull
    @Override
    public AllUserAdapter.AllUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_user_list, parent, false);

        return new AllUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllUserAdapter.AllUserViewHolder holder, int position) {
        User user = userArrayList.get(position);
        holder.userName.setText(user.getName());
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class AllUserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        public TextView userName;
        public Button callBtn;

        public AllUserViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            userName = itemView.findViewById(R.id.userName);
            callBtn = itemView.findViewById(R.id.callBtn);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            User item = userArrayList.get(position);

            Intent intent = new Intent(itemView.getContext(), AudioCallActivity.class);
            intent.putExtra("name", item.getName());
            intent.putExtra("email", item.getEmail());
            intent.putExtra("callerId", item.getUserId());

            itemView.getContext().startActivity(intent);

        }
    }
}
